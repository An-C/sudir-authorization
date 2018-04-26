package ru.anc.work.authS2.action;

import com.google.common.base.Strings;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.anc.work.authS2.service.Sudir2LoginService;
import ru.anc.work.configuration.ConfigProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * SUDIR-2 authorization
 * <p/>
 * @author chizhikova_aa
 * @since 2016
 */

@Component
public class LoginAction extends ActionSupport {

	/**
	 * Logging
	 */
	private final static Logger logger = LoggerFactory.getLogger(LoginAction.class);

	/**
	 * Session name to save "Redirect URL"
	 */
	private static final String AFTER_LOGIN_URL_SESSION_NAME = "afterLoginUrl";

	/**
	 * SUDIR-2 authorization service
	 */
	@Autowired
	private Sudir2LoginService sudirLoginService;

	/**
	 * Redirect URL
	 */
	private String afterLoginUrl;

	/**
	 * SUDIR-2 authorization code
	 */
	private String code;

	/**
	 * SUDIR-2 authorization
	 * <p/>
	 * @return action execution result
	 */
	public String execute() throws IOException {
		logger.trace("Method execute() started");

		HttpServletResponse response = ServletActionContext.getResponse();
		HttpServletRequest request = ServletActionContext.getRequest();

		String redirectUrl;

		if (StringUtils.hasText(code)) {
			try {
				sudirLoginService.sudir2Auth(request, ServletActionContext.getResponse(), code);

				afterLoginUrl = (String) request.getSession().getAttribute(AFTER_LOGIN_URL_SESSION_NAME);
				if (!Strings.isNullOrEmpty(afterLoginUrl)) {
					request.getSession().removeAttribute(AFTER_LOGIN_URL_SESSION_NAME);
					redirectUrl =  afterLoginUrl;
				} else {
					redirectUrl = ConfigProperties.getParameter("portal.sudir2.login.success");
				}
			} catch (Exception e) {
				logger.error("SUDIR-2 Authorization error\n", e);
				redirectUrl = ConfigProperties.getParameter("portal.sudir2.logout.url");
				//If you throw the exception, the redirect is still happening
				//throw new RuntimeException("SUDIR-2 Ошибка авторизации\n", e);
			}
		} else {
			redirectUrl = ConfigProperties.getParameter("portal.sudir2.login.redirect");
			request.getSession().setAttribute(AFTER_LOGIN_URL_SESSION_NAME, afterLoginUrl);
		}

		logger.debug("sendRedirect to " + redirectUrl);
		response.sendRedirect(redirectUrl);

		return ActionSupport.NONE;

	}

	/**
	 * Set redirect URL after successful authorization
	 * @param afterLoginUrl redirect URL after successful authorization
	 */
	public void setAfterLoginUrl(String afterLoginUrl) {
		this.afterLoginUrl = afterLoginUrl;
	}

	/**
	 * Set authorization code from SUDIR-2 service
	 * @param code authorization code from SUDIR-2 service
	 */
	public void setCode(String code) {
		this.code = code;
	}
}
