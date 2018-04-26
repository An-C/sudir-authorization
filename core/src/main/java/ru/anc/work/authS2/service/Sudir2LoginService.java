package ru.anc.work.authS2.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Service to deal with  SUDIR-2
 * <p/>
 * @author chizhikova_aa
 * @since 2016
 */

public interface Sudir2LoginService {

	/**
	 * Get user from SUDIR-2
	 * <p/>
	 * @param request  request
	 * @param response response
	 * @param code     Authorization code from SUDIR-2 service
	 * @return redirect url or null
	 */
	String sudir2Auth(HttpServletRequest request, HttpServletResponse response, String code) throws Exception;

}
