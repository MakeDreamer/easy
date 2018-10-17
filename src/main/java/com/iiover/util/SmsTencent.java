package com.iiover.util;
import com.aliyuncs.exceptions.ClientException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.iiover.common.entity.SmsTencentOnlyEntity;
import com.iiover.common.entity.SmsTencentTemplateEntity;
import net.sf.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName SmsTencent
 * @Descriptio TODO 腾讯云短信发送
 * @Author YuMing_Huai
 * @Date 2018/10/17 9:33
 * @Version 1.0
 **/
public class SmsTencent {

    private static final Logger logger = LoggerFactory.getLogger(SmsTencent.class);
    private static final String STRING_FILE = "sms_tencent.properties";

    private static Properties prop = new Properties();

    /**
     * @Description: 获取appid和key
     * @auther: YuMing_Huai
     * @date: 8:27 2018/10/17
     * @param: [key]
     * @return: java.lang.String
     */
    private static String getStringValueByKey(String key) {
        try {
            if (prop.isEmpty()) {
                prop.load(SmsTencent.class.getClassLoader().getResourceAsStream(STRING_FILE));
            }
            return prop.getProperty(key);
        } catch (Exception ex) {
            logger.error("Load properties file error.", ex);
            return "";
        }
    }
    /**
     *
     * @Description: 单发短信
     * 
     * @auther: YuMing_Huai
     * @date: 13:16 2018/10/17
     * @param: []
     * @return: boolean
     *
     */
    public boolean sendSms(SmsTencentOnlyEntity stoe) throws ClientException {
        // 短信应用SDK AppID
        int appid = Integer.parseInt(getStringValueByKey("appid"));

        // 短信应用SDK AppKey
        String appkey =getStringValueByKey("appkey");

        // 需要发送短信的手机号码
        String[] phoneNumbers = stoe.getPhoneNumbers();
        // 短信模板ID，需要在短信应用中申请
        int templateId = stoe.getTemplateId();

        // 签名
        String smsSign = stoe.getSmsSign();

        //短信内容
        String msg = stoe.getParams();

        //组装发送内容
        String params = "【"+smsSign+"】"+msg;

        // 单发短信
        try {
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.send(0, "86", phoneNumbers[0],
                    params, "", "");
            logger.info("send tencent sms result:"+result);

            JSONObject jsonObject = JSONObject.fromObject(result.toString());
            int code = jsonObject.getInt("result");

            if(code==0){
                return true;
            }else{
                return false;
            }
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
            return false;
        }

    }
    
    /**
     *
     * @Description: 指定模板ID单发短信
     * 
     * @auther: YuMing_Huai
     * @date: 14:04 2018/10/17
     * @param: [stte]
     * @return: boolean
     *
     */
    public boolean sendTemplateSms(SmsTencentTemplateEntity stte){
        // 短信应用SDK AppID
        int appid = Integer.parseInt(getStringValueByKey("appid"));

        // 短信应用SDK AppKey
        String appkey =getStringValueByKey("appkey");

        // 需要发送短信的手机号码
        String[] phoneNumbers = stte.getPhoneNumbers();

        // 短信模板ID，需要在短信应用中申请
        int templateId = stte.getTemplateId();

        // 签名
        String smsSign = stte.getSmsSign();

        try {
            String[] params = stte.getParams();
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    templateId, params, smsSign, "", "");

            logger.info("send tencent sms result:"+result);

            JSONObject jsonObject = JSONObject.fromObject(result.toString());
            int code = jsonObject.getInt("result");
         
            if(code==0){
                return true;
            }else{
                return false;
            }

        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
            return false;
        }

    }
}
