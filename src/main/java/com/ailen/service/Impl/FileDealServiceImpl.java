package com.ailen.service.Impl;

import com.ailen.SelfException;
//import com.ailen.service.FileDealService;
import com.ailen.service.ReadExcelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

@Service
public class FileDealServiceImpl {

    @Autowired
    ReadExcelService readExcelService;


    /**
     * 各类文件处理，汇总到类型总的excel文件中
     * 线程池的方法   不能  通过 impliments 类实现，直接调用该方法才行，原因未知
     * @param key
     * @param value
     * @return
     */
    @Async("thread1")
    public Future<Set<String>> dealFile(String key, List<File> value) {
        System.out.println("线程池线程执行："+Thread.currentThread().getName());
        Set<String> errorFileList = new HashSet<>();
        /**
         * 没有这种类型的excel文件时，直接返回不做处理
         */
        if (0 == value.size()){
            errorFileList.add("无文件需要处理");
            return new AsyncResult<>(errorFileList);
        }
        List list = null;
        File[] files = value.toArray(new File[value.size()]);
        /**
         * 该种类型，单个逐步文件处理
         */
        for (File file : files){
            try{
                /**
                 * 判断文件大小，过大文件直接排除，大于10M直接放弃处理
                 */
                if (10485760L < file.length()){
                    throw new SelfException("当前文件超过10M，怀疑文件出错，请进行人工核查"+file.getName());
                }
                /**
                 * 读取当前excel文件每行记录，存入list（不包括1、2行）----只有字段和记录行数据
                 */
                list = readExcelService.readExcel(file);
                /**
                 * 判断list集合中最后一条记录状态，
                 */
                List<String> stateList = (List<String>) list.get(list.size()-1);
                String state = stateList.get(0);
                list.remove(list.size()-1);
                /**
                 * 不管有没有身份数据错误，都会进行数据分类汇总到总的excel文件中
                 */
                readExcelService.writeExcel(key,file.getName(),list);
                //判断该excel中是否有身份证数据错误记录
                if ("exist".equals(state)){
                    throw new SelfException(file.getName()+"存在错误，已经记录");
                }
            }catch (Exception e){
                errorFileList.add(file.getName());
                System.out.println("当前文件处理报错，文件名称："+file.getName());
                e.printStackTrace();
            }
        }
        System.out.println("线程池线程执行结束："+Thread.currentThread().getName());
        return new AsyncResult<>(errorFileList);
    }

    /**
     * 	移动文件到指定位置
     * @param fileFullNameCurrent 要移动的文件全路径
     * @param fileFullNameTarget 移动到目标位置的文件全路径
     * @return 是否移动成功， true：成功；否则失败
     */
    public Boolean moveFileToTarget(String fileName,String fileFullNameCurrent, String fileFullNameTarget) throws IOException {
        boolean ismove = false;
        int fieldIndex = 0;
        //判断何种表
        if (fileName.matches("^.*振兴局.*xls.*$")){
            fieldIndex = 3;
        }
        File oldName = new File(fileFullNameCurrent);

        String fileType = fileFullNameCurrent.substring(fileFullNameCurrent.lastIndexOf(".") + 1);
        //获取输入流
        InputStream stream = null;
        //获取工作薄
        Workbook xssfWorkbook = null;
        if (fileType.equals("xls")) {
            stream = new FileInputStream(oldName);
            xssfWorkbook = new HSSFWorkbook(stream);
            stream.close();
        } else if (fileType.equals("xlsx")) {
            stream = new FileInputStream(oldName);
            xssfWorkbook = new XSSFWorkbook(stream);
            stream.close();
        }
        // 获取第一个sheet文档
        Sheet Sheet = xssfWorkbook.getSheetAt(0);
        final Row sheetRow = Sheet.getRow(1);
        if (null == sheetRow){
            return true;
        }
        final Cell cell = sheetRow.getCell(fieldIndex);
        final String cityName = cell.toString();

        if (!oldName.exists()) {
            System.out.println("要移动的文件不存在！");
            return ismove;
        }

        if (oldName.isDirectory()) {
            System.out.println("要移动的文件是目录，不移动！");
            return false;
        }

        final String osName = System.getProperty("os.name");
        /**
         * 不考虑mac
         */
        int index = 0;
        boolean isWindows = osName != null && osName.startsWith("Windows");
//        boolean isOther = osName != null && !osName.startsWith("Windows");
        if (isWindows){
            index = fileFullNameTarget.lastIndexOf("\\") + 1;
        }else {
            index = fileFullNameTarget.lastIndexOf("/") + 1;
        }
        fileFullNameTarget = fileFullNameTarget.substring(0,index)+cityName+fileFullNameTarget.substring(index);
        File newName = new File(fileFullNameTarget);
        if (newName.isDirectory()) {
            System.out.println("移动到目标位置的文件是目录，不能移动！");
            return false;
        }

        String pfile = newName.getParent();
        File pdir = new File(pfile);

        if (!pdir.exists()) {
            pdir.mkdirs();
            System.out.println("要移动到目标位置文件的父目录不存在，创建：" + pfile);
        }
        ismove = oldName.renameTo(newName);
        return ismove;
    }




}
