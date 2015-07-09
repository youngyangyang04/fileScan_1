package com.strato.publish.site;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.strato.publish.common.Marshaller;
import com.strato.publish.common.model.BundleInfo;
import com.strato.publish.common.model.BundlePathInfor;
import com.strato.publish.common.model.DownloadBundleInfo;
import com.strato.publish.common.model.SiteInfo;
import com.strato.publish.common.model.UnitBundle;

public class GenerateNativeXML {
	public static void main(String[] args) {
		String path = "D:\\repository";
		List<BundlePathInfor> files = JarDenpendencyFinder.filesfind(path,path);
		SiteInfo siteInfo = new SiteInfo();
		
		siteInfo.setName("leaddeal");
		siteInfo.setUrl("http://localhost:8888/id");
		List<BundleInfo> bundleList = JarDenpendencyFinder
				.denpendencyfind(files);
		DownloadBundleInfo downloadBundleInfo = new DownloadBundleInfo();
		for(int i=0;i<bundleList.size();i++){
			UnitBundle unitBundle = new UnitBundle();
			unitBundle.setName(bundleList.get(i).getBundleName());
			unitBundle.setVersion(bundleList.get(i).getVersion());
			downloadBundleInfo.getBundleList().add(unitBundle);
		}
		File xmlFile = new File("D:\\nativeBundle.xml");
		Writer writer = null;
		try {
			writer = new FileWriter(xmlFile);	
			Marshaller.getInstance().marshellNative(downloadBundleInfo, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
}
