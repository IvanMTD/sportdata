package ru.fcpsr.sportdata.models;

public enum SportFilterType {
    OLYMPIC("Олимпийский"),
    NO_OLYMPIC("Неолимпийский"),
    ADAPTIVE("Адаптивный");

    private final String title;

    SportFilterType(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
}
