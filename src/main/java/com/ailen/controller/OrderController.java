package com.ailen.controller;


import com.ailen.SelfException;
import com.ailen.order.entity.OrderVO;
import com.ailen.order.entity.User;
import com.ailen.order.task.OrderTask;
import com.ailen.utils.SynchroniseUtil;
import com.ailen.utils.ThreadPoolExecutors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class OrderController {
    private List<OrderVO> orderVOS = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    //初始化时就创建好数据。模拟数据库已经存在的数据
    @PostConstruct
    public void createData() {
        long dataCount = 500;

        // 创建订单数据。模拟已经插入到数据库的订单
        for (long i = 0; i < dataCount; i++) {
            OrderVO orderVO = new OrderVO();
            orderVO.setId(i + 1);
            orderVO.setUserId(i + 1);
            //防止电脑太快，导致都是同一个时间，所以加一个数
            orderVO.setCreateTime(LocalDateTime.now().plusSeconds(i));
            orderVOS.add(orderVO);
        }

        // 创建用户数据。模拟已经插入到数据库的用户
        for (long i = 0; i < dataCount; i++) {
            User user = new User();
            user.setId(i + 1);
            user.setUserName("用户名" + (i + 1));
            users.add(user);
        }
        orderVOS = orderVOS.stream()
                .sorted(Comparator.comparing(OrderVO::getCreateTime).reversed())
                .collect(Collectors.toList());
    }

    @GetMapping("/getOrderDetails")
    public Map<String, String> getOrderDetails() throws Exception{
        long startTime = System.currentTimeMillis();

//        List<OrderVO> orderVOList;
//        orderVOList = multiThread(orderVOS);
        Map<String, String> orderVOList;
        orderVOList = multiThread(orderVOS);

        long endTime = System.currentTimeMillis();

        System.out.println("执行时间：" + (endTime - startTime) + " ms");
        return orderVOList;
    }
//    List<OrderVO>
    private Map<String, String> multiThread(List<OrderVO> orders) throws Exception{

        ThreadPoolExecutor executor = ThreadPoolExecutors.getSingletonExecutor();
        int unitLength = orders.size() / ThreadPoolExecutors.getQueueSize() + 1;
        int synchroniseCount = orders.size() / unitLength;
        synchroniseCount = orders.size() % unitLength == 0
                ? synchroniseCount : synchroniseCount + 1;
        SynchroniseUtil<OrderVO> synchroniseUtil = new SynchroniseUtil<>(synchroniseCount);
        System.out.println("任务个数:" + synchroniseCount);

        for (int i = 0; i < orders.size(); i += unitLength) {
            int toIndex = Math.min(i + unitLength, orders.size() - 1);
            List<OrderVO> orderVOSubList = orders.subList(i, toIndex);
            OrderTask orderTask = new OrderTask(orderVOSubList, users, synchroniseUtil);
            executor.execute(orderTask);
        }

//        //创建线程池
//        ExecutorService executor = ThreadPoolExecutors.getSingletonExecutor();
//
//        /**
//         * 有orders.size的记录需要处理，因此需要创建这么多线程处理（每个数据一个线程处理）
//         */
//        SynchroniseUtil<OrderVO> synchroniseUtil = new SynchroniseUtil<>(orders.size());
//        System.out.println("任务个数：" + orders.size());
//
//        /**
//         * 遍历所有的数据，每个数据一个线程处理，多线程执行
//         */
//        for (OrderVO order : orders) {
//            //将数据存入线程类（任务）中，等待执行
//            OrderTask orderTask = new OrderTask(order, users, synchroniseUtil);
//            //execute方法用于向线程池中提交任务
//            //executor.submit() 方法可以有返回值
//            executor.execute(orderTask);
//        }

        Map<String, String> list = null;
//        List<OrderVO> list = null;
        try {
            //10S后 获取所有处理的数据，没完成报错。
            list = synchroniseUtil.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        if (list != null) {
//            list = list.stream()
//                    .sorted(Comparator.comparing(OrderVO::getCreateTime).reversed())
//                    .collect(Collectors.toList());
//        }

        return list;
    }
}
