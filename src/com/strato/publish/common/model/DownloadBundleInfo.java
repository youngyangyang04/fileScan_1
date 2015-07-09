package com.strato.publish.common.model;

import java.util.ArrayList;
import java.util.List;

public class DownloadBundleInfo {
	private List<UnitBundle> bundleList = new ArrayList<>();

	public List<UnitBundle> getBundleList() {
		return bundleList;
	}

	public void setBundleList(List<UnitBundle> bundleList) {
		this.bundleList = bundleList;
	}

	
}
