package com.ailen.utils;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ailen.order.entity.OrderVO;
import org.aspectj.weaver.ast.Or;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SynchroniseUtil<T>{
    private CountDownLatch countDownLatch;

    /**
     * 线程安全的list
     */
//    private final List<T> result = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, String> result = Collections.synchronizedMap(new HashMap<>());
    public SynchroniseUtil(int count) {
        /**
         * CountDownLatch 倒计时锁存器，同步处理线程，每次执行完成一个线程后，-1
         * count就是有多少个线程需要执行，当CountDownLatch的值为0时，表示所有线程执行完成，
         * 等待的线程可以恢复执行（主线程？）
         */
        this.countDownLatch = new CountDownLatch(count);
    }

//    public List<T> get() throws InterruptedException{
//        countDownLatch.await();
//        return this.result;
//    }
    public Map<String, String> get() throws InterruptedException{
        countDownLatch.await();
        return this.result;
    }

    public Map<String, String> get(long timeout, TimeUnit timeUnit) throws Exception{
        if (countDownLatch.await(timeout, timeUnit)) {
            return this.result;
        } else {
            throw new RuntimeException("超时");
        }
    }

//    public List<T> get(long timeout, TimeUnit timeUnit) throws Exception{
//        if (countDownLatch.await(timeout, timeUnit)) {
//            return this.result;
//        } else {
//            throw new RuntimeException("超时");
//        }
//    }

//    public void addResult(T resultMember) {
//        result.add(resultMember);
//        //执行完成一个线程任务，countDownLatch-1
//        countDownLatch.countDown();
//    }

    public void addResult(List<T> resultMembers) {
        String str = "ailen love you";
        final String[] split = StrUtil.split(str, " ");
        OrderVO vo = (OrderVO) resultMembers.get(0);
        result.put(vo.getUserName()+RandomUtil.randomString(8),String.valueOf(vo.getUserId()));
        result.put(split[0]+RandomUtil.randomString(8), String.valueOf(RandomUtil.randomLong()));
        countDownLatch.countDown();
    }
}
