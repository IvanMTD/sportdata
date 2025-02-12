package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fcpsr.sportdata.enums.Category;
import ru.fcpsr.sportdata.enums.Condition;
import ru.fcpsr.sportdata.enums.FederalStandard;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
public class ContestMonitoringDTO {
    private String contestTitle;
    private String sportTitle;
    private String ekp;
    private String subjectTitle;
    private String city;
    private String location;
    private LocalDate beginning;
    private LocalDate ending;

    private int athletesTookPart;
    private int athletesTookPartBoy;
    private int athletesTookPartGirl;

    private int br;
    private int yn1;
    private int yn2;
    private int yn3;
    private int r1;
    private int r2;
    private int r3;
    private int kms;
    private int ms;
    private int msmk;
    private int zms;

    private int trainerTotal;
    private int judgeTotal;
    private int nonresidentJudge;
    private int mc;
    private int vrc;
    private int fc;
    private int sc;
    private int tc;
    private int bc;
    private int dc;

    private int yn1Date;
    private int yn2Date;
    private int yn3Date;
    private int r1Date;
    private int r2Date;
    private int r3Date;
    private int kmsDate;
    private int msDate;
    private int msmkDate;
    private int zmsDate;

    private List<SportDTO> disciplines = new ArrayList<>();
    private SubjectMonitoringDTO subjectMonitoring;

    public List<String> judgeCategories(){
        List<Integer> judges = new ArrayList<>(List.of(dc,bc,tc,sc,fc,vrc,mc));
        List<String> naming = new ArrayList<>(List.of("без указанной категории","без категории", "третий категории", "второй категории", "первой категории", "всероссийской категории", "международной категории"));
        List<String> summary = new ArrayList<>();
        for(int i=0; i<judges.size(); i++){
            if(judges.get(i) != 0 && judges.get(i) > 0){
                summary.add(naming.get(i) + " - " + judges.get(i) + " " + humanFormat(judges.get(i)) + ";");
            }
        }

        if(summary.size() > 0) {
            String last = summary.get(summary.size() - 1);
            last = last.substring(0, last.length() - 1) + ".";
            summary.set(summary.size() - 1, last);
        }

        return summary;
    }

    public String getAges(){
        List<SupportPart> dis = distinctParts(getParts());
        int minAge = 100;
        int maxAge = 0;
        for(SupportPart sp : dis){
            LocalDate today = beginning;
            LocalDate birthday = sp.getBirthday();

            Period age = Period.between(birthday, today);

            int years = age.getYears();
            if(years > maxAge){
                maxAge = years;
            }
            if(years < minAge){
                minAge = years;
            }
        }

        return minAge + " - " + yearsForm(maxAge);
    }

    public String getYears(){
        List<SupportPart> dis = distinctParts(getParts());
        int minYear = 10000;
        int maxYear = 0;
        for(SupportPart sp : dis){
            int years = sp.getBirthday().getYear();
            if(years > maxYear){
                maxYear = years;
            }
            if(years < minYear){
                minYear = years;
            }
        }

        return minYear + " - " + maxYear;
    }

    private String yearsForm(int year){
        String answer = year + " лет";
        if(year < 21){
            if(year == 1){
                answer = year + " года";
            }
        }else{
            int b = year - ((year / 10) * 10);
            if(b == 1){
                answer = year + " года";
            }
        }
        return answer;
    }

    public String getFormatContestTitle(){
        String[] words = contestTitle.split("\\s");
        StringBuilder newContestTitle = new StringBuilder();
        for(String word : words){
            if(word.toLowerCase().contains("росс") || word.toLowerCase().contains("федер")){
                newContestTitle.append(word).append(" ");
            }else{
                newContestTitle.append(word.toLowerCase()).append(" ");
            }
        }

        return newContestTitle.toString().trim();
    }

    public String getFormatContestTitleInQuotes(){
        return "«" + getFormatContestTitle() + "»";
    }

    public String getSportTitleLowerCase(){
        return sportTitle.toLowerCase();
    }

    public String getSportTitleLowerCaseInQuotes(){
        return "«" + getSportTitleLowerCase() + "»";
    }

    public String getFormatAgeGroups(){
        Set<String> groupSet = new HashSet<>();
        for(SportDTO discipline : disciplines){
            String formatData = discipline.getGroup().getTitle() != null ? discipline.getGroup().getTitle().toLowerCase() : "";
            groupSet.add(formatData);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(String group : groupSet){
            stringBuilder.append(group).append(", ");
        }

        if(stringBuilder.length() == 0){
            return stringBuilder.toString();
        }else{
            return stringBuilder.substring(0,stringBuilder.toString().length() - 2);
        }
    }

    public String getFormatDisciplines(){
        Set<String> disciplineSet = new HashSet<>();
        for(SportDTO discipline : disciplines){
            String formatData = discipline.getDiscipline().getTitle().toLowerCase();
            disciplineSet.add(formatData);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(String discipline : disciplineSet){
            stringBuilder.append(discipline).append(", ");
        }

        if(stringBuilder.length() == 0){
            return stringBuilder.toString();
        }else {
            return stringBuilder.substring(0, stringBuilder.toString().length() - 2);
        }
    }

    public String getFormatDisciplinesInQuotes(){
        Set<String> disciplineSet = new HashSet<>();
        for(SportDTO discipline : disciplines){
            String formatData = discipline.getDiscipline().getTitle().toLowerCase();
            disciplineSet.add("«" + formatData + "»");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(String discipline : disciplineSet){
            stringBuilder.append(discipline).append(", ");
        }

        if(stringBuilder.length() == 0){
            return stringBuilder.toString();
        }else {
            return stringBuilder.substring(0, stringBuilder.toString().length() - 2);
        }
    }

    public String getBeginningDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(beginning);
    }

    public String getEndingDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(ending);
    }

    public String getContestDate(){
        return getBeginningDate() + " - " + getEndingDate();
    }

    public String getFullLocation(){
        Set<String> stringSet = new HashSet<>(Arrays.asList(subjectTitle,city));
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : stringSet){
            stringBuilder.append(s).append(", ");
        }
        if(stringBuilder.length() == 0){
            return stringBuilder.toString();
        }else {
            return stringBuilder.substring(0, stringBuilder.toString().length() - 2);
        }
    }

    public String getFormatContestQualificationDateAllow(){
        StringBuilder builder = new StringBuilder();
        String[] titles = {"третий юношеский разряд", "второй юношеский разряд", "первый юношеский разряд",
                "третий разряд", "второй разряд", "первый разряд",
                "кандидаты в мастера спорта", "мастера спорта России", "мастера спорта международного класса",
                "заслуженные мастера спорта"};
        int[] dates = {yn3Date, yn2Date, yn1Date, r3Date, r2Date, r1Date, kmsDate, msDate, msmkDate, zmsDate};

        for (int i = 0; i < dates.length; i++) {
            int date = dates[i];
            if (date != 0) {
                builder.append(titles[i]);
                if (date == 1 || date == 21 || date == 31 || date == 41) {
                    builder.append(" с ").append(date).append(" года, ");
                } else {
                    builder.append(" с ").append(date).append(" лет, ");
                }
            }
        }

        if(builder.length() == 0){
            return builder.toString();
        }else {
            return builder.substring(0, builder.toString().length() - 2);
        }
    }

    public String getFederalStandard(){
        List<FederalStandard> fsl = new ArrayList<>();
        for(SportDTO discipline : disciplines){
            for(FederalStandard fs : discipline.getStandards()){
                if(fs != null){
                    fsl.add(fs);
                }
            }
        }
        fsl = fsl.stream().sorted(Comparator.comparing(FederalStandard::getCount)).distinct().collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        for(FederalStandard fs : fsl){
            builder.append(fs.getTitle().toLowerCase()).append(", ");
        }
        if(builder.length() == 0){
            return builder.toString();
        }else {
            return builder.substring(0, builder.toString().length() - 2);
        }
    }

    public String getAllowed(){
        List<Category> al = new ArrayList<>();
        for(SportDTO discipline :disciplines){
            for(Category c : discipline.getAllowed()){
                if(c != null){
                    al.add(c);
                }
            }
        }
        al = al.stream().sorted(Comparator.comparing(Category::getCount)).distinct().collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        for(Category category : al){
            String title = firstLetterLow(category.getTitle());
            builder.append(title).append(", ");
        }
        if(builder.length() == 0){
            return builder.toString();
        }else {
            return builder.substring(0, builder.toString().length() - 2);
        }
    }

    public List<String> getTotalCountContestQualifications(){
        List<Integer> ql = new ArrayList<>(List.of(br,yn3,yn2,yn1,r3,r2,r1,kms,ms,msmk,zms));
        List<String> tl = new ArrayList<>(List.of(
                "без разряда",
                "юношеский третий разряд",
                "юношеский второй разряд",
                "юношеский первый разряд",
                "третий разряд",
                "второй разряд",
                "первый разряд",
                "кандидат в мастера спорта",
                "мастер спорта России",
                "мастер спорта международного класса",
                "заслуженный мастер спорта"
                ));

        List<String> answer = new ArrayList<>();
        for(int i=0; i<ql.size(); i++){
            int n = ql.get(i);
            if(n != 0){
                StringBuilder builder = new StringBuilder();
                if(n == 2 || n == 3 || n == 4){
                    builder.append(tl.get(i)).append(" - ").append(n).append(" человека;");
                }else{
                    if(n < 9){
                        builder.append(tl.get(i)).append(" - ").append(n).append(" человек;");
                    }else{
                        int b = n - ((n / 10) * 10);
                        if(b == 2 || b == 3 || b == 4){
                            builder.append(tl.get(i)).append(" - ").append(n).append(" человека;");
                        }else{
                            builder.append(tl.get(i)).append(" - ").append(n).append(" человек;");
                        }
                    }
                }
                answer.add(builder.toString());
            }
        }

        if(answer.size() > 0) {
            String last = answer.get(answer.size() - 1);
            last = last.substring(0, last.length() - 1) + ".";
            answer.set(answer.size() - 1, last);
        }

        return answer;
    }

    public List<String> getFinalistsQualification(){

        Set<SupportPart> supportParts = getParts();

        // удаляем все повторные записи сохраняя старшинство квалификации
        List<SupportPart> dis = distinctParts(supportParts);

        Map<String, Long> results = dis.stream()
                .collect(Collectors.groupingBy(sp -> sp.getCategory().getTitle(), Collectors.counting()));

        // Сортируем Map по значению поля count в Category
        results = results.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> getCategoryByTitle(e.getKey()).getCount()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        List<String> condition = new ArrayList<>();
        for(String key : results.keySet()){
            String title = firstLetterLow(key);
            if(title.equals("отсутствует")){
                title = "квалификация не была указана";
            }
            int n = Math.toIntExact(results.get(key));
            if(n == 2 || n == 3 || n == 4){
                condition.add(title + " - " + results.get(key) + " человека;");
            }else{
                if(n < 9){
                    condition.add(title + " - " + results.get(key) + " человек;");
                }else{
                    int b = n - ((n / 10) * 10);
                    if(b == 2 || b == 3 || b == 4){
                        condition.add(title + " - " + results.get(key) + " человека;");
                    }else{
                        condition.add(title + " - " + results.get(key) + " человек;");
                    }
                }
            }
        }

        if(condition.size() > 0) {
            String last = condition.get(condition.size() - 1);
            last = last.substring(0, last.length() - 1) + ".";
            condition.set(condition.size() - 1, last);
        }

        return condition;
    }

    private Category getCategoryByTitle(String title) {
        for (Category category : Category.values()) {
            if (category.getTitle().equals(title)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category title: " + title);
    }

    public List<String> getAnalyticsAboutFinalists(){
        Set<SupportPart> supportParts = getParts();
        log.info("first: [{}]",supportParts);
        List<SupportPart> dis = distinctParts(supportParts);
        return generateAnalytics(dis);
    }

    private List<String> generateAnalytics(List<SupportPart> supportParts) {

        log.info("incoming parts [{}]", supportParts);

        Map<Category, List<SupportPart>> groupedByCategory = supportParts.stream()
                .collect(Collectors.groupingBy(SupportPart::getCategory));

        // Сортируем Map по count в Category
        groupedByCategory = groupedByCategory.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getKey().getCount()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        List<String> analytics = new ArrayList<>();

        for (Map.Entry<Category, List<SupportPart>> entry : groupedByCategory.entrySet()) {
            Category category = entry.getKey();
            List<SupportPart> parts = entry.getValue();

            long count = parts.size();
            long doneCount = parts.stream().filter(p -> p.getCondition() == Condition.DONE).count();
            long allowCount = parts.stream().filter(p -> p.getCondition() == Condition.ALLOW).count();
            long notAllowCount = parts.stream().filter(p -> p.getCondition() == Condition.NOT_ALLOW).count();

            Map<Category, Long> doneResults = parts.stream()
                    .filter(p -> p.getCondition() == Condition.DONE)
                    .collect(Collectors.groupingBy(SupportPart::getNewCategory, Collectors.counting()));

            Map<Category, Long> notAllowResults = parts.stream()
                    .filter(p -> p.getCondition() == Condition.NOT_ALLOW)
                    .collect(Collectors.groupingBy(SupportPart::getNewCategory, Collectors.counting()));

            StringBuilder result = new StringBuilder();
            result.append("участники с квалификацией - ").append(firstLetterLow(category.getTitle())).append(" - ").append(count).append(humanFormat(count));

            if (allowCount > 0) {
                result.append(", подтвердили: ").append(allowCount).append(humanFormat(allowCount));
            }

            if (notAllowCount > 0) {
                result.append(", не подтвердили: ").append(notAllowCount);
                appendCategoryResults(result, notAllowResults);
            }

            if (doneCount > 0) {
                result.append(", выполнили: ").append(doneCount);
                appendCategoryResults(result, doneResults);
            }
            String res = result.toString() + ";";
            analytics.add(res);
        }

        if(analytics.size() > 0) {
            String last = analytics.get(analytics.size() - 1);
            last = last.substring(0, last.length() - 1) + ".";
            analytics.set(analytics.size() - 1, last);
        }

        return analytics;
    }

    private void appendCategoryResults(StringBuilder result, Map<Category, Long> results) {
        results.forEach((category, count) -> result.append(", из них: ").append(count).append(" с результатом ").append(firstLetterLow(category.getTitle())));
    }

    private List<SupportPart> distinctParts(Set<SupportPart> supportParts){
        List<SupportPart> dis = supportParts.stream()
                .collect(Collectors.groupingBy(SupportPart::getPid))
                .values()
                .stream()
                .map(parts -> parts.stream().max(Comparator.comparingInt(sp -> sp.getCategory().getCount())).orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(sp -> sp.getCategory().getCount()))
                .collect(Collectors.toCollection(ArrayList::new));
        return dis;
    }

    private Set<SupportPart> getParts(){
        Set<SupportPart> supportParts = new HashSet<>();

        for(SportDTO d : disciplines){
            for(PlaceDTO p : d.getPlaces()){
                if(p != null){
                    if(p.getParticipantId() != 0) {
                        SupportPart supportPart = new SupportPart();
                        supportPart.setPid(p.getParticipantId());
                        supportPart.setCategory(p.getQualification().getCategory());
                        supportPart.setCondition(p.getCondition());
                        supportPart.setNewCategory(p.getNewQualificationData());
                        supportPart.setBirthday(p.getParticipant().getBirthday());
                        supportParts.add(supportPart);
                    }
                }
            }
        }
        return supportParts;
    }

    private String firstLetterLow(String word){
        String title = word;
        String firstChar = title.substring(0,1).toLowerCase();
        String restOfTitle = title.substring(1);
        title = firstChar + restOfTitle;
        return title;
    }

    private String humanFormat(long n){
        String human = "человек";
        if(n == 2 || n == 3 || n == 4){
            human = " человека";
        }else{
            if(n < 9){
                human = " человек";
            }else{
                // Что бы не забыть - тут я ищу все числа после 20. Очень интересный способ :)))
                long b = n - ((n / 10) * 10);
                if(b == 2 || b == 3 || b == 4){
                    human = " человека";
                }else{
                    human = " человек";
                }
            }
        }
        return human;
    }
}

@Data
@NoArgsConstructor
class SupportPart {
    private int pid;
    private Category category;

    private Condition condition;
    private Category newCategory;

    private LocalDate birthday;
}
