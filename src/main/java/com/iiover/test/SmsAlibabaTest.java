package com.iiover.test;
import java.util.HashMap;
import java.util.Map;

import com.aliyuncs.exceptions.ClientException;
import com.iiover.common.entity.SmsAlibabaEntity;
import com.iiover.util.SmsAlibaba;

/**
 * @ClassName SmsAlibaba
 * @Descriptio TODO
 * @Author YuMing_Huai
 * @Date 2018/10/17 9:14
 * @Version 1.0
 **/
public class SmsAlibabaTest {
    public static void main(String[] args) {
        SmsAlibaba sa = new SmsAlibaba();
        SmsAlibabaEntity sae =  new SmsAlibabaEntity();
        sae.setPhoneNumber("156******");
        sae.setSignName("阿里云");
        sae.setTemplateCode("SMS_119080***");
        HashMap paramMap = new HashMap<String,Object>();
        paramMap.put("name","小阿草");
        paramMap.put("type","注册");
        paramMap.put("code","866816");

        sae.setTemplateParam(paramMap);

        try {
            boolean res = sa.send(sae);
            System.out.println("Res:"+res);
        } catch (ClientException e) {
            e.printStackTrace();
        }


    }
}
