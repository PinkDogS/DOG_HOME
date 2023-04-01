//package com.ailen.service.Impl;
//
//import com.ailen.service.AsyncService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.CountDownLatch;
//
//@Service
//@Slf4j
//public class AsyncServiceImpl implements AsyncService {
//
//    @Override
//    @Async("asyncServiceExecutor")
//    public void executeAsync(List<StandardStation> list, StandardStationService standardStationService, CountDownLatch countDownLatch) {
//        try {
//            log.info("start executeAsync");
//            // 异步线程需要做的事情
//            standardStationService.saveBatch(list);
//            log.info("end executeAsync");
//        } finally {
//            // 无论上面程序是否异常必须执行 countDown,否则 await 无法释放
//            countDownLatch.countDown();
//        }
//    }
//}
//
