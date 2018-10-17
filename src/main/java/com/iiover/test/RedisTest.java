package com.iiover.test;

import com.iiover.util.RedisUtils;
import com.iiover.util.SmsTencent;
import org.apache.storm.command.list;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @ClassName RedisTest
 * @Descriptio TODO 常用的Redis操作
 * @Author YuMing_Huai
 * @Date 2018/10/17 14:42
 * @Version 1.0
 **/
public class RedisTest {
    private static final Logger logger = LoggerFactory.getLogger(RedisTest.class);
    public static void main(String[] args) {
        //保存一个字符串到redis中并指定永不过期，值为字符串类型
        //RedisUtils.saveStringWithNoExpire("saveStringWithNoExpire","saveStringWithNoExpire");
        RedisUtils.saveStringWithNoExpire("key1","asdasdas");

        //根据缓存键获取Redis缓存中的值，值为字符串类型，非对象类型的缓存使用本方法性能更高
        //logger.info(RedisUtils.getStringValue("saveStringWithNoExpire"));

        // 根据缓存键批量清除Redis缓存中的值，只发送一次网络命令，性能更好
        //RedisUtils.del("saveStringWithNoExpire");

        //logger.info(RedisUtils.getStringValue("saveStringWithNoExpire"));

        //添加一个内容到指定key的hash中,存储值为字符串类型,无序列化消耗，性能更好
        //RedisUtils.addHash("addhash","001","1101");
        //RedisUtils.addHash("addhash","002","1102");

        //从指定hash中获取指定字段的值，值为字符串类型，没有反序列化消耗
        //logger.info(RedisUtils.getHashStringValue("addhash","001"));

        //从hash中删除指定key中的指定field的值
        //RedisUtils.delHash("addhash","001");

       //获得hash中的所有key value，值为字符串类型，不存在反序列化性能消耗
        Map map = RedisUtils.getHashAllFieldStringValue("addhash");
        System.out.println(map);


        //判断一个key是否存在
        boolean b =  RedisUtils.exists("saveStringWithNoExpire");
        System.out.println("saveStringWithNoExpire是否存在："+b);

        //获得一个key的有效期
        Long l  = RedisUtils.getIndate("saveStringWithNoExpire");
        System.out.println("saveStringWithNoExpire有效期："+l);

        //根据键列表批量获取数据，值类型为字符串类型，不存在反序列化性能消耗
        List<String> keyList = new ArrayList();
        keyList.add("saveStringWithNoExpire");
        keyList.add("key1");
        List<String> list = RedisUtils.getStringListByKeyList(keyList);
        System.out.println("根据键列表批量获取数据，值类型为字符串类型:"+list);

        //向集合中增加一个元素,集合是无序的
        RedisUtils.addSet("set","s1");
        RedisUtils.addSet("set","s2");

        //判断一个元素是否在集合中,集合是无序的
        boolean sb = RedisUtils.isSetMember("set","s1");
        System.out.println("判断一个元素是否在集合中:"+sb);

        //获得集合中所有的元素,集合是无序的
        Set<String> set = RedisUtils.getSetAllMembers("set");
        System.out.println(set);

        //获得集合中的元素个数,集合是无序的
        Long ls = RedisUtils.getSetLength("set");
        System.out.println("获得集合中的元素个数:"+ls);

        //删除指定集合里面的一个或一批元素,集合是无序的
        Set s = new TreeSet();
        s.add("s1");
        RedisUtils.removeSetMembers("set",s);
    }
}
