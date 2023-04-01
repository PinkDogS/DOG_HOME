package com.ailen.service;

import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface OneService {

    List<File> getFileNames(String path);


    List<File> getFileNames(File file, List<File> fileNames);

    void getAilen();
}
