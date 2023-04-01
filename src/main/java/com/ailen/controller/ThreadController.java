package com.ailen.controller;

import com.ailen.service.Impl.ThreadDemo;
import com.ailen.service.OneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
//@RequestMapping("/test")
public class ThreadController {

    @Resource
    OneService oneService;

    @Autowired
    ThreadDemo threadDemo;

    /**
     * 多线程处理数据实现
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @GetMapping("/test")
    public void test() throws InterruptedException, ExecutionException {
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<Integer> jiList = new ArrayList<>();
        List<Integer> ouList = new ArrayList<>();
        map.put(0,ouList);
        map.put(1,jiList);
        /**
         * 循环多线程处理任务，将处理完成的数据汇总到map中，最终输出数据
         */
        for (int i=0;i<10;i++){
            final Future<Map<Integer, Integer>> mapAsyncResult = threadDemo.threadTest(i);
            final Map<Integer, Integer> map1 = mapAsyncResult.get();
            if (map1.containsKey((Integer) 0)){
                map.get(0).add(map1.get(0));
            }else {
                map.get(1).add(map1.get(1));
            }
        }
        for (Map.Entry<Integer,List<Integer>> resultMap : map.entrySet()){
            System.out.println(resultMap.getKey()+"________"+resultMap.getValue());
        }
    }


}
