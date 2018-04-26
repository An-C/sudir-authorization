package ru.anc.work.authS2.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.anc.work.authS2.service.Sudir2LogoutService;
import ru.anc.work.authentication.web.security.CookieAuthentificationService;
import ru.anc.work.components.util.StringUtils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Session ending for SUDIR + SUDIR-2 authorization system
 * <p/>
 * @author chizhikova_aa
 * @since 03.2016
 */
@Component
public class LogoutAction {

	/**
	 * Logging
	 */
	private final static Logger logger = LoggerFactory.getLogger(LogoutAction.class);

	/**
	 * SUDIR-2 authorization service
	 */
	@Autowired
	private Sudir2LogoutService sudir2LogoutService;

	/**
	 * Session ending for SUDIR + SUDIR-2 authorization system
	 * <p/>
	 * @return ActionSupport.SUCCESS
	 */
	public String execute() throws IOException {
		logger.trace("Method execute() started");

		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();

		String redirectUrl;

		try {
			//Get redirect url
			redirectUrl = sudir2LogoutService.doubleLogout(request, ServletActionContext.getResponse());
			if (StringUtils.nvl(request.getParameter("error")) != null) {
				redirectUrl += "?error=" + java.net.URLEncoder.encode(request.getParameter("error"), "UTF-8");
			}
			//Remove authorization Cookie
			CookieAuthentificationService.logout(request, ServletActionContext.getResponse());
		} catch (Exception e) {
			logger.error("Error during logout\n", e);
			throw new RuntimeException("Error during logout\n", e);
		}

		logger.debug("sendRedirect to " + redirectUrl);
		response.sendRedirect(redirectUrl);

		return ActionSupport.NONE;

	}

}
