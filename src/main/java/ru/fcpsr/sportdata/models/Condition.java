package ru.fcpsr.sportdata.models;

public enum Condition {
    DONE("Выполнение"),
    ALLOW("Подтверждение"),
    NOT_ALLOW("Не подтверждение");

    private final String title;

    Condition(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
