package com.idocv.docview.service;

import com.idocv.docview.exception.DocServiceException;
import com.idocv.docview.po.AppPo;

public interface AppService {

	boolean add(String id, String name, String key, String phone) throws DocServiceException;
	
	AppPo getByKey(String key) throws DocServiceException;

}