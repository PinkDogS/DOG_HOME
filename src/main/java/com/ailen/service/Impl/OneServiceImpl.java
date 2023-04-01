package com.ailen.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.ailen.service.OneService;
import com.ailen.service.ReadExcelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

import static java.sql.Types.BOOLEAN;
import static java.sql.Types.NUMERIC;
import static jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType.BLANK;
import static org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType.FORMULA;
import static org.apache.poi.util.POILogger.ERROR;
import static org.apache.xmlbeans.impl.piccolo.xml.Piccolo.STRING;

@Service
public class OneServiceImpl implements OneService {

    @Resource
    ReadExcelService readExcelService;


    @Async("thread1")
    public String ailen() throws InterruptedException {
        Thread.sleep(3000);
        System.out.println("睡了三秒");
        return "ailen";
    }

    /**
     * 得到文件名称
     *
     * @param path 路径
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<File> getFileNames(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        List<File> fileNames = new ArrayList<>();
        return getFileNames(file, fileNames);
    }

    /**
     * 得到文件名称
     *
     * @param file      文件
     * @param fileNames 文件名
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<File> getFileNames(File file, List<File> fileNames) {
        File[] files = file.listFiles();
        for (File f : files){
            if (f.isDirectory()){
                getFileNames(f,fileNames);
            }else {
                String fName = f.getName();
                String fileType = fName.substring(fName.lastIndexOf(".")+1);
                if ("xls".equals(fileType)||"xlsx".equals(fileType)){
                    fileNames.add(f);
                }
            }
        }
        return fileNames;
    }

    /**
     * 读取单元格内容 包括计算公式的结果，引用公式的结果（引用公式值当前的sheet单元格，引用了另一个Excel文件的内容例：='C:\Users\Desktop\[测试引用.xlsx]Sheet1'!A3）
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell){
        System.out.println(cell);
        String value = null;
        if(cell != null){
            System.out.println(cell.getCellType());
            switch (cell.getCellType()){
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()){
                        case NUMERIC:
                            if(DateUtil.isCellDateFormatted(cell)){
                                Date date = cell.getDateCellValue();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                value = sdf.format(date);
                            }else{
                                BigDecimal n = new BigDecimal(cell.getNumericCellValue());
                                DecimalFormat decimalFormat = new DecimalFormat("0");
                                decimalFormat.setMaximumFractionDigits(18);
                                value = decimalFormat.format(n.doubleValue());
                            }
                            break;
                        case STRING:
                            value = String.valueOf(cell.getStringCellValue());
                            if(value != null){
                                value = value.trim();
                            }
                            break;
                        case BOOLEAN:
                            value = String.valueOf(cell.getBooleanCellValue());
                            break;
                        case ERROR: value = "";
                            break;
                        default:
                            value = cell.getRichStringCellValue().getString();
                            break;
                    }
                    break;
                case NUMERIC:
                    if(DateUtil.isCellDateFormatted(cell)){
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        value = sdf.format(date);
                    }else{
                        BigDecimal n = new BigDecimal(cell.getNumericCellValue());
                        DecimalFormat decimalFormat = new DecimalFormat("0");
                        decimalFormat.setMaximumFractionDigits(18);
                        value = decimalFormat.format(n.doubleValue());
                    }
                    break;
                case STRING:
                    value = String.valueOf(cell.getStringCellValue());
                    if(value != null){
                        value = value.trim();
                    }
                    break;
                default:
                    value = cell.getRichStringCellValue().getString();
                    break;
            }
        }
        return value;
    }





    /**
     * 获取map的最后一个key
     */
    @Override
    public void getAilen() {
        Map<String,String> map = new LinkedHashMap<>();
        map.put("1","ailen");
        map.put("2","bella");
        System.out.println(this.getLastKey(map));
    }

    public String getLastKey(Map<String,String> maps){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String,String> map : maps.entrySet()){
            list.add(map.getKey());
        }
        return list.get(list.size()-1);
    }


}
