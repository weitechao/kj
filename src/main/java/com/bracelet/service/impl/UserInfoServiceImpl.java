package com.bracelet.service.impl;

import com.bracelet.entity.BindDevice;
import com.bracelet.entity.NotRegisterInfo;
import com.bracelet.entity.UserInfo;
import com.bracelet.service.IUserInfoService;
import com.bracelet.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Service
public class UserInfoServiceImpl implements IUserInfoService {
	@Autowired
	JdbcTemplate jdbcTemplate;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public boolean insert(String tel) {
		Timestamp now = Utils.getCurrentTimestamp();
		int i = jdbcTemplate
				.update("insert into user_info (username, password, createtime) values (?,?,?)",
						new Object[] { tel, "123456", now }, new int[] {
								Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP });
		return i == 1;
	}

	public BindDevice getBindInfoByImeiAndStatus(String imei, Integer status) {
		String sql = "select * from bind_device where  imei=? and status=? LIMIT 1";
		List<BindDevice> list = jdbcTemplate.query(sql, new Object[] { imei,
				status }, new BeanPropertyRowMapper<BindDevice>(
				BindDevice.class));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			logger.info("cannot find userinfo,imei:" + imei);
		}
		return null;
	}
	

	public boolean saveBindInfo(Long user_id, String imei, String name,
			Integer status) {
		Timestamp now = Utils.getCurrentTimestamp();
		int i = jdbcTemplate
				.update("insert into bind_device (user_id, imei, name,status,createtime) values (?,?,?,?,?)",
						new Object[] { user_id, imei, name, status, now },
						new int[] { Types.INTEGER, Types.VARCHAR,
								Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP });
		return i == 1;
	}

	public boolean bindDevice(Long user_id, String imei, Integer status,
			String name) {
		String sql = "select * from bind_device where user_id=? and imei=? and status=? LIMIT 1";
		List<BindDevice> list = jdbcTemplate.query(sql, new Object[] { user_id,
				imei, status }, new BeanPropertyRowMapper<BindDevice>(
				BindDevice.class));
		
		boolean bindSuccess = false;
		if (list != null && !list.isEmpty()) {
			logger.info("查询结果不为空已经绑定过=" + bindSuccess);
		} else {
			if (status == 1) {
				BindDevice info = getBindInfoByImeiAndStatus(imei, status);
				if (info != null) {
					logger.info("状态1=" + bindSuccess);
				} else {
					bindSuccess = this
							.saveBindInfo(user_id, imei, name, status);
				}
			} else {
				BindDevice info = getBindInfoByImeiAndStatus(imei, 1);
				bindSuccess = this.saveBindInfo(user_id, imei, info.getName(), 0);
				logger.info("status=" + status + "=" + bindSuccess);
			}
		}
		return bindSuccess;

	}

	public boolean unbindDevice(Long user_id, Integer id) {
		int i = jdbcTemplate.update(
				"delete from bind_device where id = ? and user_id = ?",
				new Object[] { id, user_id }, new int[] { Types.INTEGER,
						Types.INTEGER });
		return i == 1;
	}

	@Override
	public UserInfo getUserInfoByImei(String imei) {
		String sql = "select * from user_info where imei=? LIMIT 1";
		List<UserInfo> list = jdbcTemplate.query(sql, new Object[] { imei },
				new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			logger.info("cannot find userinfo,imei:" + imei);
		}
		return null;
	}

	@Override
	public UserInfo getUserInfoById(Long id) {
		String sql = "select * from user_info where user_id=? LIMIT 1";
		List<UserInfo> list = jdbcTemplate.query(sql, new Object[] { id },
				new BeanPropertyRowMapper<UserInfo>(UserInfo.class));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			logger.info("cannot find userinfo,id:" + id);
		}
		return null;
	}

	@Override
	public UserInfo getUserInfoByUsername(String username) {
		String sql = "select * from user_info where username=? LIMIT 1";
		List<UserInfo> list = jdbcTemplate.query(sql,
				new Object[] { username }, new BeanPropertyRowMapper<UserInfo>(
						UserInfo.class));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			logger.info("cannot find userinfo,username:" + username);
		}
		return null;
	}

	@Override
	public boolean saveUserPassword(Long user_id, String md5) {
		int i = jdbcTemplate.update(
				"update user_info set password=? where user_id = ?",
				new Object[] { md5, user_id }, new int[] { Types.VARCHAR,
						Types.INTEGER });
		return i == 1;
	}

	@Override
	public boolean updateUserInfo(Long user_id, String avatar, String nickname,
			Integer intSex, String weight, String height, String address) {
		int i = jdbcTemplate
				.update("update user_info set avatar=?,nickname=?,sex=?,weight=?,height=?,address=? where user_id = ?",
						new Object[] { avatar, nickname, intSex, weight,
								height, address, user_id }, new int[] {
								Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
								Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
								Types.INTEGER });
		return i == 1;
	}

	@Override
	public boolean saveUserInfo(String tel, String password, Integer type) {
		Timestamp now = Utils.getCurrentTimestamp();
		int i = jdbcTemplate
				.update("insert into user_info (username, password, createtime,type) values (?,?,?,?)",
						new Object[] { tel, password, now, type }, new int[] {
								Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP,
								Types.INTEGER });
		return i == 1;
	}

	@Override
	public List<BindDevice> getBindInfoById(Long user_id) {
		String sql = "select * from bind_device where user_id=?";
		List<BindDevice> list = jdbcTemplate.query(sql,
				new Object[] { user_id },
				new BeanPropertyRowMapper<BindDevice>(BindDevice.class));
		if (list != null && !list.isEmpty()) {
			return list;
		} else {
			logger.info("cannot find userinfo:");
		}
		return null;
	}

	@Override
	public List<BindDevice> getBindInfoByImei(String imei) {
		String sql = "select * from bind_device where imei=?";
		List<BindDevice> list = jdbcTemplate.query(sql, new Object[] { imei },
				new BeanPropertyRowMapper<BindDevice>(BindDevice.class));
		return list;
	}

	@Override
	public boolean updateUserPassword(String tel, String password) {
		int i = jdbcTemplate.update(
				"update user_info set password=? where username = ?",
				new Object[] { password, tel }, new int[] { Types.VARCHAR,
						Types.VARCHAR });
		return i == 1;
	}

	@Override
	public boolean insertNotRegistUser(String tel, String name, Long user_id,
			String imei) {
		Timestamp now = Utils.getCurrentTimestamp();
		int i = jdbcTemplate
				.update("insert into not_register_info (user_id, phone,name,imei,createtime) values (?,?,?,?,?)",
						new Object[] { user_id, tel, name, imei, now },
						new int[] { Types.INTEGER, Types.VARCHAR,
								Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP });
		return i == 1;
	}

	@Override
	public NotRegisterInfo getNotRegistUserIdByCondition(String tel,
			String name, Long user_id, String imei) {
		String sql = "select * from not_register_info where imei=? and user_id=? and phone=? and name= ? LIMIT 1";
		List<NotRegisterInfo> list = jdbcTemplate.query(sql, new Object[] {
				imei, user_id, tel, name },
				new BeanPropertyRowMapper<NotRegisterInfo>(
						NotRegisterInfo.class));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			logger.info("cannot find userinfo,imei:" + imei);
		}
		return null;
	}

	@Override
	public NotRegisterInfo getNotRegistUserIdByCondition(Long userid) {
		String sql = "select * from not_register_info where user_id=? LIMIT 1";
		List<NotRegisterInfo> list = jdbcTemplate.query(sql,
				new Object[] { userid },
				new BeanPropertyRowMapper<NotRegisterInfo>(
						NotRegisterInfo.class));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			logger.info("cannot find userinfo,id:" + userid);
		}
		return null;
	}

	@Override
	public boolean updateUserInfoHeadAndNickName(Long user_id, String nickname,
			String head) {
		int i = jdbcTemplate.update(
				"update user_info set nickname=?,head=? where user_id = ?",
				new Object[] { nickname, head, user_id }, new int[] {
						Types.VARCHAR, Types.VARCHAR, Types.INTEGER });
		return i == 1;
	}

	@Override
	public BindDevice getBindInfoByUserIdAndImei(Long user_id, String imei,
			Integer status) {
		String sql = "select * from bind_device where user_id=? and imei =? and status=? LIMIT 1";
		List<BindDevice> list = jdbcTemplate.query(sql, new Object[] { user_id,
				imei, status }, new BeanPropertyRowMapper<BindDevice>(
				BindDevice.class));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public boolean deleteByImei(String imei) {
		jdbcTemplate.update("delete from bind_device where   imei = ?",
				new Object[] { imei }, new int[] { Types.VARCHAR });
		return true;
	}

	@Override
	public boolean updatePwdAndType(String tel, String pwd, Integer type) {
		int i = jdbcTemplate.update(
				"update user_info set password=?,type=?  where username = ?",
				new Object[] { pwd, type, tel }, new int[] { Types.VARCHAR,
						Types.INTEGER, Types.VARCHAR });
		return i == 1;
	}

	@Override
	public boolean updateName(Long id, String name) {
		int i = jdbcTemplate.update(
				"update bind_device set name = ? where id = ?",
				new Object[] { name, id }, new int[] { Types.VARCHAR,
						Types.INTEGER });
		return i == 1;
	}

	@Override
	public BindDevice getBindInfoByImeiAndUserId(String imei, Long user_id) {
		String sql = "select * from bind_device where user_id=? and imei =?  LIMIT 1";
		List<BindDevice> list = jdbcTemplate.query(sql, new Object[] { user_id,
				imei}, new BeanPropertyRowMapper<BindDevice>(
				BindDevice.class));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public boolean saveUserInfo(String tel, String password, Integer type,
			String name) {
		Timestamp now = Utils.getCurrentTimestamp();
		int i = jdbcTemplate
				.update("insert into user_info (username, password, createtime,type,nickname) values (?,?,?,?,?)",
						new Object[] { tel, password, now, type,name}, new int[] {
								Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP,
								Types.INTEGER, Types.VARCHAR });
		return i == 1;
	}

	@Override
	public boolean updateUserInfoHeadAndNickName(Long user_id, String nickname) {
		int i = jdbcTemplate.update(
				"update user_info set nickname=? where user_id = ?",
				new Object[] { nickname, user_id }, new int[] {
						Types.VARCHAR, Types.INTEGER });
		return i == 1;
	}
}
