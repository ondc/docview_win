package com.idocv.docview.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.idocv.docview.dao.AppDao;
import com.idocv.docview.exception.DBException;
import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;
import com.idocv.docview.service.AppService;

@Service
public class AppServiceImpl implements AppService {

	private static final Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);
	
	@Resource
	private AppDao appDao;

	@Override
	public boolean add(String id, String name, String key, String phone, String email) throws DocServiceException {
		try {
			return appDao.add(id, name, key, phone, email);
		} catch (DBException e) {
			logger.error("Add app error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public AppPo get(String id) throws DocServiceException {
		try {
			return appDao.get(id);
		} catch (DBException e) {
			logger.error("get error: ", e);
			throw new DocServiceException(e);
		}
	}

	@Override
	public AppPo getByKey(String key) throws DocServiceException {
		try {
			return appDao.getByToken(key);
		} catch (DBException e) {
			logger.error("getByKey error: ", e);
			throw new DocServiceException(e);
		}
	}
}
