package com.iiover.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SerializeUtils {

	private static Logger logger = Logger.getLogger(SerializeUtils.class);

	/**
	 * 序列化
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static byte[] serialize(Object object) throws Exception {
		if(object == null) return null;
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static Object unSerialize(byte[] bytes) throws Exception {
		if(bytes == null) return null;
		ByteArrayInputStream bais = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}
	}
	
	
    public static String writeObject(Object o) throws Exception {  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        ObjectOutputStream oos = new ObjectOutputStream(bos);  
        oos.writeObject(o);  
        oos.flush();  
        oos.close();  
        bos.close();  
        return new BASE64Encoder().encode(bos.toByteArray());  
    }  
  
    //反序列化String字符串为对象  
    public static Object readObject(String object) throws Exception{ 
    	if(object == null) return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(object));  
        ObjectInputStream ois = new ObjectInputStream(bis);  
        Object o = ois.readObject();  
        bis.close();   
        ois.close();  
        return o;  
   }  
}
