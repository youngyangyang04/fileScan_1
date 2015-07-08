package com.strato.publish.collector;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.strato.publish.common.Marshaller;
import com.strato.publish.common.model.BundleInfo;
import com.strato.publish.common.model.SiteInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

@SuppressWarnings("deprecation")
public class HttpDownload {

    public static final int cache = 10 * 1024;
    public static final boolean isWindows;
    public static final String splash;
    public static final String root;
    static {
        if (System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().contains("windows")) {
            isWindows = true;
            splash = "\\";
            root="D:";
        } else {
            isWindows = false;
            splash = "/";
            root="/search";
        }
    }

    /**
     * 根据url下载文件，文件名从response header头中获取 
     * @param url
     * @return
     */
 //   public static String download(String url) {
//        return download(url, null);
//    }

    /**
     * 根据url下载文件，保存到filepath中 
     * @param url is bundle url
     * @param filepath is native download path
     * @return
     */
    @SuppressWarnings("resource")
	public static void download(String url, String filepath) {
//    	System.out.println(url+"      "+filepath);
        try {
            HttpClient client = new DefaultHttpClient();
//        	CloseableHttpClient client = HttpClient.createDefault();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = client.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            if (filepath == null)
                filepath = getFilePath(response);
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            FileOutputStream fileout = new FileOutputStream(file);
            /**
             * 根据实际运行效果 设置缓冲区大小 
             */
            byte[] buffer=new byte[cache];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer,0,ch);
            }
            fileout.flush();
            fileout.close();
            
            is.close();
            fileout.flush();
            fileout.close();
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("resource")
	public static InputStream download(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
//        	CloseableHttpClient client = HttpClient.createDefault();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = client.execute(httpget);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            return is;
//            if (filepath == null)
//                filepath = getFilePath(response);
//            File file = new File(filepath);
//            file.getParentFile().mkdirs();
//            FileOutputStream fileout = new FileOutputStream(file);
//            /**
//             * 根据实际运行效果 设置缓冲区大小 
//             */
//            byte[] buffer=new byte[cache];
//            int ch = 0;
//            while ((ch = is.read(buffer)) != -1) {
//                fileout.write(buffer,0,ch);
//            }
//            fileout.flush();
//            fileout.close();
//            
//            is.close();
//            fileout.flush();
 //           fileout.close();
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取response要下载的文件的默认路径 
     * @param response
     * @return
     */
    public static String getFilePath(HttpResponse response) {
        String filepath = root + splash;
        String filename = getFileName(response);

        if (filename != null) {
            filepath += filename;
        } else {
            filepath += getRandomFileName();
        }
        return filepath;
    }
    /**
     * 获取response header中Content-Disposition中的filename值 
     * @param response
     * @return
     */
    public static String getFileName(HttpResponse response) {
        Header contentHeader = response.getFirstHeader("Content-Disposition");
        String filename = null;
        if (contentHeader != null) {
            HeaderElement[] values = contentHeader.getElements();
            if (values.length == 1) {
                NameValuePair param = values[0].getParameterByName("filename");
                if (param != null) {
                    try {
                        //filename = new String(param.getValue().toString().getBytes(), "utf-8");  
                        //filename=URLDecoder.decode(param.getValue(),"utf-8");  
                        filename = param.getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filename;
    }
    /**
     * 获取随机文件名 
     * @return
     */
    public static String getRandomFileName() {
        return String.valueOf(System.currentTimeMillis());
    }
    public static void outHeaders(HttpResponse response) {
        Header[] headers = response.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i]);
        }
    }
    public static void main(String[] args) {
        String url="http://img.alicdn.com/imgextra/i2/2364196519/TB253lncVXXXXadXXXXXXXXXXXX_!!2364196519.jpg_430x430q90.jpg";
        String filepath = "D:\\test\\a.jpg";
        HttpDownload.download(url, filepath);
        File xmlFile = new File("D:\\xmlFile.xml");
        Reader reader =null;
        SiteInfo siteInfo = new SiteInfo();
        try {
			reader = new FileReader(xmlFile);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(reader!=null){
				try {
					 
					siteInfo=(SiteInfo)Marshaller.getInstance().unmarshall(reader);
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
       
		List<BundleInfo> testList = siteInfo.getBundleList();
		for(int i=0;i<testList.size();i++){
			System.out.println(testList.get(i).getBundleName());
		}
        
    }
}  