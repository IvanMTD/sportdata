package ru.fcpsr.sportdata.models;

public enum Condition {
    DONE("Выполнение", 3),
    ALLOW("Подтверждение", 2),
    NOT_ALLOW("Не подтверждение", 1),

    NO("Отсутствует", 0);

    private final String title;
    private final int count;

    Condition(String title, int count){
        this.title = title;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public int getCount() {
        return count;
    }
}
