package ru.fcpsr.sportdata.enums;

public enum Season {
    WINTER("Зимний"),
    SUMMER("Летний"),
    ALL("Всесезонный"),
    NO("");

    private final String title;

    Season(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
