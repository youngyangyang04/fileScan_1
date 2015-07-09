package com.strato.publish.site;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import com.strato.publish.collector.HttpDownload;
import com.strato.publish.common.Marshaller;
import com.strato.publish.common.model.*;


public class ScanSitesXML {
	private static Properties properties = new Properties();
	private ScanSitesXML(){
		
	}
	/**
	 * put all bundleInfo into HashMap<String, TreeMap<VersionInfo, BundleInfo>>
	 * @param propertiesName
	 * @return all bundleInfo into HashMap<String, TreeMap<VersionInfo, BundleInfo>> 
	 */
	private static  HashMap<String, TreeMap<VersionInfo, BundleInfo>> getAllBundleInfo(String propertiesName, HashMap<BundleInfo, String> findWebURL){
		HashMap<String, TreeMap<VersionInfo, BundleInfo>> allBundleInfo = new HashMap<>();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertiesName);
			properties.load(inputStream);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(inputStream!=null){
				try{
					inputStream.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
				
		}		
		Enumeration<?> names = properties.propertyNames();		
		while(names.hasMoreElements()){
			String filePath= (String) names.nextElement();
			String url= properties.getProperty(filePath);
			InputStream is=HttpDownload.download(url+"/site.xml");
			SiteInfo siteInfo = new SiteInfo();
		    Reader reader =null;		        
			reader = new InputStreamReader(is);
			siteInfo=(SiteInfo)Marshaller.getInstance().unmarshall(reader);
			if(reader!=null){
				try {
					
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
						       				
			List<BundleInfo> bundleInfoList = siteInfo.getBundleList();			

		//	String tempString = null;
			for(int i=0;i<bundleInfoList.size();i++){			
				TreeMap<VersionInfo, BundleInfo> treeMap = new TreeMap<>();
				VersionInfo versionInfo = tranVersionInfo(bundleInfoList.get(i).getVersion());			
		//		tempString=siteInfo.getUrl().concat(bundleInfoList.get(i).getUrl());
		//		bundleInfoList.get(i).setUrl(bundleInfoList.get(i).getUrl());
//				System.out.println(bundleInfoList.get(i).getUrl());
				treeMap.put(versionInfo, bundleInfoList.get(i));
				allBundleInfo.put(bundleInfoList.get(i).getBundleName(), treeMap);
				findWebURL.put(bundleInfoList.get(i), siteInfo.getUrl());
				System.out.println(bundleInfoList.get(i).getBundleName()+"####"+bundleInfoList.get(i).getUrl());
			}
				
		}
		return allBundleInfo;
	}
    /**
     * get native bundlInfo and put it into siteInfo
     * @param nativeXMLFile
     * @return
     */
	private static DownloadBundleInfo getNativeInfo(String nativeXMLFile) {
		 File xmlFile = new File(nativeXMLFile);
		 Reader reader = null;
		 try {
			reader = new FileReader(xmlFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		 DownloadBundleInfo siteInfo = (DownloadBundleInfo) Marshaller.getInstance().unmarshallNative(reader);
		
		return siteInfo;
		
	}
	private static  void findBundleRequire(Set<BundleInfo> downloadSet,BundleInfo b,HashMap<String, TreeMap<VersionInfo, BundleInfo>> allBundleInfo){
		List<RequireBundleInfo> list = b.getRequireBundles();
		RequireBundleInfo requireBundleInfo = new RequireBundleInfo();
		for(int i=0;i<list.size();i++){
			requireBundleInfo = list.get(i);
			VersionInfo versionInfo = tranVersionInfo(requireBundleInfo.getVersion());
			TreeMap<VersionInfo, BundleInfo> treeMap = allBundleInfo.get(requireBundleInfo.getName());
			if(treeMap==null)
				return;
			else {
				BundleInfo bundleInfo = treeMap.get(versionInfo);
				if(bundleInfo==null)
					return;
				else{
					downloadSet.add(bundleInfo);
					findBundleRequire(downloadSet, bundleInfo, allBundleInfo);
				}
			}
			
		}
		return ;
	}
	private static void findBundleImport(Set<BundleInfo> downloadSet,BundleInfo b,HashMap<String, TreeMap<VersionInfo, BundleInfo>> allBundleInfo,HashMap<PackageInfo, BundleInfo> map) {
		List<PackageInfo> list = b.getImportPackages();
		for(int i=0;i<list.size();i++){
			PackageInfo packageInfo = list.get(i);
			BundleInfo bundleInfo = map.get(packageInfo);
			if(bundleInfo!=null){
				downloadSet.add(bundleInfo);
			}
			else{
				
			}
		}
		return ;

	}
	/**
	 * find the bundle URL in allBundleinfo
	 * @param bundleName
	 * @param version
	 * @param allBundleinfo
	 * @return
	 */
	private static void findBundle(Set<BundleInfo> downloadSet,String bundleName,String version,HashMap<String, TreeMap<VersionInfo, BundleInfo>> allBundleinfo,HashMap<PackageInfo, BundleInfo> map){
		VersionInfo versionInfo = tranVersionInfo(version);
		TreeMap<VersionInfo, BundleInfo> mapTreeMap = allBundleinfo.get(bundleName);
		BundleInfo b = new BundleInfo();
		if(mapTreeMap==null||mapTreeMap.isEmpty()){
			//TODO return null;
		}
		if(version==null){
			downloadSet.add(mapTreeMap.values().iterator().next());
		}else{
			b = mapTreeMap.get(versionInfo);
			if(b!=null){
				downloadSet.add(b);
			}
		}
		
		//根据require-bundle拿所有依赖的bundle
		findBundleRequire(downloadSet, b, allBundleinfo);
		//根据import-package拿所有依赖的bundle
		findBundleImport(downloadSet, b, allBundleinfo, map);
//		String urlString = allBundleinfo.get(bundleName).get(versionInfo).getUrl();
//		return urlString;
	}
	private static void download(HashMap<String, TreeMap<VersionInfo, BundleInfo>> allBundleinfo,String downloadPath,Set<BundleInfo> downloadSet, HashMap<BundleInfo, String> findWebURL,boolean flatOrNot){
		Iterator<BundleInfo> iterator = downloadSet.iterator();
		while(iterator.hasNext()){
			BundleInfo bundleInfo = iterator.next();
			
		//	VersionInfo versionInfo = tranVersionInfo(bundleInfo.getVersion());
			String relativePathString = bundleInfo.getUrl().replace('/', '\\');
	//		System.out.println(downloadPath+relativePathString);
			System.out.println(bundleInfo.getBundleName()+" "+downloadPath);
		//	System.out.println(bundleInfo.getBundleName()+" "+allBundleinfo.get(bundleInfo.getBundleName()).get(tranVersionInfo(bundleInfo.getVersion())).getUrl());
			if(flatOrNot == false)
				HttpDownload.download(findWebURL.get(bundleInfo)+bundleInfo.getUrl(),downloadPath+relativePathString);
			else{
				HttpDownload.download(findWebURL.get(bundleInfo)+bundleInfo.getUrl(),downloadPath+"\\"+bundleInfo.getBundleName()+".jar");
			}
		}
	}
	/**
	 * download bundle 
	 * @param allBundleinfo
	 * @param nativeSiteInfo
	 * @param downloadPath
	 */
	private static void downloadBundle(HashMap<String, TreeMap<VersionInfo, BundleInfo>> allBundleinfo,DownloadBundleInfo nativeSiteInfo,String downloadPath,HashMap<PackageInfo, BundleInfo> map, HashMap<BundleInfo, String> findWebURL,boolean flatOrNot){
		List<UnitBundle> nativeBundleList = nativeSiteInfo.getBundleList();
//		String url = null;
		Set<BundleInfo> downloadSet = new HashSet<BundleInfo>();
		for(int i=0;i<nativeBundleList.size();i++){
		//	System.out.println(nativeBundleList.get(i).getBundleName());
			findBundle(downloadSet,nativeBundleList.get(i).getName(),nativeBundleList.get(i).getVersion(),allBundleinfo,map);						
		}
		download(allBundleinfo,downloadPath,downloadSet,findWebURL,flatOrNot);
//		HttpDownload.download(url,downloadPath+nativeBundleList.get(i).getUrl());
		
	}
	/**
	 * turn string version into VersionInfo
	 * @param version
	 * @return
	 */
	private static VersionInfo tranVersionInfo(String version){
		 String[] subVersion =version.split("\\.");
		 int size = subVersion.length;
		 int first=0,second=0,third=0;
		 String last=null;
//		System.out.println(size);
		if(size==4){
			first = Integer.parseInt(subVersion[0]);
			second = Integer.parseInt(subVersion[1]);
			third = Integer.parseInt(subVersion[2]);
			last = subVersion[3];
		}
		else if(size==3){
			first = Integer.parseInt(subVersion[0]);
			second = Integer.parseInt(subVersion[1]);
			third = Integer.parseInt(subVersion[2]);
			last = null;
		}else if(size==2){
			first = Integer.parseInt(subVersion[0]);
			second = Integer.parseInt(subVersion[1]);
			third = 0;
			last = null;
		}
		else if(size==1){
			first = Integer.parseInt(subVersion[0]);
			second = 0;
			third = 0;
			last = null;
		}
		VersionInfo versionInfo = new VersionInfo(first, second, third, last);
		return versionInfo;
	}
	private static void initExportpackageMap( HashMap<String, TreeMap<VersionInfo, BundleInfo>> allBundleinfo,HashMap<PackageInfo, BundleInfo> map){
		Iterator<?> iterator = allBundleinfo.entrySet().iterator();
		while(iterator.hasNext()){
			@SuppressWarnings("unchecked")
			Map.Entry<String,TreeMap<VersionInfo, BundleInfo>> entry = (Entry<String, TreeMap<VersionInfo, BundleInfo>>) iterator.next();
			TreeMap<VersionInfo, BundleInfo> treeMap = entry.getValue();
			Iterator<?> iteratorTreemap = treeMap.entrySet().iterator();
			while(iteratorTreemap.hasNext()){
				@SuppressWarnings("unchecked")
				Map.Entry<VersionInfo,BundleInfo> entryTreeMap = (Entry<VersionInfo, BundleInfo>) iteratorTreemap.next();
				BundleInfo bundleInfo = (BundleInfo)entryTreeMap.getValue();
				List<PackageInfo> list = bundleInfo.getExportPackages();
				for(int i=0;i<list.size();i++){
					PackageInfo packageInfo = list.get(i);
					map.put(packageInfo, bundleInfo);
				}
				
			}
		}
		
	}
	/**
	 * pay attention to that the bundle name should be with version because the hashmap
	 * for example st.business.admin.base.v1_0_0
	 * @param args
	 */
	public static void main(String[] args) {
		 boolean flatOrNot = false; 
		 String propertiesFilename="D:\\site.properties";
		 String nativeXMLFile = "D:\\nativeBundle.xml";
		 String downloadPath = "D:\\test1";
		 HashMap<BundleInfo, String> findWebURL = new HashMap<>();
		 HashMap<PackageInfo, BundleInfo> map = new HashMap<PackageInfo, BundleInfo>();		 
		 System.out.println("asdfg");
		 HashMap<String, TreeMap<VersionInfo, BundleInfo>> allBundleinfo = ScanSitesXML.getAllBundleInfo(propertiesFilename,findWebURL);		
		 initExportpackageMap(allBundleinfo, map);		
//		 System.out.println(allBundleinfo.get("究极测试："+"framework.saas.bootstrap.v1_0_0").get("1.0.0").getUrl());
		 DownloadBundleInfo nativeSiteInfo = getNativeInfo(nativeXMLFile);	
		 downloadBundle(allBundleinfo, nativeSiteInfo,downloadPath,map,findWebURL,flatOrNot);
	}
}
