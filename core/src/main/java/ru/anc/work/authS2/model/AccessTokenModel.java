package ru.anc.work.authS2.model;

/**
 * Class that keeps access_token information / for SUDIR-2 integration
 * <p/>
 * @author chizhikova_aa
 * @since 2016
 */
public class AccessTokenModel {

	/**
	 * Access token storage time
	 */
	private int expires_in;

	/**
	 * Token scope
	 */
	private String scope;

	/**
	 * Access token
	 */
	private String access_token;

	/**
	 * Access token  type
	 */
	private String token_type;

	/**
	 * Access token to reentry
	 */
	private String refresh_token;


	/**
	 * Get access token storage time
	 * @return access token storage time
	 */
	public int getExpires_in() {
		return expires_in;
	}

	/**
	 * Set access token storage time
	 * @param expires_in access token storage time
	 */
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	/**
	 * Get token scope
	 * @return token scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Set token scope
	 * @param scope token scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Get access token
	 * @return access token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * Set access token
	 * @param access_token access token
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	/**
	 * Get access token type
	 * @return access token type
	 */
	public String getToken_type() {
		return token_type;
	}

	/**
	 * Set access token type
	 * @param token_type access token type
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	/**
	 * Get access token to reentry
	 * @return access token to reentry
	 */
	public String getRefresh_token() {
		return refresh_token;
	}

	/**
	 * Set access token to reentry
	 * @param refresh_token access token to reentry
	 */
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
}
