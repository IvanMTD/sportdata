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
    BR("Без разряда",1),
    YN1("Юношеский разряд 1",4),
    YN2("Юношеский разряд 2",3),
    YN3("Юношеский разряд 3",2),
    R1("1 Разряд",7),
    R2("2 Разряд",6),
    R3("3 Разряд",5),
    KMS("Кандидат в мастера спорта",8),
    MS("Мастер спорта России",9),
    MSMK("Мастер спорта международного класса",10),
    ZMS("Заслуженный мастер спорта",11),

    NO("Отсутствует",12);

    private final String title;
    private final int count;

    Category(String title, int count){
        this.title = title;
        this.count = count;
    }

    public String getTitle(){
        return title;
    }

    public int getCount() {
        return count;
    }

    public Category[] getBase(){
        return new Category[]{
                BR,YN3,YN2,YN1,R3,R2,R1,KMS,MS,MSMK,ZMS
        };
    }
}
