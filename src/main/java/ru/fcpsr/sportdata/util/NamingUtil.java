package ru.fcpsr.sportdata.util;

public class NamingUtil {
    private static NamingUtil instance = null;

    private final String refreshName;
    private final String accessName;
    private final String sessionName;

    protected NamingUtil(){
        refreshName = "app_sd_rt";
        accessName = "app_sd_at";
        sessionName = "app_sd_st";
    }

    public static NamingUtil getInstance(){
        if(instance == null){
            instance = new NamingUtil();
        }
        return instance;
    }

    public String getRefreshName() {
        return refreshName;
    }

    public String getAccessName() {
        return accessName;
    }

    public String getSessionName() {
        return sessionName;
    }
}
