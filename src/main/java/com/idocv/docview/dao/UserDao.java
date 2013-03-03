package com.idocv.docview.dao;

import com.idocv.docview.exception.DBException;
import com.idocv.docview.po.UserPo;

public interface UserDao {

	UserPo signUp(String appId, String username, String password, String email) throws DBException;

	String addSid(String uid) throws DBException;

	void logout(String sid) throws DBException;

	UserPo getByUsername(String username) throws DBException;

	UserPo getByEmail(String email) throws DBException;

	UserPo getBySid(String sid) throws DBException;
}