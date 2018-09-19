package com.fc.common;

import java.io.Serializable;

public class BaseRsp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private boolean success = true;
	
	private String retCode;
	
	private String retMsg;
	
	private Object data;
	
	private Object value;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	
	public void success() {
		this.success = true;
	}
	
	public void fail(String retCode,String retMsg) {
		this.success = false;
		this.retCode = retCode;
		this.retMsg = retMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data ;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
}
