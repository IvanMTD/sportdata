package ru.fcpsr.sportdata.models;

/*
б/р - без разряда
юн. разряды - юношеские разряды
1 разряд -  так и есть
2 разряд - так и есть
3 разряд - так и есть
КМС - кандидат в мастера спорта
МС - мастер спорта России
МСМК - мастер спорта международного класса
ЗМС -заслуженный мастер спорта.
 */

public enum Category {
    BR("Без разряда"),
    YN1("Юношеский разряд 1"),
    YN2("Юношеский разряд 2"),
    YN3("Юношеский разряд 3"),
    R1("1 Разряд"),
    R2("2 Разряд"),
    R3("3 Разряд"),
    KMS("Кандидат в мастера спорта"),
    MS("Мастер спорта России"),
    MSMK("Мастер спорта международного класса"),
    ZMS("Заслуженный мастер спорта"),

    NO("Отсутствует");

    private final String title;

    Category(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public Category[] getBase(){
        return new Category[]{
                BR,YN1,YN2,YN3,R1,R2,R3,KMS,MS,MSMK,ZMS
        };
    }
}
