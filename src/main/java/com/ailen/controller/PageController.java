package com.ailen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
public class PageController {

    @Autowired
    ThreadPoolTaskExecutor thread1;
    // applicationTaskExecutor 为spring注册时定义得 beanName


    // 开辟两个线程，后等待两个线程 都执行完的案例
    @GetMapping("/thread")
    public Object thread() throws ExecutionException, InterruptedException {
        final long startTime = System.currentTimeMillis();
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("==============start");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"睡了1秒");
            Map<Integer,String> map = new HashMap<>();
            for (int i=0;i<10;i++){
                map.put(i,""+i);
            }
            return map;
        }, thread1).thenCompose(new Function<Map<Integer, String>, CompletionStage<String>>() {
            @Override
            public CompletionStage<String> apply(Map<Integer, String> integerStringMap) {
                return CompletableFuture.supplyAsync(new Supplier<String>() {
                    @Override
                    public String get() {
                        System.out.println("++++++++++++++"+Thread.currentThread().getName());
                        for (Map.Entry<Integer, String> map : integerStringMap.entrySet()) {
                            System.out.println(map.getKey() + "  " + map.getValue());
                        }
                        return "执行成功";
                    }
                },thread1);
            }
        });

        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> {
            System.out.println("-----------开始执行");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"睡了2秒");
        }, thread1);

        // 等待这两个线程都执行完
        final String result = completableFuture1.get();
        completableFuture2.get();
        System.out.println(System.currentTimeMillis()-startTime+"ms");
        System.out.println(result);
        return "success";
    }
}
