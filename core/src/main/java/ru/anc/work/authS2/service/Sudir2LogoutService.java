package ru.anc.work.authS2.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Service to end the session when working with authorization through SUDIR-2
 * or double authorization system (SUDIR + SUDIR-2)
 * <p/>
 * @author chizhikova_aa
 * @since 2016
 */

public interface Sudir2LogoutService {

	/**
	 * SUDIR-2 authorization type
	 */
	static final String SUDIR2_AUTH_TYPE = "SUDIR2";


	/**
	 * Session ending for SUDIR + SUDIR-2 authorization system
	 * <p/>
	 * @param request  request
	 * @param response response
	 * @return URL to exit system we use
	 */
	String doubleLogout(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
