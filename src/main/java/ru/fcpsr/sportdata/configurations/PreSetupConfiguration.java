package ru.fcpsr.sportdata.configurations;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Discipline;
import ru.fcpsr.sportdata.models.TypeOfSport;
import ru.fcpsr.sportdata.repositories.DisciplineRepository;
import ru.fcpsr.sportdata.repositories.TypeOfSportRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Configuration
public class PreSetupConfiguration {
    @Bean
    public CommandLineRunner dataLoader(TypeOfSportRepository sportRepository, DisciplineRepository disciplineRepository) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {

            }
        };
    }

    private void setupSportDataBase(TypeOfSportRepository sportRepository, DisciplineRepository disciplineRepository){
        XSSFWorkbook wb = getWorkBook("./src/main/resources/static/file/test.xlsx");

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

        try {
            wb.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private XSSFWorkbook getWorkBook(String filePath){
        try{
            return new XSSFWorkbook(new FileInputStream(filePath));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
