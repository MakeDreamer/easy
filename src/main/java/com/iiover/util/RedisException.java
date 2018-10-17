package com.iiover.util;

/**  
 * 与redis服务器交互发生的运行时异常
 */
public class RedisException extends RuntimeException {
	
	private static final long serialVersionUID = -5726542044619740046L;

	public RedisException(Throwable cause) {
		super("系统临时发生异常，请稍后再试一下", cause);
	}
	
	public RedisException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}