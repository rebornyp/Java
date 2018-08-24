### 面试题目：

1. 一分钟内访问超过500次，加入黑名单 ；
2. 一个小时内访问超过15000次，加入黑名单 ；
3. 一个黑名单的用户一分钟内访问不超过500次或者一个小时内访问不超过15000次，解除黑名单；

### 实现思路

1. 通过ConcurrentHashMap类型的全局变量skipMap变量存储用户和访问记录，key是用户id，value是用户访问记录；
2. 用户访问记录以跳表ConcurrentSkipListMap类型的形式记录，key是用户访问的时间（long字段），value是用户访问的序号（int型），之所以想到用跳  表是一开始用双向链表时每次遍历复杂度很高，所以想到使用并发的map来存时间戳节点，不仅可以保证访问的节点按照时间严格有序，而且查找效率高；
3.通过一个ConcurrentHashMap类的全局变量userAccessCount变量记录每个用户的访问次数，每个用户每访问一次其值就加1；
4. 通过ConcurrentHashMap类的全局变量blackList变量维护一张黑名单，如果某用户值为true，则其在黑名单上，否则不在；
5. access（）传入用户 id 和访问时间 time ，负责处理每次的用户访问及记录黑名单操作；service（）方法负责用户时间记录的插入并返回相应值给access（）方法；check（）方法负责检查时间记录是否符合条件；
6. 跳表检验时为了优化内存选择了删除该访问时间节点1小时前的记录（不影响条件判断），防止内存占用过大；
7. 用户第一次访问skipMap时需初始化，这里通过单例模式中的double check来完成检查；

### 实现代码

```java
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
    private static ConcurrentHashMap<String, Boolean> blackList = new ConcurrentHashMap<>();

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
        blackList.putIfAbsent(id, service(id, time));
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
                    cp.put(time, 0);
                    skipMap.put(id, cp);
                    userAccessCount.putIfAbsent(id, new AtomicInteger(1));
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
        sp.remove(sp.firstKey(), sp.floorKey(preHour)); // 删除用户 time时刻1小时前的所有访问记录；

        if (preMin != null && sp.get(time) - sp.get(preMin) > 500)
            return true;
        if (preHour != null && sp.get(time) - sp.get(preHour) > 15000)
            return true;
        return false;
    }

}
```
