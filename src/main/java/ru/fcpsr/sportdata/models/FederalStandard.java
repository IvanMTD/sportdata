package ru.fcpsr.sportdata.models;

public enum FederalStandard {
    NP("Этап начальной подготовки","НП"),
    UTE("Учебно-тренированный этап", "УТЭ"),
    SSM("Этап совершенствования спортивного мастерства","ССМ"),
    VSM("Этап высшего спортивного мастерства","ВСМ");

    private final String title;
    private final String shortTitle;
    FederalStandard(String title, String shortTitle){
        this.title = title;
        this.shortTitle = shortTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getShortTitle() {
        return shortTitle;
    }
}
