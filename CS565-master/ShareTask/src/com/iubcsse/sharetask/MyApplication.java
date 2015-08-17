package com.iubcsse.sharetask;

import android.app.Application;

public class MyApplication extends Application {
	
	private String testSystemDate = "";
	private Boolean useAdvancedDate = false;

	public String getTestSystemDate() {
		return testSystemDate;
	}

	public void setTestSystemDate(String testSystemDate) {
		this.testSystemDate = testSystemDate;
	}

	public Boolean getUseAdvancedDate() {
		return useAdvancedDate;
	}

	public void setUseAdvancedDate(Boolean useAdvancedDate) {
		this.useAdvancedDate = useAdvancedDate;
	}

}
