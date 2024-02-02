package ru.fcpsr.sportdata.configurations;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.*;
import ru.fcpsr.sportdata.repositories.DisciplineRepository;
import ru.fcpsr.sportdata.repositories.SubjectRepository;
import ru.fcpsr.sportdata.repositories.TypeOfSportRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@Configuration
public class PreSetupConfiguration {
    @Bean
    public CommandLineRunner dataLoader(TypeOfSportRepository sportRepository, DisciplineRepository disciplineRepository, SubjectRepository subjectRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                //setupSportDataBase(sportRepository,disciplineRepository);
                //setupSubjects(sportRepository,subjectRepository);
                //setupSportFilters(sportRepository,subjectRepository);
                setupInSportSubject(sportRepository,subjectRepository);
            }
        };
    }
    private void setupInSportSubject(TypeOfSportRepository sportRepository, SubjectRepository subjectRepository){
        HSSFWorkbook wb = getWorkBookFromHSSF("./src/main/resources/static/file/subjects.xls");
        HSSFSheet sheet = wb.getSheet("ВСЕГО");
        Iterator<Row> rowIter = sheet.rowIterator();
        Map<String, List<String>> baseMap = new HashMap<>();
        String current = "";
        while (rowIter.hasNext()){
            Row row = rowIter.next();
            Cell federalDistrict = row.getCell(0); // Федеральное деление
            Cell subjectName = row.getCell(1); // Название субъекта
            Cell baseSportName = row.getCell(2); // Названия спорта который является базовым для субъекта
            Cell sportType = row.getCell(3); // Олимп, Неолимп, Адапт
            Cell season = row.getCell(4); // Сезон
            if(row.getRowNum() != 0){
                 if(!current.equals(subjectName.toString())){
                     baseMap.put(subjectName.toString(),new ArrayList<>());
                     baseMap.get(subjectName.toString()).add(baseSportName.toString());
                 }else{
                     baseMap.get(subjectName.toString()).add(baseSportName.toString());
                 }
            }
            current = subjectName.toString();
        }

        for(String subjectName : baseMap.keySet()){

        }

        System.out.println("End process");

        try {
            wb.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupSportFilters(TypeOfSportRepository sportRepository, SubjectRepository subjectRepository){
        HSSFWorkbook wb = getWorkBookFromHSSF("./src/main/resources/static/file/subjects.xls");
        HSSFSheet sheet = wb.getSheet("ВСЕГО");
        Iterator<Row> rowIter = sheet.rowIterator();
        while (rowIter.hasNext()){
            Row row = rowIter.next();
            Cell federalDistrict = row.getCell(0); // Федеральное деление
            Cell subjectName = row.getCell(1); // Название субъекта
            Cell baseSportName = row.getCell(2); // Названия спорта который является базовым для субъекта
            Cell sportType = row.getCell(3); // Олимп, Неолимп, Адапт
            Cell season = row.getCell(4); // Сезон
            if(row.getRowNum() != 0){
                sportRepository.findByTitle(baseSportName.toString()).flatMap(s -> {
                    System.out.println(s.getTitle());
                    s.setSeason(parseSeason(season.toString()));
                    s.setSportFilterType(parseFilter(sportType.toString()));
                    return Mono.just(s);
                }).subscribe();
            }
        }

        System.out.println("End process");

        try {
            wb.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void  setupSubjects(TypeOfSportRepository sportRepository, SubjectRepository subjectRepository){
        HSSFWorkbook wb = getWorkBookFromHSSF("./src/main/resources/static/file/subjects.xls");
        HSSFSheet sheet = wb.getSheet("ВСЕГО");
        Iterator<Row> rowIter = sheet.rowIterator();
        String subjectNameTemp = "";
        while (rowIter.hasNext()){
            Row row = rowIter.next();
            Cell federalDistrict = row.getCell(0); // Федеральное деление
            Cell subjectName = row.getCell(1); // Название субъекта
            Cell baseSportName = row.getCell(2); // Названия спорта который является базовым для субъекта
            Cell sportType = row.getCell(3); // Олимп, Неолимп, Адапт
            Cell season = row.getCell(4); // Сезон
            if(row.getRowNum() != 0){
                if(!subjectNameTemp.equals(subjectName.toString())){
                    Subject subject = new Subject();
                    subject.setTitle(subjectName.toString());
                    subject.setFederalDistrict(parseDistrict(federalDistrict.toString()));
                    subjectRepository.save(subject).subscribe();
                }
            }
            subjectNameTemp = subjectName.toString();
        }

        System.out.println("End process");

        try {
            wb.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupSportDataBase(TypeOfSportRepository sportRepository, DisciplineRepository disciplineRepository){
        XSSFWorkbook wb = getWorkBookFromXSSF("./src/main/resources/static/file/test.xlsx");

        XSSFSheet sheet = wb.getSheet("Лист1");

        Iterator<Row> rowIter = sheet.rowIterator();
        Map<String, List<String>> map = new HashMap<>();
        String current = "";
        while (rowIter.hasNext()){
            Row row = rowIter.next();
            Cell counter = row.getCell(0);
            Cell sportName = row.getCell(1);
            Cell discipline = row.getCell(2);
            if(counter.toString().equals("n")){
                System.out.println("start process");
            }else if(!counter.toString().equals("")){
                current = sportName.toString();
                map.put(current,new ArrayList<>());
                map.get(current).add(discipline.toString());
            }else{
                map.get(current).add(discipline.toString());
            }
        }

        for(String sport : map.keySet()){
            TypeOfSport typeOfSport = new TypeOfSport();
            typeOfSport.setTitle(sport);
            Mono<TypeOfSport> typeOfSportMono = sportRepository.save(typeOfSport);
            for(String d : map.get(sport)){
                typeOfSportMono = typeOfSportMono.flatMap(ts -> {
                    Discipline discipline = new Discipline();
                    discipline.setTitle(d);
                    discipline.setTypeOfSportId(ts.getId());
                    return disciplineRepository.save(discipline).flatMap(disciplineSave -> {
                        ts.addDiscipline(disciplineSave);
                        return sportRepository.save(ts);
                    });
                });
            }
            typeOfSportMono.subscribe();
        }

        System.out.println("End process");

        try {
            wb.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private XSSFWorkbook getWorkBookFromXSSF(String filePath){
        try{
            return new XSSFWorkbook(new FileInputStream(filePath));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private HSSFWorkbook getWorkBookFromHSSF(String filePath){
        try{
            return new HSSFWorkbook(new FileInputStream(filePath));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Season parseSeason(String season){
        return switch (season) {
            case "Летний" -> Season.SUMMER;
            case "Зимний" -> Season.WINTER;
            default -> Season.ALL;
        };
    }

    private SportFilterType parseFilter(String filer){
        return switch (filer) {
            case "олимпийский" -> SportFilterType.OLYMPIC;
            case "неолимпийский" -> SportFilterType.NO_OLYMPIC;
            default -> SportFilterType.ADAPTIVE;
        };
    }

    private FederalDistrict parseDistrict(String district){
        return switch (district) {
            case "ЦФО" -> FederalDistrict.CFO;
            case "СЗФО" -> FederalDistrict.SZFO;
            case "ЮФО" -> FederalDistrict.YFO;
            case "СКФО" -> FederalDistrict.SKFO;
            case "ПФО" -> FederalDistrict.PFO;
            case "УФО" -> FederalDistrict.UFO;
            case "СФО" -> FederalDistrict.SFO;
            case "ДФО" -> FederalDistrict.DFO;
            default -> null;
        };
    }
}
