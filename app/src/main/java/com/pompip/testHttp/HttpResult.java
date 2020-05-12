package com.pompip.testHttp;

public class HttpResult {
	public int responseCode;
	public byte[] responseEntity;
	public HttpResult(int respCode, byte[] respEntity){
		this.responseCode = respCode;
		this.responseEntity = respEntity;
	}

	@Override
	public String toString() {
		return "HttpResult{" +
				"responseCode=" + responseCode +
				", responseEntity=" + (responseEntity == null ? "null" : responseEntity.length) +
				'}';
	}
}
