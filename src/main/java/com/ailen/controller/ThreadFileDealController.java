package com.ailen.controller;

import com.ailen.model.Result;
//import com.ailen.service.FileDealService;
import com.ailen.model.User;
import com.ailen.service.OneService;
import com.ailen.utils.ThreadPoolExecutors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/threadFileDeal")
public class ThreadFileDealController {


//    @Autowired
//    FileDealService fileDealService;

    @Autowired
    OneService oneService;


    @PostMapping(value = "/test")
    public Result test(@RequestBody User user){
        return Result.succeed(user,"success");
    }

    @GetMapping("/forPath")
    public Result<Map<String, Set<String>>> dealFile(@RequestParam(value = "filePath")String filePath){
        List<File> fileNames = oneService.getFileNames(filePath);
        /**
         * 暂存各类总表excel文件
         */
        Map<String,List<File>> fileMap = new HashMap<>();
        fileMap.put("人大代表",new ArrayList<>());
        fileMap.put("政协委员",new ArrayList<>());
        fileMap.put("共产党信息表",new ArrayList<>());
        fileMap.put("财政供养人员-基本",new ArrayList<>());
        fileMap.put("财政供养人员-退出",new ArrayList<>());
        fileMap.put("财政供养人员-新增",new ArrayList<>());
        fileMap.put("第一、二类监察对象",new ArrayList<>());
        fileMap.put("第三、四、六类监察对象",new ArrayList<>());
        fileMap.put("第五类监察对象",new ArrayList<>());
        /**
         * excel表汇各类型汇总
         */
        for (File file : fileNames){
            String fileName = file.getName();
            if (fileName.matches("^.*人大代表.*xls.*$")){
                fileMap.get("人大代表").add(file);
            }else if (fileName.matches("^.*政协委员名单.*xls.*$")){
                fileMap.get("政协委员").add(file);
            }else if (fileName.matches("^.*党员基本信息.*xls.*$")){
                fileMap.get("共产党信息表").add(file);
            }else if (fileName.matches("^.*财政供养人员.*xls.*$")){
                if (-1 != fileName.indexOf("基本")){
                    fileMap.get("财政供养人员-基本").add(file);
                }else if (-1 != fileName.indexOf("退出")){
                    fileMap.get("财政供养人员-退出").add(file);
                }else if (-1 != fileName.indexOf("新增")){
                    fileMap.get("财政供养人员-新增").add(file);
                }
            }else if (fileName.matches("^.*监察对象.*xls.*$")){
                if (-1 != fileName.indexOf("一")){
                    fileMap.get("第一、二类监察对象").add(file);
                }else if (-1 != fileName.indexOf("五")){
                    fileMap.get("第五类监察对象").add(file);
                }else {
                    fileMap.get("第三、四、六类监察对象").add(file);
                }
            }
        }
        //创建线程池
        ThreadPoolExecutor threadPoolExecutors = ThreadPoolExecutors.getSingletonExecutor();
        return null;

    }
}
