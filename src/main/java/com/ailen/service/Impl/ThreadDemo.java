package com.ailen.service.Impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class ThreadDemo {

    @Async("thread1")
    public Future<Map<Integer,Integer>> threadTest(Integer num) {
        System.out.println(Thread.currentThread().getName()+"开始执行");
//        try {
//            Thread.sleep(100);
//        }catch (InterruptedException e){
//            System.out.println(e.getMessage());
//        }
        Map<Integer,Integer> map = new HashMap<>();
        if (0 == num%2){
            map.put(0,num);
        }else {
            map.put(1,num);
        }
        System.out.println("睡的秒数："+num);
        System.out.println(Thread.currentThread().getName()+"结束执行");
        return new AsyncResult<>(map);
    }


    public Integer getInfo(Integer num) throws InterruptedException {
        Thread.sleep(100);
        System.out.println("no Async "+num);
        return num;
    }

}
