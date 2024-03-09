package ru.fcpsr.sportdata.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum FederalDistrict {
    CFO("Центральный федеральный округ","ЦФО"),
    SZFO("Северо-Западный федеральный округ","СЗФО"),
    YFO("Южный федеральный округ","ЮФО"),
    SKFO("Северо-Кавказский федеральный округ","СКФО"),
    PFO("Приволжский федеральный округ","ПФО"),
    UFO("Уральский федеральный округ","УФО"),
    SFO("Сибирский федеральный округ","СФО"),
    DFO("Дальневосточный федеральный округ","ДФО"),
    NO("","");

    private final String title;
    private final String simple;

    FederalDistrict(String title,String simple){
        this.title = title;
        this.simple = simple;
    }

    public String getTitle(){
        return title;
    }

    public String getSimple(){
        return simple;
    }

    public static List<FederalDistrict> getDistricts(){
        return new ArrayList<>(Arrays.asList(CFO,SZFO,YFO,SKFO,PFO,UFO,SFO,DFO));
    }
}
