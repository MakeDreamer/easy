package com.iiover.common.entity;

import lombok.Data;

/**
 * @ClassName SmsTencentOnlyEntity
 * @Descriptio TODO
 * @Author YuMing_Huai
 * @Date 2018/10/17 15:46
 * @Version 1.0
 **/
@Data
public class SmsTencentOnlyEntity {

    //号码
    private String[] phoneNumbers;

    //模板ID
    private int templateId;

    //签名 非签名ID
    private String smsSign;

    //参数
    private String params;
}
