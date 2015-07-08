package com.strato.publish.site;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.strato.publish.common.Marshaller;
import com.strato.publish.common.model.BundleInfo;
import com.strato.publish.common.model.BundlePathInfor;
import com.strato.publish.common.model.SiteInfo;


public class GenerateSiteXML {
	public static void main(String[] args) {
		String path = "D:\\repository";
		List<BundlePathInfor> files = JarDenpendencyFinder.filesfind(path,path);
		SiteInfo siteInfo = new SiteInfo();
		
		siteInfo.setName("leaddeal");
		siteInfo.setUrl("http://localhost:8888/id");
		List<BundleInfo> bundleList = JarDenpendencyFinder
				.denpendencyfind(files);
		System.out.println(bundleList.size());
		siteInfo.setBundleList(bundleList);
		File xmlFile = new File(path+"\\site.xml");
		Writer writer = null;
		try {
			writer = new FileWriter(xmlFile);
			Marshaller.getInstance().marshall(siteInfo, writer);	
//			Marshaller.getInstance().marshall(downloadBundleInfo, writer);
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
