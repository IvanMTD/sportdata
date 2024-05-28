package ru.fcpsr.sportdata.enums;

public enum SportFilterType {
    OLYMPIC("Олимпийский"),
    NO_OLYMPIC("Неолимпийский"),
    ADAPTIVE("Адаптивный"),
    NO("");

    private final String title;

    SportFilterType(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
}
