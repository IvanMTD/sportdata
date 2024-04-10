package ru.fcpsr.sportdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching
public class SportdataApplication {
	public static void main(String[] args) {
		SpringApplication.run(SportdataApplication.class, args);
	}
}

/*
public class SportdataApplication {
	public static void main(String[] args) {
		ExelService exelService = new ExelService();
		String fullFilePath = "./src/main/resources/static/file/test.xlsx";
		XSSFWorkbook wb = exelService.readWordbook(fullFilePath);
		XSSFSheet sheet = wb.getSheet("Лист1");

		Iterator<Row> rowIter = sheet.rowIterator();
		Map<String,List<String>> map = new HashMap<>();
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
			for(String discipline : map.get(sport)){
				System.out.println(sport + " | " + discipline );
			}
			System.out.println("\n");
		}


		*/
/*XSSFRow row = sheet.getRow(0);
		System.out.println(row.getCell(1).toString());*//*


		try {
			wb.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}*/
