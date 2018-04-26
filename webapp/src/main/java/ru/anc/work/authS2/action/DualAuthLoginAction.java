package ru.anc.work.authS2.action;

import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.anc.work.authentication.web.security.CookieAuthentificationService;
import ru.anc.work.components.dao.CommonDAO;
import ru.anc.work.configuration.ConfigProperties;

import java.net.URLEncoder;
import java.util.HashMap;


/**
 * Double-authorization page
 * <p/>
 * @author chizhikova_aa
 * @since 2016
 */

@Component
public class DualAuthLoginAction extends ActionSupport {

	/**
	 * Logging
	 */
	private final static Logger logger = LoggerFactory.getLogger(DualAuthLoginAction.class);

	/**
	 * DAO with common functionality
	 */
	@Autowired
	private CommonDAO commonDAO;

	/**
	 * Authorization page content, from xslt-transformation
	 */
	private String pageContent;

	/**
	 * Redirect URL after successful authorization
	 */
	private String service;

	/**
	 * Show login page for double authorization
	 * <p/>
	 * @return action execution result
	 */
	public String execute() {
		logger.trace("Method execute() started");

		try {
			String sudir1RedirectUrl = ConfigProperties.getParameter("portal.sudir.login.page");
			String sudir2RedirectUrl = ConfigProperties.getParameter("portal.sudir2.login.redirect");

			if (!StringUtils.hasText(sudir1RedirectUrl)) {
				logger.error("Authorization page URL is not set for SUDIR: portal.sudir.login.page");
				throw new RuntimeException("Authorization page URL is not set for SUDIR: portal.sudir.login.page");
			}

			if (!StringUtils.hasText(sudir2RedirectUrl)) {
				logger.error("Authorization page URL is not set for SUDIR-2: portal.sudir2.login.redirect");
				throw new RuntimeException("Authorization page URL is not set for SUDIR-2: portal.sudir2.login.redirect");
			}

			if (StringUtils.hasText(service)){
				service = service.replaceAll("&", CookieAuthentificationService.URL_PARAMETER_SEPARATOR);
				sudir1RedirectUrl += "?afterLoginUrl=" + URLEncoder.encode(service, "UTF-8");
			}

			HashMap<String,String[]> sessionParams = new HashMap<String,String[]>();
			sessionParams.put("sudir1RedirectUrl", new String[] { sudir1RedirectUrl });
			sessionParams.put("sudir2RedirectUrl", new String[] { sudir2RedirectUrl });

			pageContent = commonDAO.getXSLTPage("CARD$AUTH", "1", sessionParams);
		} catch (Exception e) {
			logger.error("Error when retrieving parameters to the authorization page", e);
			throw new RuntimeException("Error when retrieving parameters to the authorization page", e);
		}
		return ActionSupport.SUCCESS;

	}

	/**
	 * Get page content
	 * @return page content
	 */
	public String getPageContent() {
		return pageContent;
	}

	/**
	 * Set redirect URL after successful authorization
	 * @param service redirect URL after successful authorization
	 */
	public void setService(String service) {
		this.service = service;
	}
}
