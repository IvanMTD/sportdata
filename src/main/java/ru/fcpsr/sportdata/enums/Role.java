package ru.fcpsr.sportdata.enums;

public enum Role {
    USER("Пользователь"), MANAGER("Менеджер"), ADMIN("Администратор");

    private final String name;
    Role(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
