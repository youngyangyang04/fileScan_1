package com.strato.publish.common;

import java.io.Reader;
import java.io.Writer;

import com.strato.publish.common.model.BundleInfo;
import com.strato.publish.common.model.DownloadBundleInfo;
import com.strato.publish.common.model.PackageInfo;
import com.strato.publish.common.model.RequireBundleInfo;
import com.strato.publish.common.model.SiteInfo;
import com.strato.publish.common.model.UnitBundle;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.XStream;
public class Marshaller {

	private static Marshaller instance=new Marshaller();
	
	public static Marshaller getInstance(){
		return instance;
	}
	
	private XStream xstream=null;
	private XStream xstreamNative=null;
	private Marshaller(){
		init();
	}
	
	private void init() {
		xstream= new XStream(new DomDriver());
		xstream.alias("site", SiteInfo.class);
		xstream.alias("bundleInfo", BundleInfo.class);
		xstream.alias("Bundle", RequireBundleInfo.class);
		xstream.alias("packageInfo", PackageInfo.class);
		xstreamNative = new XStream(new DomDriver());
		xstreamNative.alias("bundleSet", DownloadBundleInfo.class);
		xstreamNative.alias("bundleInfo", UnitBundle.class);
	}

	public void marshall(Object o,Writer writer){
		xstream.toXML(o, writer);
	}
	
	public Object unmarshall(Reader reader){
		return xstream.fromXML(reader);
	}
	public void marshellNative(Object o,Writer writer){
		xstreamNative.toXML(o, writer);
	}
	public Object unmarshallNative(Reader reader){
		return xstreamNative.fromXML(reader);
	}
	
}
