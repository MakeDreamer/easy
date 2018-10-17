package com.iiover.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
/**
 * 利用HttpClient进行post请求的工具类
 * @ClassName: HttpClientUtil 
 * @Description: TODO
 * @author
 * @date 2018年10月17日16:01:23
 *  
 */
public class HttpClientUtil {
    @SuppressWarnings("resource")
    public static String doPost(String url,String jsonstr,String charset){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity se = new StringEntity(jsonstr);
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
    
    @SuppressWarnings("resource")
    public static String doGet(String url,String jsonstr,String charset){
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpGet = new HttpGet(url);
            httpGet.addHeader("Content-Type", "application/json");
            StringEntity se = new StringEntity(jsonstr);
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
           // httpGet.setEntity(se);
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
}