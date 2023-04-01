package com.ailen.order.task;


import com.ailen.SelfException;
import com.ailen.order.entity.Order;
import com.ailen.order.entity.OrderVO;
import com.ailen.order.entity.User;
import com.ailen.utils.SynchroniseUtil;

import java.util.List;

public class OrderTask implements Runnable {
    private List<OrderVO> orderVO;
    private List<User> users;
    private SynchroniseUtil<OrderVO> synchroniseUtil;

    public OrderTask(List<OrderVO> orderVO,
                     List<User> users,
                     SynchroniseUtil<OrderVO> synchroniseUtil) {
        this.orderVO = orderVO;
        this.users = users;
        this.synchroniseUtil = synchroniseUtil;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"开始执行");
        //模拟从数据库里查数据
        try {
            Thread.sleep(10);
            if (1 == 1){
                throw new SelfException(Thread.currentThread().getName()+"线程执行中-------------");
            }
        } catch (InterruptedException | SelfException e) {
            OrderVO orderVO1 = new OrderVO().setUserName("bella");
            orderVO1.setUserId(34343L);
            orderVO.add(orderVO1);
            System.out.println(e.getMessage());
        }
        /**
         * 将查到的用户订单信息同步到ordervo中
         */
        for (OrderVO orderVO: orderVO){
            for (User user : users) {
                if (orderVO.getUserId().equals(user.getId())) {
                    orderVO.setUserName(user.getUserName());
                    break;
                }
            }
        }
        synchroniseUtil.addResult(orderVO);
        System.out.println(Thread.currentThread().getName()+"执行结束");
    }
}
