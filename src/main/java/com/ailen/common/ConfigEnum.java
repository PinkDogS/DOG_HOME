package com.ailen.common;

public enum ConfigEnum {

    WINDOWS_SAVE_PATH("E:\\anshare\\"),
    WINDOWS_DIR_PATH("E:\\anshare\\"),



    LINUX_SAVE_PATH("/usr/local/resultFile/"),
    LINUX_DIR_PATH("/usr/local/");


    private String name;

    ConfigEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
