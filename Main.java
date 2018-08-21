package com.study;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    /**
     * ConcurrentHashMap，保存用户的主键 id 作为 Key，用户的时间戳信息节点插入跳表ConcurrentSkipListMap中作为 value，
     * ConcurrentSkipListMap 记录用户的访问时间作为 key，用户的访问次序作为 value；插入时间戳时可以保证按时有序；
     */
    private static ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Integer>> skipMap = new ConcurrentHashMap<>();

    /**
     * 记录用户访问的原子变量映射表，每个用户保留唯一的原子变量记录当前是第几次访问；
     */
    private static ConcurrentHashMap<String, AtomicInteger> userAccessCount = new ConcurrentHashMap<>();

    /**
     * 记录全局黑名单的映射表，如果用户为 true，那么则该用户在黑名单上；
     */
    private static ConcurrentHashMap<String, AtomicBoolean> blackList = new ConcurrentHashMap<>();

    /**
     * 测试 demo；
     * @param args
     */
    public static void main(String[] args) {
        Main m = new Main();
        String id = new String("tom");
        for (int i=0; i<480; i++){
            m.access(id, System.currentTimeMillis());
            System.out.println(userAccessCount.get("tom"));
            System.out.println(skipMap.get(id));
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(blackList.get("tom"));
    }

    /**
     * 处理访问的方法，获取用户的主键 id并作为参数传入；
     * @param id 用户 id
     * @param time 用户访问时间
     */
    void access(String id, long time) {
        if (!blackList.containsKey(id)) { //双重检查，若不在黑名单上则将用户加入黑名单并初始化为false；
            synchronized (blackList) {
                if (!blackList.containsKey(id))
                    blackList.put(id, new AtomicBoolean(false));
            }
        }
        blackList.get(id).set(service(id, time));
    }

    /**
     * true 代表用户访问超过了限制条件，需加入黑名单
     * false 代表用户访问符合条件，从黑名单里移除
     * @param id 用户访问时，可以作为主键的字符串，保证唯一性
     * @param time 用户访问时间：long类型数字
     * @return
     */
    public boolean service(String id, long time) {
        if (!skipMap.containsKey(id)) { //用户存在ConcurrentHashMap中
            synchronized (skipMap) {
                if (!skipMap.containsKey(id)) {
                    ConcurrentSkipListMap<Long, Integer> cp = new ConcurrentSkipListMap<>();
                    cp.put(System.currentTimeMillis(), 0);
                    skipMap.put(id, cp);
                    userAccessCount.put(id, new AtomicInteger(0));
                    return false;
                }
            }
        }
        skipMap.get(id).put(time, userAccessCount.get(id).incrementAndGet());
        return check(skipMap.get(id), time);
    }

    /**
     * 返回 true，代表用户此次访问不符合条件，否则返回 false就说明用户访问符合条件
     * @param sp 跳表；
     * @param time 此刻访问时的 time数值
     * @return 返回值
     */
    private boolean check(ConcurrentSkipListMap<Long, Integer> sp, long time) {
        Long preMin = sp.ceilingKey(time-60000); //获取到距离 time 时刻前1min最近最新一次访问的时间
        Long preHour = sp.ceilingKey(time - 3600000); //获取到距离 time 时刻前1hour最近最新一次访问的时间
        if (preMin != null && sp.get(time) - sp.get(preMin) > 500) return true;
        if (preHour != null && sp.get(time) - sp.get(preHour) > 15000) return true;
        return false;
    }

}
