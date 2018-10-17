package com.iiover.test;

import com.aliyuncs.exceptions.ClientException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsStatusPuller;
import com.github.qcloudsms.SmsMobileStatusPuller;
import com.github.qcloudsms.SmsStatusPullCallbackResult;
import com.github.qcloudsms.SmsStatusPullReplyResult;
import com.github.qcloudsms.SmsVoiceVerifyCodeSender;
import com.github.qcloudsms.SmsVoiceVerifyCodeSenderResult;
import com.github.qcloudsms.SmsVoicePromptSender;
import com.github.qcloudsms.SmsVoicePromptSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import com.iiover.common.entity.SmsTencentOnlyEntity;
import com.iiover.common.entity.SmsTencentTemplateEntity;
import com.iiover.util.SmsTencent;
import org.json.JSONException;

import java.io.IOException;


import java.io.IOException;

/**
 * @ClassName SmsTencentTest
 * @Descriptio TODO
 * @Author YuMing_Huai
 * @Date 2018/10/17 10:19
 * @Version 1.0
 **/
public class SmsTencentTest {
    public static void main(String[] args) throws ClientException {
        SmsTencent st = new SmsTencent();

        //sendTemplateSms 短信发送
//        SmsTencentTemplateEntity s = new SmsTencentTemplateEntity();
//        String[] phone = {"1886******"};
//        s.setPhoneNumbers(phone);
//        s.setTemplateId(123456);
//        s.setSmsSign("智慧");
//        String[] params = {"897665"};
//        s.setParams(params);
//
//
//        System.out.println(st.sendTemplateSms(s));


        //sendSms
        SmsTencentOnlyEntity stoe = new SmsTencentOnlyEntity();
        String[] phone = {"188*******"};
        stoe.setPhoneNumbers(phone);
        stoe.setTemplateId(212111);
        stoe.setSmsSign("智慧");
        stoe.setParams("亲爱的同学,你的登录密码已被重置,如非本人操作,请立即联系我们!");


        boolean b = st.sendSms(stoe);
        System.out.println(b);


    }
}
