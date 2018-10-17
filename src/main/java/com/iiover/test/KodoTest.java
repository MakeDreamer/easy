package com.iiover.test;

import com.iiover.util.SmsAlibaba;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.storage.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName Kodotest
 * @Descriptio TODO
 * @Author YuMing_Huai
 * @Date 2018/10/17 16:10
 * @Version 1.0
 **/
public class KodoTest {

    private static final Logger logger = LoggerFactory.getLogger(KodoTest.class);
    private static final String STRING_FILE = "kodo.properties";

    private static Properties prop = new Properties();

    /**
     * @Description:
     * @auther: YuMing_Huai
     * @date: 8:27 2018/10/17
     * @param: [key]
     * @return: java.lang.String
     */
    public static String getStringValueByKey(String key) {
        try {
            if (prop.isEmpty()) {
                prop.load(SmsAlibaba.class.getClassLoader().getResourceAsStream(STRING_FILE));
            }
            return prop.getProperty(key);
        } catch (Exception ex) {
            logger.error("Load properties file error.", ex);
            return "";
        }
    }

    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = getStringValueByKey("ACCESS_KEY");
    String SECRET_KEY = getStringValueByKey("SECRET_KEY");
    //要上传的空间
    String bucketname = getStringValueByKey("bucketname");
    //上传到七牛后保存的文件名
    String key = "java.png";
    //上传文件的路径
    String FilePath = "D:/a.png";

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    ///////////////////////指定上传的Zone的信息//////////////////

    //要上传的空间(bucket)的存储区域为华东时
    Zone z = Zone.zone0();
    Configuration c = new Configuration(z);

    //创建上传对象
    UploadManager uploadManager = new UploadManager(c);

    public static void main(String args[]) throws IOException {
        new KodoTest().upload();
    }

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    public void upload() throws IOException {
        try {
            //调用put方法上传
            Response res = uploadManager.put(FilePath, key, getUpToken());
            //打印返回的信息
            System.out.println(res.bodyString());
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                //响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                //ignore
            }
        }
    }


}
