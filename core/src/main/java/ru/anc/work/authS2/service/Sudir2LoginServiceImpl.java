package ru.anc.work.authS2.service;

import com.google.common.base.Strings;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.anc.work.authS2.dao.Sudir2UserDAO;
import ru.anc.work.authS2.model.AccessTokenModel;
import ru.anc.work.authS2.model.Sudir2UserModel;
import ru.anc.work.authentication.web.security.CookieAuthentificationService;
import ru.anc.work.components.util.HttpUtils;
import ru.anc.work.configuration.ConfigProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;


/**
 * Implementation for {@link Sudir2LoginService} interface
 * <p/>
 *
 * @author chizhikova_aa
 * @since 2016
 */

@Component
public class Sudir2LoginServiceImpl implements Sudir2LoginService {

	/**
	 * Logging
	 */
	private final static Logger logger = LoggerFactory.getLogger(Sudir2LoginService.class);

	/**
	 * DAO to deal with user
	 */
	@Autowired
	private Sudir2UserDAO sudirUserDAO;


	@Override
	public String sudir2Auth(HttpServletRequest request, HttpServletResponse resp, String code) throws Exception {
		String userName;

		try {
			if (StringUtils.hasText(code)) {
				logger.debug("SUDIR-2 The authorization code is received, start the user authentication");

				//Get parameters from configuration to receive token
				String clientId = ConfigProperties.getParameter("portal.sudir2.clientId");
				String clientSecret = ConfigProperties.getParameter("portal.sudir2.clientSecret");
				String tokenUrl = ConfigProperties.getParameter("portal.sudir2.tokenUrl");
				String aISUrl = ConfigProperties.getParameter("portal.sudir2.aISUrl");

				//Prepare parameter string
				String postParameters = new StringBuffer()
						.append("redirect_uri=").append(aISUrl)
						.append("&grant_type=").append("authorization_code")
						.append("&code=").append(code)
						.append("&client_secret=").append(clientSecret)
						.append("&client_id=").append(clientId)
						.toString();


				//Receive token
				AccessTokenModel tokenItem = getAccessToken(tokenUrl, postParameters);
				String accessToken = tokenItem.getAccess_token();
				if (!StringUtils.hasText(accessToken)) {
					throw new RuntimeException("SUDIR-2 service returned empty accessToken!");
				}
				//Receive user
				String userInfoUrl = ConfigProperties.getParameter("portal.sudir2.userInfoUrl");
				Sudir2UserModel sudirUser = getUserModel(userInfoUrl + accessToken);

				//Actions with user
				userName = sudirUser.getGuid();
				if (Strings.isNullOrEmpty(userName)) {
					throw new RuntimeException("SUDIR-2 service returned empty user guid");
				} else {
					updateUserInfo(sudirUser);
					callUpdateInfoProcedure(sudirUser);
					//Authorize user
					try {
						//To support authorization from another domain, use the configuration portal.sudir2.auth.domain
						String domain = Strings.isNullOrEmpty(ConfigProperties.getParameter("portal.sudir2.auth.domain"))
								? request.getServerName()
								: ConfigProperties.getParameter("portal.sudir2.auth.domain");
						CookieAuthentificationService.login(domain, resp, userName);
						logger.debug("SUDIR-2 User " + userName + " is authorized successfully");
					} catch (Exception e) {
						throw new RuntimeException("SUDIR-2 Cannot set cookie for user = " + userName);
					}
				}
			} else {
				throw new RuntimeException("Authorization code from SUDIR-2 service has not been received");
			}
		} catch (Exception e) {
			throw new RuntimeException("SUDIR-2 Authentication failed", e);
		}
		return userName;
	}

	/**
	 * Call user data updating procedure
	 * @param userModel SUDIR user model
	 * @throws Exception exceptions
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void callUpdateInfoProcedure(Sudir2UserModel userModel) throws Exception {
		logger.debug("SUDIR-2 Call user data updating procedure");
		//it is critical for the procedure that the user already exists in the database, because it is called in a separate transaction
		if (!Strings.isNullOrEmpty(ConfigProperties.getParameter("portal.sudir2.update.procedure"))) {
			sudirUserDAO.callUpdateInfoProcedure(ConfigProperties.getParameter("portal.sudir2.update.procedure"),
					getXmlFromSudir2UserModel(userModel), userModel.getGuid());
		}
	}

	/**
	 * Update SUDIR-2 user information
	 *
	 * @param sudirUser SUDIR-2 user
	 * @throws Exception exceptions
	 */
	@Transactional
	private void updateUserInfo(Sudir2UserModel sudirUser) throws Exception {
		String userName = sudirUser.getGuid();
		try {
			//If user does not exist, add this user to USERINFO
			if (sudirUserDAO.isUndefinedUser(userName)) {
				logger.debug("User " + userName + " not found. Save user to the system");
				String commonPass = ConfigProperties.getParameter("ru.anc.work.hdgate.sdou.newuser.password");
				sudirUser.setPassword(commonPass);
				String userId = sudirUserDAO.createUserInfo(sudirUser);

				//Add user to the user group
				String defaultGroup = ConfigProperties.getParameter("portal.sudir2.defaultGroup");
				if (!Strings.isNullOrEmpty(defaultGroup)) {
					logger.debug("Add user  " + userName + " to " + defaultGroup + "group");
					sudirUserDAO.assignUserGroup(userId, defaultGroup);
				}

			} else {
				logger.debug("User " + userName + " already registered. Update user data");
				sudirUserDAO.updateUserInfo(sudirUser);
			}
		} catch (Exception e) {
			throw new RuntimeException("SUDIR-2 User update failed " + userName, e);
		}
	}

	/**
	 * Get xml from SUDIR user data model
	 * @param userModel SUDIR user data model
	 * @return xml
	 * @throws Exception exceptions
	 */
	private String getXmlFromSudir2UserModel(Sudir2UserModel userModel) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(Sudir2UserModel.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		//jaxbMarshaller.marshal(userModel, System.out);
		StringWriter sw = new StringWriter();
		jaxbMarshaller.marshal(userModel, sw);
		return sw.toString();
	}

	/**
	 * Fill user model
	 * <p/>
	 *
	 * @param url request address
	 * @return user model
	 */
	private Sudir2UserModel getUserModel(String url) throws Exception {
		logger.debug("User data receiving: " + url);
		try {
			String userJson = HttpUtils.sendGet(url);
			logger.debug("User information from SUDIR-2 service\n" + userJson);
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(userJson, Sudir2UserModel.class);
		} catch (Exception e) {
			throw new RuntimeException("Cannot get user information from SUDIR-2 service\n", e);
		}
	}

	/**
	 * Fill access token model
	 * <p/>
	 *
	 * @param url request address
	 * @param urlParameters URL parameters
	 * @return model with access token parameters
	 */
	private AccessTokenModel getAccessToken(String url, String urlParameters) throws Exception {
		logger.debug("Receiving access_token: " + url);
		logger.debug("POST-request parameters: " + urlParameters);
		try {
			String tokenJson = HttpUtils.sendPost(url, urlParameters, "application/x-www-form-urlencoded");
			logger.debug("Responce with access_token from SUDIR-2 service:\n" + tokenJson);
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(tokenJson, AccessTokenModel.class);
		} catch (Exception e) {
			throw new RuntimeException("Receiving of access_token from SUDIR-2 is failed\n", e);
		}

	}
}