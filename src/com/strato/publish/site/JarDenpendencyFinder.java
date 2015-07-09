
package com.strato.publish.site;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import com.strato.publish.common.model.BundleInfo;
import com.strato.publish.common.model.BundlePathInfor;
import com.strato.publish.common.model.PackageInfo;
import com.strato.publish.common.model.RequireBundleInfo;

/**
 * Created by Administrator on 2015/5/14.
 */
public class JarDenpendencyFinder {
    private JarDenpendencyFinder(){
        
    }
    public static List<BundleInfo> denpendencyfind(List<BundlePathInfor> files){
        ArrayList<BundleInfo> jarDenpendencyies=new ArrayList<>();
        int flag=0;
        for (int j = 0 ,isize=files.size(); j < isize; j++) {
        	flag=0;
            BundleInfo jarDenpendency=new BundleInfo();
            jarDenpendency.setUrl(files.get(j).getPath());
            try {
                @SuppressWarnings("resource")
				JarFile jarFile = new JarFile(files.get(j).getFile());
                jarFile.getManifest();
                Attributes attributesMap=jarFile.getManifest().getMainAttributes();
                Set<Object> key=attributesMap.keySet();
   //             jarFile.getManifest().getEntries();
                if(!key.isEmpty()){
                    Iterator<?> iterator=attributesMap.entrySet().iterator();
                    while(iterator.hasNext()){
                        @SuppressWarnings("unchecked")
						Map.Entry<Object, Object> entry= (Map.Entry<Object, Object>) iterator.next();
                        if (entry.getKey().toString().equals("Bundle-Name"))
                        {
                            String BundleName = entry.getValue().toString();
                            jarDenpendency.setBundleName(BundleName);
//                            System.out.println("key:"+entry.getKey()+" value  "+ BundleName);
                        }
                        else if (entry.getKey().toString().equals("Bundle-Version")){

                            String BundleVersion = entry.getValue().toString();
                            jarDenpendency.setVersion(BundleVersion);
                            //System.out.println("key:"+entry.getKey()+" value  "+BundleVersion);
                        }
                        else if (entry.getKey().toString().equals("Export-Package")) {
                        	flag=1;
                            String[] temp = entry.getValue().toString().split(",");
                            
                            for (int i = 0; i < temp.length; i++) {
                            	PackageInfo packageInfo = new PackageInfo();
                            	packageInfo.setName(temp[i]);
                            	packageInfo.setVersion(jarDenpendency.getVersion());              
                               jarDenpendency.setExportPackages(packageInfo);
                             
//                                System.out.println("key:"+entry.getKey()+"name "+temp[i]+"version"+jarDenpendency.getBundleVersion());
                            }
                        }
                        else if (entry.getKey().toString().equals("Import-Package")) {
                        	
                            String[] temp = entry.getValue().toString().split(",");
                            String[] temp1 ,temp2 ;
                            for (int i = 0; i < temp.length; i++) {
                            	 PackageInfo PackageInfo=new PackageInfo();
                            	temp1 = temp[i].split(";");
                            	if(temp1.length==2){
//	                            	System.out.println("+++++" + temp1[1] + temp1[1].length());
                            		temp2 = temp1[1].split("="); 
//	                            	System.out.println("-----" + temp2[1]);
	                            	temp2[0] = temp2[1].substring(1, temp2[1].length()-1);
//	                            	System.out.println("{{{{{" + temp2[0]);
	                            	
	                                 PackageInfo.setName(temp1[0]);
	                                 PackageInfo.setVersion(temp2[0]);
                            	}
                            	else{
                            		 
                                     PackageInfo.setName(temp1[0]);
                                     PackageInfo.setVersion("*");
                            	}
                               
                                
                                jarDenpendency.setImportPackages(PackageInfo);
                                //  System.out.println("key:"+entry.getKey()+" value  "+temp[i]);
                            }
                        }
                        else if (entry.getKey().toString().equals("Require-Bundle")) {
                            String[] temp = entry.getValue().toString().split(",");
//                            System.out.println("test2:"+temp[0]);
                            //direct=temp.length;
                           // System.out.println(direct);
                            for (int i = 0; i < temp.length; i++) {
                                String[] temp1=temp[i].split(";");
                                RequireBundleInfo requireBundle=new RequireBundleInfo();
                                if (temp1.length==2) {
                                    String  Version=temp1[1].substring(16,temp1[1].length()-1);
                                    requireBundle.setName(temp1[0]);
                                    requireBundle.setVersion(Version);
 //                                   System.out.println("test:"+temp1[0]+Version);
 //                                   System.out.println("test1:"+requireBundle.getName()+requireBundle.getVersion());
                                    jarDenpendency.setRequireBundles(requireBundle);
                                }
                                else {
                                    requireBundle.setName(temp1[0]);
                                    requireBundle.setVersion("all");
                                   
                                    jarDenpendency.setRequireBundles(requireBundle);
                                }
                            }
                        }

                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(flag==0){
            	jarDenpendency.setExportPackages(null);
            }
            jarDenpendencyies.add(jarDenpendency);
        }
       return jarDenpendencyies;
    }
    public static List<BundlePathInfor> filesfind(String path,String originalPath){
        List<BundlePathInfor> files=new LinkedList<>() ;
        File textFolder =new File(path);
        File[] file =textFolder.listFiles();;
        int isize=file.length;
        String tempString = null;
       for (int i = 0; i <isize; i++) {
    	   
           if (file[i].isDirectory()){
               //System.out.println(file[i]);
        	   //file[i].getPath();       	  
               files.addAll(filesfind(file[i].toString(),originalPath));
           }
           else if (file[i].getName().endsWith(".jar")){
        	   BundlePathInfor bundlePathInfor = new  BundlePathInfor();      	  
      
               tempString=  file[i].getAbsolutePath();
               int len=originalPath.length();
               tempString = tempString.substring(len);
               tempString=tempString.replace('\\','/');
               bundlePathInfor.setFile(file[i]);
        	   bundlePathInfor.setPath(tempString);
        	   files.add(bundlePathInfor);
        	   System.out.println(bundlePathInfor.getFile().getName()+"****"+tempString);
           }
        }
        return files;
    }
}
