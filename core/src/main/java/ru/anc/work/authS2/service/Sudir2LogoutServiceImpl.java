package ru.anc.work.authS2.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.anc.work.authS2.dao.Sudir2UserDAO;
import ru.anc.work.authentication.web.security.AuthData;
import ru.anc.work.authentication.web.security.CookieAuthentificationService;
import ru.anc.work.configuration.ConfigProperties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Implementation for {@link ru.anc.work.authS2.service.Sudir2LoginService} interface
 * <p/>
 * @author chizhikova_aa
 * @since 2016
 */

@Component
public class Sudir2LogoutServiceImpl implements Sudir2LogoutService {

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
	public String doubleLogout(HttpServletRequest request, HttpServletResponse resp) throws Exception {
		logger.trace("Method doubleLogout() has started");
		String redirectUrl = ConfigProperties.getParameter("cas.server.login.url");
		try {
			//Authentication data from Cookie
			AuthData authData = CookieAuthentificationService.getAuthDataFromCookie(request);

			if (authData.getLogin() != null ) {
				//default redirectUrl; Default authorization type is SUDIR
				if (ConfigProperties.getBoolParameter("portal.sudir.enabled")) {
					redirectUrl = ConfigProperties.getParameter("portal.sudir.logout.url");
				}
				//Get user name
				String userName = authData.getLogin();
				logger.debug("Starting to logout user, userName from cookie: " + userName);

				//Get authorization type
				String authType = sudirUserDAO.getAuthType(userName);
				logger.debug("Authentication type for this user is " + (!StringUtils.hasText(authType) ? "not set" : authType));

				//If authorization type is  - SUDIR-2, change logout link
				if (authType != null && authType.equalsIgnoreCase(SUDIR2_AUTH_TYPE) && ConfigProperties.getBoolParameter("portal.sudir2.enabled")) {
					redirectUrl = ConfigProperties.getParameter("portal.sudir2.logout.url");
				}
				logger.debug("URL of redirection for logout is: " + redirectUrl);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot end user session", e);
		}
		return redirectUrl;
	}
}