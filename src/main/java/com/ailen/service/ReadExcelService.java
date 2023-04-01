package com.ailen.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ReadExcelService {

    //读取excel函数：
    List readExcel(File file) throws Exception;

    void writeExcel(String key,String fileName,List list) throws IOException, InvalidFormatException;

    List<String> getFileNames(String path);

    List<String> getFileNames(File file, List<String> fileNames);
}
