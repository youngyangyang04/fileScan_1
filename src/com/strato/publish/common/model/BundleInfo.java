package com.strato.publish.common.model;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2015/5/14.
 */
public class BundleInfo {
    private String name;
	private String version;
    private String url;

	private List<RequireBundleInfo> requireBundles=new ArrayList<RequireBundleInfo>();
    private List<PackageInfo> importPackages=new ArrayList<PackageInfo>();
    private List<PackageInfo> exportPackages=new ArrayList<PackageInfo>();
   
    public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBundleName() {
		return name;
	}
	public void setBundleName(String bundleName) {
		this.name = bundleName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String bundleVersion) {
		this.version = bundleVersion;
	}
	public List<PackageInfo> getImportPackages() {
		return importPackages;
	}
	public void setImportPackages(PackageInfo Packages) {
		importPackages.add(Packages);
	}
	public List<PackageInfo> getExportPackages() {
		return exportPackages;
	}
	public void setExportPackages(PackageInfo Packages) {
		exportPackages.add(Packages);
	}

	public void setRequireBundles(RequireBundleInfo Bundle) {
		requireBundles.add(Bundle);
	}
	public List<RequireBundleInfo> getRequireBundles() {
		return requireBundles;
	}
	public void setRequireBundles(List<RequireBundleInfo> requireBundles) {
		this.requireBundles = requireBundles;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BundleInfo other = (BundleInfo) obj;
		if(this.name.equals(other.name) && this.version.equals(other.version)){
			return true;
		}else {
			return false;
		}
	}
	
}
