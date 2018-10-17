package com.iiover.common.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SmsAlibabaEntity
 * @Descriptio TODO
 * @Author YuMing_Huai
 * @Date 2018/10/17 8:45
 * @Version 1.0
 **/
@Data
public class SmsAlibabaEntity {
   //手机号码
    private String phoneNumber;

    //短信签名
    private String signName;

    //短信模板签名Code
    private String templateCode;

    //传递参数Map
    private HashMap<String,Object> templateParam;
}
