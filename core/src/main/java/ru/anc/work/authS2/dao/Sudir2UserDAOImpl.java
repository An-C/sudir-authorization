package ru.anc.work.authS2.dao;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.anc.work.components.util.StringUtils;
import ru.anc.work.authS2.model.Sudir2UserModel;
import ru.anc.work.authS2.service.Sudir2LogoutService;
import ru.anc.work.authentication.utils.PortalPasswordEncoder;

import javax.annotation.Resource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Implementation of {@link Sudir2UserDAO} to deal with SUDIR-2 users
 *
 * @author chizhikova_aa
 */

@Repository
public class Sudir2UserDAOImpl implements Sudir2UserDAO {

	/**
	 * Logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(Sudir2UserDAOImpl.class);

	/**
	 * JDBC-template to deal with "users" scheme
	 */
	@Resource(name = "usersJdbcTemplate")
	protected JdbcTemplate jdbcTemplate;


	@Override
	public boolean isUndefinedUser(String userName) throws Exception {
		logger.trace("Method isUndefinedUser started with userName=" + userName);
		if (Strings.isNullOrEmpty(userName)) {
			throw new IllegalArgumentException("userName is empty");
		}

		String sql = "select count(*) from userinfo where upper(username) = upper(?) and hide=0";
		Object[] params = new Object[]{userName};
		try {
			return jdbcTemplate.queryForObject(sql, params, Integer.class) == 0;
		} catch (Exception e) {
			logger.error("Failed to execute sql:\n" + sql + "\nParams=" + Arrays.toString(params), e);
			throw new RuntimeException("Failed to execute sql:\n" + sql + "\nParams=" + e);
		}
	}


	@Override
	public String getAuthType(String userName) throws Exception {
		logger.trace("Method getAuthType() has started");
		String sql = "select auth_type from userinfo where upper(username) = upper(?) and hide=0";
		Object[] params = new Object[]{userName};
		try {
			return jdbcTemplate.queryForObject(sql, params, String.class);
		} catch (Exception e) {
			logger.error("Failed to execute sql:\n" + sql + "\nParams=" + Arrays.toString(params), e);
			throw new RuntimeException("Failed to execute sql:\n" + sql + "\nParams=" + e);
		}

	}


	@Override
	public String createUserInfo(final Sudir2UserModel userModel) throws Exception {
		logger.trace("Method createUserInfo() has started");
		if (userModel == null) {
			throw new IllegalArgumentException("SudirUserModel is empty");
		}

		final String sql = "insert into users.userinfo (ID, USERNAME, FIO, PWD, EMAIL, PHONE, AUTH_TYPE) values (sys_guid(), ?,?,?,?,?,?)";

		PortalPasswordEncoder encoder = new PortalPasswordEncoder("MD5");
		final String[] params = new String[]{
				userModel.getGuid()
				, StringUtils.asSentence(userModel.getLastName()) +
				" " + StringUtils.asSentence(userModel.getFirstName()) +
				" " + StringUtils.asSentence(userModel.getMiddleName())
				, encoder.encode(userModel.getGuid() + PortalPasswordEncoder.MD5_SALT + userModel.getPassword())
				, userModel.getMail()
				, userModel.getPhone()
				, Sudir2LogoutService.SUDIR2_AUTH_TYPE
		};

		String userId = null;

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
					ps.setString(1, params[0]);
					ps.setString(2, params[1]);
					ps.setString(3, params[2]);
					ps.setString(4, params[3]);
					ps.setString(5, params[4]);
					ps.setString(6, params[5]);
					return ps;
				}
			}, keyHolder);

			userId = String.valueOf(keyHolder.getKeys().get("id"));

		} catch (Exception e) {
			logger.error("Failed to execute sql:\n" + sql + "\nParams=" + Arrays.toString(params), e);
			throw new RuntimeException("Failed to execute sql:\n" + sql + "\nParams=" + Arrays.toString(params), e);
		}



		return userId;
	}


	@Override
	public void updateUserInfo(final Sudir2UserModel userModel) throws Exception {
		logger.trace("Method updateUserInfo() has started");
		if (userModel == null) {
			throw new IllegalArgumentException("SudirUserModel is empty");
		}

		final String sql = "update users.userinfo set FIO=?, EMAIL=?, PHONE=?, AUTH_TYPE=? where USERNAME = UPPER(?)";

		PortalPasswordEncoder encoder = new PortalPasswordEncoder("MD5");
		final String[] params = new String[]{
				StringUtils.asSentence(userModel.getLastName()) +
						" " + StringUtils.asSentence(userModel.getFirstName()) +
						" " + StringUtils.asSentence(userModel.getMiddleName())
				, userModel.getMail()
				, userModel.getPhone()
				, Sudir2LogoutService.SUDIR2_AUTH_TYPE
				, userModel.getGuid()
		};

		try {
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setString(1, params[0]);
					ps.setString(2, params[1]);
					ps.setString(3, params[2]);
					ps.setString(4, params[3]);
					ps.setString(5, params[4]);
					return ps;
				}
			});


		} catch (Exception e) {
			logger.error("Failed to execute sql:\n" + sql + "\nParams=" + Arrays.toString(params), e);
			throw new RuntimeException("Failed to execute sql:\n" + sql + "\nParams=" + Arrays.toString(params), e);
		}

	}

	@Override
	public void assignUserGroup(String userId, String userGroupName) throws IllegalArgumentException {
		logger.trace("Method assignUserGroup() has started");
		if (!StringUtils.hasText(userId)) {
			throw new IllegalArgumentException("Cannot assign a UserGroup, userId is empty");
		}

		//check that default group exists and get its id
		String groupId = null;
		final String defaultGroupExistQuery = "select a.id from USERGROUP a where a.name = ?";
		final String[] defaultGroupExistQueryParams = new String[]{userGroupName};
		try {
			groupId = jdbcTemplate.queryForObject(defaultGroupExistQuery, defaultGroupExistQueryParams, java.lang.String.class);
			if (!StringUtils.hasText(groupId)) {
				throw new RuntimeException("Группа по-умолчанию : \n" + userGroupName + "\nне существует!");
			}
		} catch (Exception e) {
			logger.error("Failed to execute sql:\n" + defaultGroupExistQuery + "\nParams=" + Arrays.toString(defaultGroupExistQueryParams), e);
			throw new RuntimeException("Failed to execute sql:\n" + defaultGroupExistQuery + "\nParams=" + Arrays.toString(defaultGroupExistQueryParams), e);
		}

		final String sql = "insert into userinfo_usergroup values (?,?)";
		final String[] params = new String[]{userId, groupId};

		try {
			jdbcTemplate.update(sql, params);
		} catch (Exception e) {
			logger.error("Failed to execute sql:\n" + sql + "\nParams=" + Arrays.toString(params), e);
			throw new RuntimeException("Failed to execute sql:\n" + sql + "\nParams=" + Arrays.toString(params), e);
		}
	}

	@Override
	public void callUpdateInfoProcedure(final String procName, final String xml, final String userName) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("{call ").append(procName).append("(p_xml => ?, p_loginname => ?)}");
		try {
			jdbcTemplate.execute(sql.toString(), new CallableStatementCallback<Object>() {
				@Override
				public Object doInCallableStatement(CallableStatement cs)
						throws SQLException, DataAccessException {
					cs.setString(1, xml);
					cs.setString(2, userName);
					cs.execute();
					return null;
				}
			});
		} catch (Exception e) {
			logger.error("Failed to execute sql:\n" + sql + "\n; xml=" + xml + "; username = " + userName, e);
			throw new RuntimeException("Failed to execute sql:\n" + sql + "\n; xml=" + xml + "; username = " + userName, e);
		}
	}
}
