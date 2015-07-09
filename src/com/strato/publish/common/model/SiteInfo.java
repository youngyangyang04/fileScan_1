package com.strato.publish.common.model;

import java.util.ArrayList;
import java.util.List;

public class SiteInfo {
	private String name;
	
	private String url;

	private List<BundleInfo> bundleList = new ArrayList<BundleInfo>();

	public List<BundleInfo> getBundleList() {
		return bundleList;
	}

	public void setBundleList(List<BundleInfo> bundleList) {
		this.bundleList = bundleList;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
//	static{
//		Properties properties=new Properties();
//		properties.load(reader);
//		
//	}
	
}
