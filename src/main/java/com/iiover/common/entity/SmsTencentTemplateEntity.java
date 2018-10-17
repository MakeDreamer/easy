package com.iiover.common.entity;

import lombok.Data;

/**
 * @ClassName SmsTencentTemplateEntity
 * @Descriptio TODO
 * @Author YuMing_Huai
 * @Date 2018/10/17 13:28
 * @Version 1.0
 **/
@Data
public class SmsTencentTemplateEntity {
    //号码
    private String[] phoneNumbers;

    //模板ID
    private int templateId;

    //签名 非签名ID
    private String smsSign;

    //参数
    private String[] params;


}
