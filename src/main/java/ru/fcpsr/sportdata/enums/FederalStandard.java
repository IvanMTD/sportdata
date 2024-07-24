package ru.fcpsr.sportdata.enums;

public enum FederalStandard {
    NP("Этап начальной подготовки","НП", 1),
    UTE("Учебно-тренировочный этап", "УТЭ",2),
    SSM("Этап совершенствования спортивного мастерства","ССМ",3),
    VSM("Этап высшего спортивного мастерства","ВСМ",4);

    private final String title;
    private final String shortTitle;
    private final int count;
    FederalStandard(String title, String shortTitle, int count){
        this.title = title;
        this.shortTitle = shortTitle;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public int getCount() {
        return count;
    }
}
