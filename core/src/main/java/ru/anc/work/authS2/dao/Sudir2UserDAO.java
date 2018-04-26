package ru.anc.work.authS2.dao;

import ru.anc.work.authS2.model.Sudir2UserModel;

/**
 * Interface to deal with SudirUser
 * <p/>
 *
 * @author chizhikova_aa
 * @since 2016
 */

public interface Sudir2UserDAO {

	/**
	 * Checks is there user with such userName in USERINFO table
	 * <p/>
	 *
	 * @param userName user login
	 * @return true if there is such user in USERINFO table
	 */
	boolean isUndefinedUser(String userName) throws Exception;


	/**
	 * Saves user from SUDIR-2 to USERINFO table and returns user id
	 * <p/>
	 *
	 * @param userModel SUDIR-2 user model
	 * @return user identifier
	 */
	String createUserInfo(Sudir2UserModel userModel) throws Exception;

	/**
	 * Updates user from SUDIR-2 in USERINFO table and returns user id
	 * <p/>
	 *
	 * @param userModel SUDIR-2 user model
	 */
	void updateUserInfo(Sudir2UserModel userModel) throws Exception;

	/**
	 * Get authorization type
	 * Authorization type is a system in which this user is supposed to be authorized
	 * <p/>
	 *
	 * @param userName user name/login
	 * @return Authorization type or null if authorization type is not set
	 */
	String getAuthType(String userName) throws Exception;

	/**
	 * Set user to default user group (for PGU users)
	 * <p/>
	 *
	 * @param userId user identifier
	 * @param userGroupName group name
	 */
	void assignUserGroup(String userId, String userGroupName) throws Exception;

	/**
	 * Call SUDIR2-data updating procedure
	 *
	 * @param procName procedure name
	 * @param xml      XML text
	 * @param userName user login
	 */
	void callUpdateInfoProcedure(String procName, String xml, String userName) throws Exception;

}

