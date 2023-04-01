package com.ailen.controller;

import com.ailen.SelfException;
import com.ailen.config.MyThread;
import com.ailen.model.Result;
//import com.ailen.service.FileDealService;
import com.ailen.service.Impl.FileDealServiceImpl;
import com.ailen.service.OneService;
import com.ailen.service.ReadExcelService;
import org.apache.lucene.util.fst.Util;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/fileDeal")
public class FileDealcontroller {

    @Autowired
    MyThread myThread;

    @Autowired
    OneService oneService;

    @Autowired
    ReadExcelService readExcelService;

    @Autowired
    FileDealServiceImpl fileDealService;

    @Value("${file.dirPath}")
    String dirPath;

    @GetMapping("/test")
    public String test(){
        System.out.println("接口测试后台显示");
        return "测试接口访问";
    }

    /**
     * 同类型excel汇聚
     * @param localPath
     * @return
     */
    @GetMapping("/dataConverge")
    public Result dateConverge(String localPath){
        List<File> fileNames = oneService.getFileNames(localPath);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String date = simpleDateFormat.format(new Date());
        String savePath = dirPath + date;
        File filePath = new File(savePath);
        if (!filePath.exists()){
            filePath.mkdirs();
        }

        /**
         * 判断系统类型 忽略Mac
         */
        final String osName = System.getProperty("os.name");
        boolean isWindows = osName != null && osName.startsWith("Windows");
//        boolean isOther = osName != null && !osName.startsWith("Windows");
        String concatStr = null;
        if (isWindows){
            concatStr = "\\";
        }else {
            concatStr = "/";
        }
        for (File file : fileNames) {
            final String fileName = file.getName();
            boolean state = false;
            try{
                if (fileName.matches("^.*人大代表.*xls.*$")) {
                    state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"人大代表"+concatStr+ fileName);
                    System.out.println(fileName + "移动结果：" + state);
                    if (!state){
                        throw new SelfException(fileName+"移动报错，可能因为名称重复");
                    }
                } else if (fileName.matches("^.*政协委员.*xls.*$")) {
                    state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"政协委员"+concatStr+ fileName);
                    System.out.println(fileName + "移动结果：" + state);
                    if (!state){
                        throw new SelfException(fileName+"移动报错，可能因为名称重复");
                    }
                } else if (fileName.matches("^.*共产党.*xls.*$")) {
                    state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"党员信息"+concatStr+ fileName);
                    System.out.println(fileName + "移动结果：" + state);
                    if (!state){
                        throw new SelfException(fileName+"移动报错，可能因为名称重复");
                    }
                } else if (fileName.matches("^.*财政供养.*xls.*$")) {
                    if (-1 != fileName.indexOf("基本")){
                        state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"财政供养人员-基础"+concatStr+ fileName);
                    }else if (-1 != fileName.indexOf("退出")){
                        state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"财政供养人员-退出"+concatStr+ fileName);
                    }else if (-1 != fileName.indexOf("新增")){
                        state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"财政供养人员-新增"+concatStr+ fileName);
                    }
//                    state = fileDealService.moveFileToTarget(file.getAbsolutePath(), savePath + "\\财政供养\\" + fileName);
                    System.out.println(fileName + "移动结果：" + state);
                    if (!state){
                        throw new SelfException(fileName+"移动报错，可能因为名称重复");
                    }
                } else if (fileName.matches("^.*监察对象.*xls.*$")) {
                    if (-1 != fileName.indexOf("一")){
                        state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"第一、二类监察对象"+concatStr+ fileName);
                    }else if (-1 != fileName.indexOf("五")){
                        state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"第五类监察对象"+concatStr+ fileName);
                    }else {
                        state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"第三、四、六类监察对象"+concatStr+ fileName);
                    }
//                    state = fileDealService.moveFileToTarget(file.getAbsolutePath(), savePath + "\\监察对象\\" + fileName);
                    System.out.println(fileName + "移动结果：" + state);
                    if (!state){
                        throw new SelfException(fileName+"移动报错，可能因为名称重复");
                    }
                } else if (fileName.matches("^.*振兴局.*xls.*$")){
                    state = fileDealService.moveFileToTarget(fileName,file.getAbsolutePath(), savePath +concatStr+"乡村振兴局"+concatStr+ fileName);
                    System.out.println(fileName + "移动结果：" + state);
                    if (!state){
                        throw new SelfException(fileName+"移动报错，可能因为名称重复");
                    }
                }
            }catch (SelfException e){
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.succeed();
    }

    @GetMapping("/forPath")
    public Result<Map<String,Set<String>>> fileNames(@RequestParam(value = "filePath") String filePath) {
        System.out.println("-----------------主线程开始执行："+Thread.currentThread().getName());
        System.out.println("-----------------文件路径："+filePath);
        final long startTime = System.currentTimeMillis();
        /**
         * 获取所有待插入excel文件路径
         */
        List<File> fileNames = oneService.getFileNames(filePath);
        for (File file : fileNames){
            System.out.println(file.getName());
        }

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
        fileMap.put("乡村振兴局检测对象",new ArrayList<>());
        fileMap.put("城市低保信息",new ArrayList<>());
        fileMap.put("城市特困信息",new ArrayList<>());
        fileMap.put("农村低保信息",new ArrayList<>());
        fileMap.put("农村特困信息",new ArrayList<>());
        fileMap.put("退出城市低保信息",new ArrayList<>());
        fileMap.put("退出城市特困信息",new ArrayList<>());
        fileMap.put("退出农村低保信息",new ArrayList<>());
        fileMap.put("退出农村特困信息",new ArrayList<>());
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
            else if (fileName.matches("^.*振兴局.*xls.*$")){
                fileMap.get("乡村振兴局检测对象").add(file);
            }else if (fileName.matches("^.*低保.*xls.*$")){
                if (0 == fileName.indexOf("农村")){
                    fileMap.get("农村低保信息").add(file);
                }else if (2 == fileName.indexOf("农村")){
                    fileMap.get("退出农村低保信息").add(file);
                }else if (0 == fileName.indexOf("城市")){
                    fileMap.get("城市低保信息").add(file);
                }else if (2 == fileName.indexOf("城市")){
                    fileMap.get("退出城市低保信息").add(file);
                }
            }else if (fileName.matches("^.*特困.*xls.*$")){
                if (0 == fileName.indexOf("农村")){
                    fileMap.get("农村特困信息").add(file);
                }else if (2 == fileName.indexOf("农村")){
                    fileMap.get("退出农村特困信息").add(file);
                }else if (0 == fileName.indexOf("城市")){
                    fileMap.get("城市特困信息").add(file);
                }else if (2 == fileName.indexOf("城市")){
                    fileMap.get("退出城市特困信息").add(file);
                }
            }
        }
        /**
         * 读取各表excel，统一入汇总表
         */
        Map<String,Set<String>> errorMap = new HashMap<>();
        for (Map.Entry<String,List<File>> mapType : fileMap.entrySet()){
            try {
                /**
                 * 处理一种类型excel文件，汇总到一张类型excel中
                 */
                Future<Set<String>> msg = fileDealService.dealFile(mapType.getKey(),mapType.getValue());
                /**
                 * 判断是否有错误记录，错误类型记录
                 */
                if (0 == msg.get().size()){
                    System.out.println(mapType.getKey()+"处理完成");
                }else {
                    System.out.println(mapType.getKey()+"处理完成，错误表名称已汇总");
                    errorMap.put(mapType.getKey(),msg.get());
                }
            }catch (SelfException e){
                System.out.println(e.getMessage());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("主线程执行结束："+Thread.currentThread().getName());
        System.out.println("总共耗时："+(System.currentTimeMillis()-startTime)+"ms");
        return Result.succeed(errorMap,"处理完成，请查看出错表格名称");
    }

}
