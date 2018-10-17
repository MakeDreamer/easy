package com.iiover.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * Redis缓存工具类<br/><br/>
 * 本类中的所有redis操作方法在与redis服务器进行交互的过程中，都有可能抛出运行时异常（如网络中断、网络读取超时、协议解析错误等），
 * 以便能正确地告知业务代码调用方缓存操作的结果，业务代码调用方需要正确理解异常情况的出现
 * 
 */
public class RedisUtils {

	private static Logger logger = Logger.getLogger(RedisUtils.class);

	/** 默认缓存时间：一小时 */
	public static final int DEFAULT_CACHE_SECONDS = 3600;// 单位秒 设置成一个小时
	
	/** 缓存时间：一天 */
	public static final int ONEDAY_CACHE_SECONDS = 86400;// 单位秒 设置成一天
	
	/** 缓存时间：三天 */
	public static final int ONEWEEK_CACHE_SECONDS = 604800;// 单位秒 设置成一周
	
	public static final int NOOVERDUE_CACHE_SECONDS = -1;// 单位秒 设置成一周

	/** 连接池 **/
	private static JedisPool jedisPool;
	
	public static final String UTF_8 = "UTF-8";
	
	 private static String host;
	 private static int port;
	 private static int MaxTotal;
	 private static int MinIdle;
	 private static int timeout;
	 
	
	static { //初始化，只执行一次
		Properties prop = new Properties();
		 try {
			prop = PropertiesLoaderUtils.loadAllProperties("redis.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("加载<redis>配置文件失败!");
		}//生产环境
		 
		 host = prop.getProperty("redis.host");
		 port = Integer.parseInt(prop.getProperty("redis.port"));
		 MaxTotal =  Integer.parseInt(prop.getProperty("redis.MaxTotal"));
		 MinIdle =  Integer.parseInt(prop.getProperty("redis.MinIdle"));
		 timeout =  Integer.parseInt(prop.getProperty("redis.timeout"));
		
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(MaxTotal); //最大连接5000个
		jedisPoolConfig.setMinIdle(MinIdle); //最小空闲50个
		
		
		//下面的构造方法可设置超时时间，意为与redis服务器建立连接或者读取数据可等待的超时时间，默认为2秒，如果redis服务器阻塞繁忙或者网络繁忙，则有可能抛出异常"java.net.SocketTimeoutException: Read timed out",因此可视情况予以调节
		jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout); //超时时间改成8秒
		logger.info("RedisUtils工具类完成初始化了...");
	}
	
	public void setJedisPool(JedisPool jedisPool) {
//		logger.info("RedisUtils工具类完成初始化了...");
//		RedisUtils.jedisPool = jedisPool;
	}
	
	/**
	 * 释放redis资源
	 * @param jedis
	 */
	private static void releaseResource(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * 删除Redis中的所有key
	 */
	public static void flushAll() {
		logger.warn("此方法不建议在代码里面使用，因此这里什么都不执行了...");
//		Jedis jedis = null;
//		try {
//			jedis = jedisPool.getResource();
//			jedis.flushAll();
//		} catch (Exception e) {
//			logger.error("Redis清空失败", e);
//		} finally {
//			releaseResource(jedis);
//		}
	}

	/**
	 * 保存一个对象到Redis中(缓存过期时间:一个小时)
	 * @param key 键
	 * @param value 缓存对象
	 * @return true or false
	 */
	public static void save(String key, Object value) {
		save(key, value, DEFAULT_CACHE_SECONDS);
	}
	
	/**
	 * 保存一个字符串到Redis中(缓存过期时间:一个小时)
	 * @param key 键
	 * @param value 缓存字符串
	 * @return true or false
	 */
	public static void saveString(String key, String value) {
		saveString(key, value, DEFAULT_CACHE_SECONDS);
	}
	
	/**
	 * 保存一个对象到Redis中(缓存过期时间: 一天)
	 * @param key 键
	 * @param value 缓存对象
	 * @return true or false
	 */
	public static void saveOneDay(String key, Object value) {
		save(key, value, ONEDAY_CACHE_SECONDS);
	}
	
	/**
	 * 保存一个字符串到Redis中(缓存过期时间: 一天)
	 * @param key 键
	 * @param value 缓存字符串
	 * @return true or false
	 */
	public static void saveStringOneDay(String key, String value) {
		saveString(key, value, ONEDAY_CACHE_SECONDS);
	}
	
	/**
	 * 保存一个对象到Redis中(缓存过期时间: 一周)
	 * @param key 键
	 * @param value 缓存对象
	 * @return true or false
	 */
	public static void saveOneWeek(String key, Object value) {
		save(key, value, ONEWEEK_CACHE_SECONDS);
	}
	
	/**
	 * 保存一个字符串到Redis中(缓存过期时间: 一周)
	 * @param key 键
	 * @param value 缓存字符串
	 * @return true or false
	 */
	public static void saveStringOneWeek(String key, String value) {
		saveString(key, value, ONEWEEK_CACHE_SECONDS);
	}
	
	/**
	 * 保存一个对象到redis中并指定过期时间，若保存对象为空，则什么也不做
	 * @param key 键
	 * @param value 缓存对象
	 * @param seconds 过期时间（单位为秒）
	 */
	public static void save(String key, Object value, int seconds) {
		if(StringUtils.isNotEmpty(key) && value != null){
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.setex(key.getBytes(UTF_8), seconds, SerializeUtils.serialize(value));
			} catch (Throwable t) {
				logger.error("Redis保存失败，key=" + key, t);
				throw new RedisException(t);
			} finally {
				releaseResource(jedis);
			}
		}
	}
	
	/**
	 * 保存一个字符串到redis中并指定过期时间，若保存字符串为空，则什么也不做
	 * @param key 键
	 * @param value 字符串
	 * @param seconds 过期时间（单位为秒）
	 * @return true or false
	 */
	public static void saveString(String key, String value, int seconds) {
		if(StringUtils.isNotEmpty(key) && value != null){
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.setex(key, seconds, value);
			} catch (Throwable t) {
				logger.error("Redis保存失败，key=" + key, t);
				throw new RedisException(t);
			} finally {
				releaseResource(jedis);
			}
		}
	}
	
	/**
	 * 保存一个对象到redis中并指定永不过期，值为对象类型
	 * @param key 键
	 * @param value 缓存对象
	 * @return true or false
	 */
	public static void saveWithNoExpire(String key, Object value) {
		if(StringUtils.isNotEmpty(key) && value != null){
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.set(key.getBytes(UTF_8), SerializeUtils.serialize(value));
			} catch (Throwable t) {
				logger.error("Redis保存失败，key=" + key, t);
				throw new RedisException(t);
			} finally {
				releaseResource(jedis);
			}
		}
	}
	
	/**
	 * 保存一个字符串到redis中并指定永不过期，值为字符串类型
	 * @param key 键
	 * @param value 缓存字符串
	 * @return true or false
	 */
	public static void saveStringWithNoExpire(String key, String value) {
		if(StringUtils.isNotEmpty(key) && value != null){
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.set(key, value);
			} catch (Throwable t) {
				logger.error("Redis保存失败，key=" + key, t);
				throw new RedisException(t);
			} finally {
				releaseResource(jedis);
			}
		}
	}
	
	/**
	 * 根据缓存键获取Redis缓存中的值，值为对象类型
	 * @param key 键
	 * @return Object 返回对象类型
	 */
	public static Object get(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			byte[] obj = jedis.get(key.getBytes(UTF_8));
			return obj == null ? null : SerializeUtils.unSerialize(obj);
		} catch (Throwable t) {
			logger.error("Redis读取对象数据失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 根据缓存键获取Redis缓存中的值，值为字符串类型，非对象类型的缓存使用本方法性能更高
	 * @param key 键
	 * @return String 返回字符串类型
	 */
	public static String getStringValue(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} catch (Throwable t) {
			logger.error("Redis读取字符串数据失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 根据缓存键获取Redis缓存中的值，值为可转换成整型数据的字符串类型，一般为计数器一类key的值，非整数类型将会转换失败并返回0，该键不存在也返回0
	 * @param key 键
	 * @return long 返回长整数类型
	 */
	public static long getLongValue(String key) {
		Jedis jedis = null;
		String strVal = null;
		try {
			jedis = jedisPool.getResource();
			strVal = jedis.get(key);
			if(strVal != null){
				return Long.parseLong(strVal);
			}
		} catch (Throwable t) {
			logger.error("Redis读取长整数类型数据失败，key=" + key + ", value=" + strVal, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
		
		return 0;
	}
	
	/**
	 * 根据缓存键清除Redis缓存中的值
	 * @param key
	 * @return
	 */
	public static void del(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
		} catch (Throwable t) {
			logger.error("Redis删除失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 根据缓存键批量清除Redis缓存中的值，只发送一次网络命令，性能更好
	 * @param keys
	 * @return
	 */
	public static void del(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(keys);
		} catch (Throwable t) {
			logger.error("Redis批量删除失败，keys=" + keys, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param seconds 超时时间（单位为秒）
	 * @return
	 */
	public static void expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.expire(key, seconds);
		} catch (Throwable t) {
			logger.error("Redis设置超时时间失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 添加一个内容到指定key的hash中,存储值为对象类型，存在序列化消耗
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static void addHash(String key, String field, Object value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hset(key.getBytes(UTF_8), field.getBytes(UTF_8), SerializeUtils.serialize(value));
		} catch (Throwable t) {
			logger.error("Redis哈希保存失败，key=" + key + "，field=" + field, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 添加一个内容到指定key的hash中,存储值为字符串类型,无序列化消耗，性能更好
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static void addHash(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hset(key, field, value);
		} catch (Throwable t) {
			logger.error("Redis哈希保存失败，key=" + key + "，field=" + field, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 从指定hash中获取指定字段的值，值为对象类型，有反序列化消耗
	 * @param key
	 * @param field
	 * @return 返回值为对象类型，有反序列化消耗
	 */
	public static Object getHash(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			byte[] obj = jedis.hget(key.getBytes(UTF_8), field.getBytes(UTF_8));
			return obj == null ? null : SerializeUtils.unSerialize(obj);
		} catch (Throwable t) {
			logger.error("Redis哈希读取失败，key=" + key + "，field=" + field, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从指定hash中获取指定字段的值，值为字符串类型，没有反序列化消耗
	 * @param key
	 * @param field
	 * @return 返回值为字符串类型，没有反序列化消耗
	 */
	public static String getHashStringValue(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hget(key, field);
		} catch (Throwable t) {
			logger.error("Redis哈希读取失败，key=" + key + "，field=" + field, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 从hash中删除指定key中的指定field的值
	 * @param key
	 * @param field
	 * @return
	 */
	public static boolean delHash(String key, String... field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			long result = jedis.hdel(key, field);
			return result == 1 ? true : false;
		} catch (Throwable t) {
			logger.error("Redis哈希删除失败，key=" + key + ", field=" + field, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 拿到缓存中所有符合pattern的key
	 * 
	 * @param pattern
	 * @return
	 */
	public static Set<String> keys(String pattern) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.keys("*" + pattern + "*");
		} catch (Throwable t) {
			logger.error("Redis模式读取失败，pattern=" + pattern, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得hash中的所有key value，值为对象类型，存在反序列化性能消耗
	 * @param key
	 * @return
	 */
	public static Map<String, Object> getHashAllFieldValue(String key) {
		Jedis jedis = null;
		Map<String, Object> res = null;
		try {
			jedis = jedisPool.getResource();
			Map<byte[], byte[]> map = jedis.hgetAll(key.getBytes(UTF_8));
			Set<byte[]> keyset = map.keySet();
			res = new HashMap<String, Object>();
			for(byte[] bt : keyset) {
				res.put(new String(bt, UTF_8), SerializeUtils.unSerialize(map.get(bt)));
			}
		} catch (Throwable t) {
			logger.error("Redis哈希读取失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
		
		return res;
	}
	
	/**
	 * 获得hash中的所有key value，值为字符串类型，不存在反序列化性能消耗
	 * @param key
	 * @return
	 */
	public static Map<String, String> getHashAllFieldStringValue(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hgetAll(key);
		} catch (Throwable t) {
			logger.error("Redis哈希读取失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 判断一个key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public static Boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		} catch (Throwable t) {
			logger.error("Redis判断key存在性失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得一个key的有效期
	 * 
	 * @param key
	 * @return
	 */
	public static Long getIndate(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.ttl(key);
		} catch (Throwable t) {
			logger.error("Redis判断key存在性失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}

	/**
	 * 根据键模式批量删除，只发送一次网络命令，性能更好
	 * @param keysPattern
	 */
	public static void delByKeysPattern(String keysPattern) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Set<String> keyset = keys(keysPattern);
			jedis.del(keyset.toArray(new String[keyset.size()]));
		} catch (Throwable t) {
			logger.error("Redis根据键模式批量删除失败，keysPattern=" + keysPattern, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 根据键列表批量获取数据，值类型为对象类型，存在反序列化性能消耗
	 * @param keyList
	 * @return
	 */
	public static List<Object> getObjectListByKeyList(List<String> keyList){
		Jedis jedis = null;
		List<Object> retList = null;
		try {
			jedis = jedisPool.getResource();
			int size = keyList.size();
			byte[][] keyBytes = new byte[size][];
			for(int i = 0 ; i < size ; i ++){
				keyBytes[i] = keyList.get(i).getBytes(UTF_8);
			}
			List<byte[]> blist = jedis.mget(keyBytes);
			retList = new ArrayList<Object>(size);
			for(int i = 0 ; i < size ; i ++){
				retList.add(SerializeUtils.unSerialize(blist.get(i)));
			}
		} catch (Throwable t) {
			logger.error("Redis批量读取数据失败", t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
		
		return retList;
	}
	
	/**
	 * 根据键列表批量获取数据，值类型为字符串类型，不存在反序列化性能消耗
	 * @param keyList
	 * @return
	 */
	public static List<String> getStringListByKeyList(List<String> keyList){
		Jedis jedis = null;
		List<String> retList = null;
		try {
			jedis = jedisPool.getResource();
			int size = keyList.size();
			retList = jedis.mget(keyList.toArray(new String[size]));
		} catch (Throwable t) {
			logger.error("Redis批量读取数据失败", t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
		
		return retList;
	}
	
	/**
	 * 向集合中增加一个元素,集合是无序的
	 * @param key
	 * @param value
	 */
	public static void addSet(String key, String value){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.sadd(key, value);
		} catch (Throwable t) {
			logger.error("Redis执行sadd操作失败，key=" + key + "，value=" + value, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 判断一个元素是否在集合中,集合是无序的
	 * @param key
	 * @param value
	 */
	public static boolean isSetMember(String key, String value){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sismember(key, value);
		} catch (Throwable t) {
			logger.error("Redis执行sadd操作失败，key=" + key + "，value=" + value, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得集合中所有的元素,集合是无序的
	 * @param key
	 * @return
	 */
	public static Set<String> getSetAllMembers(String key){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smembers(key);
		} catch (Throwable t) {
			logger.error("Redis执行smembers操作失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得集合中的元素个数,集合是无序的
	 * @param key
	 * @return
	 */
	public static long getSetLength(String key){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.scard(key);
		} catch (Throwable t) {
			logger.error("Redis执行scard操作获取集合元素个数失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 删除指定集合里面的一个或一批元素,集合是无序的
	 * @param key
	 * @param members
	 */
	public static void removeSetMembers(String key, Set<String> members){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.srem(key, members.toArray(new String[members.size()]));
		} catch (Throwable t) {
			logger.error("Redis删除指定集合里面的一个或一批元素失败，集合key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 删除指定集合里面的一批元素,集合是无序的
	 * @param key
	 * @param members
	 */
	public static void removeSetMembers(String key, String... members){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.srem(key, members);
		} catch (Throwable t) {
			logger.error("Redis删除指定集合里面的一批元素，集合key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从key指定的集合中获得所有待删除的键列表，并删除这些键，另外同时删除该key对应的集合,集合是无序的
	 * @param key
	 */
	public static void delByKeySetAndDelSet(String key){
		Jedis jedis = null;
		int size = 0;
		try {
			jedis = jedisPool.getResource();
			//获得所有待删除的键列表，并删除这些键
			Set<String> keyset = getSetAllMembers(key);
			if(keyset != null){
				size = keyset.size();
				if(size > 0){
					String[] members = keyset.toArray(new String[size]);
					jedis.del(members);
					//删除该key对应的集合中的元素
					jedis.srem(key, members);
				}
			}
		} catch (Throwable t) {
			logger.error("Redis从key指定的集合(元素个数为" + size + ")中获得所有待删除的键列表，并删除这些键，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 向集合中增加一个元素,集合是有序的
	 * @param key 集合键
	 * @param score 排序分数值
	 * @param value 元素值
	 * 
	 */
	public static void zaddSet(String key, double score, String value){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zadd(key, score, value);
		} catch (Throwable t) {
			logger.error("Redis执行zadd操作失败，key=" + key + "score=" + score + "，value=" + value, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得集合中一个区间内的元素,集合是有序的,其中成员的位置按score值递增(从小到大)来排序。<br/>
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。<br/>
     * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。<br/>
	 * 超出范围的下标并不会引起错误：<br/>
	 * 比如说，当 start 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。<br/>
	 * 另一方面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。<br/>
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static Set<String> getZSetRangeMembers(String key, long start, long end){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.zrange(key, start, end);
		} catch (Throwable t) {
			logger.error("Redis执行zrange操作升序获取有序集合一个区间内的元素失败，key=" + key + ", start=" + start + ", end=" + end, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得集合中一个区间内的元素,集合是有序的,其中成员的位置按score值递减(从大到小)来排序。<br/>
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。<br/>
     * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。<br/>
	 * 超出范围的下标并不会引起错误：<br/>
	 * 比如说，当 start 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。<br/>
	 * 另一方面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。<br/>
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static Set<String> getZSetRangeMembersByReverse(String key, long start, long end){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key, start, end);
		} catch (Throwable t) {
			logger.error("Redis执行zrange操作降序获取有序集合一个区间内的元素失败，key=" + key + ", start=" + start + ", end=" + end, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得集合中所有的元素,集合是有序的,其中成员的位置按score值递增(从小到大)来排序。
	 * @param key
	 * @return
	 */
	public static Set<String> getZSetAllMembers(String key){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.zrange(key, 0, -1);
		} catch (Throwable t) {
			logger.error("Redis执行zrange操作升序获取有序集合所有元素失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得集合中所有的元素,集合是有序的,其中成员的位置按score值递减(从大到小)来排序
	 * @param key
	 * @return
	 */
	public static Set<String> getZSetAllMembersByReverse(String key){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key, 0, -1);
		} catch (Throwable t) {
			logger.error("Redis执行zrange操作降序获取有序集合所有元素失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 获得集合中的元素个数,集合是有序的
	 * @param key
	 * @return
	 */
	public static long getZSetLength(String key){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.zcard(key);
		} catch (Throwable t) {
			logger.error("Redis执行zcard操作获取集合元素个数失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 删除指定集合里面的一个或一批元素,集合是有序的
	 * @param key
	 * @param members
	 */
	public static void removeZSetMembers(String key, String... members){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.zrem(key, members);
		} catch (Throwable t) {
			logger.error("Redis删除指定有序集合里面的一个或一批元素失败（zrem操作），集合key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 对键进行增减delta数字操作
	 * @param key
	 * @param delta 大于0表示增加，小于0表示减少
	 * @return 返回增减之后的新值
	 */
	public static Long increment(String key, long delta){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.incrBy(key, delta);
		} catch (Throwable t) {
			logger.error("Redis对键key=" + key + "进行增减" + delta + "操作失败", t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 发布一个消息到某个渠道
	 * @param channel
	 * @param msg
	 * @return 返回整数：该接收到的消息的客户端数。
	 */
	public static Long publish(String channel, Object msg){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.publish(channel.getBytes(UTF_8), SerializeUtils.serialize(msg));
		} catch (Throwable t) {
			logger.error("Redis消息发布失败，channel=" + channel + "，msg=" + msg , t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 订阅某个渠道的消息
	 * @param channel
	 */
	public static void subscribe(String channel, BinaryJedisPubSub msgHandle){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			jedis.subscribe(msgHandle, channel.getBytes(UTF_8));
		} catch (Throwable t) {
			logger.error("Redis消息订阅失败，channel=" + channel, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 返回列表 key 的长度;<br/>
	 * 如果 key 不存在，则 key 被解释为一个空列表，返回 0;<br/>
	 * 如果 key 不是列表类型，返回一个错误。
	 * @param key
	 * 
	 */
	public static long getListLength(String key) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.llen(key);
		} catch (Throwable t) {
			logger.error("Redis获取列表长度失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 向名为key的List数据结构的头部插入一条数据(字符串类型)，注意list结构是有序的，如果 key不存在，一个空列表会被创建并执行LPUSH操作
	 * @param key
	 * @param value
	 */
	public static void lpushList(String key, String value){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			jedis.lpush(key, value);
		} catch (Throwable t) {
			logger.error("Redis向List头部插入数据(字符串类型)失败，key=" + key + ", value=" + value, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 向名为key的List数据结构的头部插入一条数据(对象类型)，注意list结构是有序的，如果 key不存在，一个空列表会被创建并执行LPUSH操作
	 * @param key
	 * @param value
	 */
	public static void lpushList(String key, Object value){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			jedis.lpush(key.getBytes(UTF_8), SerializeUtils.serialize(value));
		} catch (Throwable t) {
			logger.error("Redis向List头部插入数据(对象类型)失败，key=" + key + ", value=" + value, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从名为key的List数据结构的尾部阻塞式删除一条数据(字符串类型)并返回该数据，注意list结构是有序的<br/>
	 * 本方法是阻塞式的，一直等到redis操作结果，如果key不存在或者队列为空，则一直等待
	 * @param key
	 */
	public static String brpopStringFromList(String key) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			List<String> list = jedis.brpop(0, key);
			if(list != null){
				return list.get(1);
			}
			
			return null;
		} catch (Throwable t) {
			logger.error("Redis从List尾部阻塞式删除数据(字符串类型)失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从名为key的List数据结构的尾部阻塞式删除一条数据(字符串类型)并返回该数据，注意list结构是有序的<br/>
	 * 本方法是阻塞式的，一直等到redis操作结果直到超时，如果key不存在或者队列为空，此时返回null
	 * @param key
	 * @param timeout 超时参数timeout接受一个以秒为单位的数字作为值，超时参数设为 0 表示阻塞时间可以无限期延长(block indefinitely) 
	 */
	public static String brpopStringFromList(String key, int timeout) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			List<String> list = jedis.brpop(timeout, key);
			if(list != null){
				return list.get(1);
			}
			
			return null;
		} catch (Throwable t) {
			logger.error("Redis从List尾部阻塞式删除数据(字符串类型)失败，key=" + key + ",timeout=" + timeout, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从名为key的List数据结构的尾部阻塞式删除一条数据(对象类型)并返回该数据，注意list结构是有序的，当key不存在时，返回null<br/>
	 * 本方法是阻塞式的，一直等到redis操作结果，如果key不存在或者队列为空，则一直等待
	 * @param key
	 */
	public static Object brpopObjectFromList(String key) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			List<byte[]> list = jedis.brpop(0, key.getBytes(UTF_8));
			if(list != null){
				return SerializeUtils.unSerialize(list.get(1));
			}
			
			return null;
		} catch (Throwable t) {
			logger.error("Redis从List尾部阻塞式删除数据(对象类型)失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从名为key的List数据结构的尾部阻塞式删除一条数据(对象类型)并返回该数据，注意list结构是有序的，当key不存在时，返回null<br/>
	 * 本方法是阻塞式的，一直等到redis操作结果直到超时，如果key不存在或者队列为空，此时返回null
	 * @param key
	 * @param timeout 超时参数timeout接受一个以秒为单位的数字作为值，超时参数设为 0 表示阻塞时间可以无限期延长(block indefinitely) 
	 */
	public static Object brpopObjectFromList(String key, int timeout) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			List<byte[]> list = jedis.brpop(timeout, key.getBytes(UTF_8));
			if(list != null){
				return SerializeUtils.unSerialize(list.get(1));
			}
			
			return null;
		} catch (Throwable t) {
			logger.error("Redis从List尾部阻塞式删除数据(对象类型)失败，key=" + key + ",timeout=" + timeout, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从名为key的List数据结构的尾部删除一条数据(字符串类型)并返回该数据，注意list结构是有序的，当key不存在时，返回null
	 * @param key
	 */
	public static String rpopStringFromList(String key) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.rpop(key);
		} catch (Throwable t) {
			logger.error("Redis从List尾部非阻塞式删除数据(字符串类型)失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从名为key的List数据结构的尾部删除一条数据(对象类型)并返回该数据，注意list结构是有序的，当key不存在时，返回null
	 * @param key
	 */
	public static Object rpopObjectFromList(String key) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return SerializeUtils.unSerialize(jedis.rpop(key.getBytes(UTF_8)));
		} catch (Throwable t) {
			logger.error("Redis从List尾部非阻塞式删除数据(对象类型)失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从列表srckey尾部删除一条数据(字符串类型)并返回该数据，另外将该数据插入到列表distkey头部，该动作是一个原子操作;<br/>
	 * 如果source不存在，将返回null，并且不执行其他动作;<br/>
	 * 如果source和destination相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
	 * @param srckey 源列表
	 * @param distkey 目标列表
	 * 
	 */
	public static String rpopSrcListAndLpushDistListForString(String srckey, String distkey) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.rpoplpush(srckey, distkey);
		} catch (Throwable t) {
			logger.error("Redis从列表srckey尾部删除一条数据(字符串类型)并返回该数据，另外将该数据插入到列表distkey头部，该操作失败，srckey=" + srckey + ", distkey" + distkey, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 从列表srckey尾部删除一条数据(对象类型)并返回该数据，另外将该数据插入到列表distkey头部，该动作是一个原子操作;<br/>
	 * 如果source不存在，将返回null，并且不执行其他动作;<br/>
	 * 如果source和destination相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
	 * @param srckey 源列表
	 * @param distkey 目标列表
	 * 
	 */
	public static Object rpopSrcListAndLpushDistListForObject(String srckey, String distkey) {
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return SerializeUtils.unSerialize(jedis.rpoplpush(srckey.getBytes(UTF_8), distkey.getBytes(UTF_8)));
		} catch (Throwable t) {
			logger.error("Redis从列表srckey尾部删除一条数据(对象类型)并返回该数据，另外将该数据插入到列表distkey头部，该操作失败，srckey=" + srckey + ", distkey" + distkey, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 在名为key的List数据结构中删除所有值为value(字符串类型)的元素，注意list结构是有序的
	 * @param key
	 * @param value
	 */
	public static void removeValueInList(String key, String value){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			jedis.lrem(key, 0, value);
		} catch (Throwable t) {
			logger.error("Redis删除List中元素值失败，key=" + key + ", value=" + value, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 在名为key的List数据结构中删除所有值为value(对象类型)的元素，注意list结构是有序的
	 * @param key
	 * @param value
	 */
	public static void removeValueInList(String key, Object value){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			jedis.lrem(key.getBytes(UTF_8), 0, SerializeUtils.serialize(value));
		} catch (Throwable t) {
			logger.error("Redis删除List中元素值失败，key=" + key + ", value=" + value, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除；<br/>
	 * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除；<br/>
	 * 参数 start 和 end 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推；<br/>
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推；<br/>
	 * 当 key 不是列表类型时，返回一个错误。
	 * @param key
	 * @param start 
	 * @param end 
	 * 
	 */
	public static void ltrimList(String key, long start, long end){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			jedis.ltrim(key, start, end);
		} catch (Throwable t) {
			logger.error("Redis修剪List中的元素失败，key=" + key + ", start=" + start + ", end=" + end, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 返回列表key中指定区间内的元素(字符串类型)，区间以偏移量 start 和 end 指定，包含end指定的元素；<br/>
	 * 当start=0，end=-1，表示获取所有元素；<br/>
	 * 如果获取出错，将返回null。<br/>
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<String> getRangeStringList(String key, int start, int end){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis.lrange(key, start, end);
		} catch (Throwable t) {
			logger.error("Redis获取List中区间元素(字符串类型)列表失败，key=" + key + ", start=" + start + "，end=" + end, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 返回列表key中指定区间内的元素(对象类型)，区间以偏移量 start 和 end 指定，包含end指定的元素；<br/>
	 * 当start=0，end=-1，表示获取所有元素；<br/>
	 * 如果获取出错，将返回null。<br/>
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<Object> getRangeObjectList(String key, int start, int end){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			List<byte[]> list = jedis.lrange(key.getBytes(UTF_8), start, end);
			List<Object> retList = new ArrayList<Object>();
			for(byte[] b : list){
				retList.add(SerializeUtils.unSerialize(b));
			}
			
			return retList;
		} catch (Throwable t) {
			logger.error("Redis获取List中区间元素(对象类型)列表失败，key=" + key + ", start=" + start + "，end=" + end, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 返回列表key中所有的元素(字符串类型)；<br/>
	 * 如果获取出错，将返回null。<br/>
	 * @param key
	 * @return
	 * 
	 */
	public static List<String> getStringList(String key){
		return getRangeStringList(key, 0, -1);
	}
	
	/**
	 * 返回列表key中所有的元素(对象类型)；<br/>
	 * 如果获取出错，将返回null。<br/>
	 * @param key
	 * @return
	 * 
	 */
	public static List<Object> getObjectList(String key){
		return getRangeObjectList(key, 0, -1);
	}
	
	/**
	 * 判断列表key中是否存在指定value的元素，注意列表元素均为字符串类型
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean isValueExistInStringList(String key, String value){
		List<String> list = getStringList(key);
		if(list != null && value != null){
			return list.contains(value);
		}
		
		return false;
	}
	
	/**
	 * 判断列表key中是否存在指定value的元素，注意列表元素均为对象类型
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean isValueExistInObjectList(String key, Object value){
		List<Object> list = getObjectList(key);
		if(list != null && value != null){
			return list.contains(value);
		}
		
		return false;
	}
	
	/**
	 * 设置一个缓存键，如果不存在，才能设置成功，返回true，否则返回false，该键的过期时间为600秒
	 * @param key
	 * @return
	 */
	public static boolean setnx(String key){
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			if(jedis.setnx(key, key) == 1){
				jedis.expire(key, 600); //设置分布式锁的有效期为600秒
				return true;
			}
		} catch (Throwable t) {
			logger.error("Redis命令setnx失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
		
		return false;
	}
	
	/**
	 * 获得一个分布式锁，此方法会阻塞线程直到获取成功或者获取失败<br/>
	 * 锁键的命名规范统一为 "Lock.业务命名"<br/>
	 * 注意：有效期为30秒，防止未释放导致的死锁，因此业务调用方要特别注意这个有效时间，并且用完请及时释放，为了保证业务操作的可靠性和及时响应，请不要使用该锁执行耗时操作，最长不能超过有效期30秒
	 * @param key
	 * 
	 */
	public static void getLock(String key) {
		Jedis jedis = null;
		int i = 0;
		long tid = Thread.currentThread().getId();
		
		try {
			jedis = jedisPool.getResource();
			String headStr1 = "线程id-" + tid + "第"; 
			String headStr2 = "次：分布式锁key=" + key;
			while(true){
				i++;
				String headStr = ( headStr1 + i + headStr2 );
				if(jedis.setnx(key, key) == 1){
					logger.info(headStr + "获取成功...");
					jedis.expire(key, 30); //设置分布式锁的有效期为30秒
					logger.info(headStr + "有效期设置成功，30秒内有效...");
					return;
				}
				//这一次没获取到就等待0.5秒后再获取
//				logger.info(headStr + "未获取到，等待0.5秒再次尝试获取...");
				Thread.sleep(500);
			}
		} catch (Throwable t) {
			logger.error("线程id-" + tid + "获取Redis分布式锁(已尝试" + i + "次)失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
	
	/**
	 * 释放一个分布式锁
	 * @param key
	 */
	public static void delLock(String key){
		Jedis jedis = null;
		long tid = Thread.currentThread().getId();
		
		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
			logger.info("线程id-" + tid + "对分布式锁key=" + key + "已经成功释放");
		} catch (Throwable t) {
			logger.error("线程id-" + tid + "对Redis释放分布式锁失败，key=" + key, t);
			throw new RedisException(t);
		} finally {
			releaseResource(jedis);
		}
	}
}