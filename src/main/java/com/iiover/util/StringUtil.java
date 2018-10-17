package com.iiover.util;

import java.util.UUID;

/**
 * @Title: StringUtil
 * @Description:  String工具类
 * @author YuMing_Huai
 * @date 2018年3月29日 下午1:03:39
 * @版本 V 1.0 
 */
public class StringUtil {
	/**
	 * 判断是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		return str==null||"".equals(str.trim())?true:false;
	}
	
	/**
	 *  去除空格
	  * Title: trimStr 
	  * Description: TODO
	  * Param @param str
	  * Param @return
	  * Return String
	 */
	public static String trimStr(String str){
		if(str == null || "".equals(str)){
			return "";
		}
		return str.replaceAll("\\s", "");
	}
	
	/** 
     * Title:String 
     * @Description: TODO 
     * @Param: 
     * @Return: common uuid  
     * @throws:  
     * @author:YuMing 
     * @Date:2017-12-28上午9:42:42 
     * @return 
     */  
    public static String uuid(){  
        return UUID.randomUUID().toString();  
    }  
      
      
    /** 
     * Title:String 
     * @Description: TODO 
     * @Param: 
     * @Return:replace - uuid 
     * @throws:  
     * @author:YuMing 
     * @Date:2017-12-28上午9:43:51 
     * @return 
     */  
    public static String uuidReplace(){  
        return UUID.randomUUID().toString().replace("-", "");  
    }

}




