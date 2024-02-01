package ru.fcpsr.sportdata.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

/*
create table if not exists age_group(
    id int GENERATED BY DEFAULT AS IDENTITY,
    title text not null,
    min_age int not null,
    max_age int not null,
    discipline_id int,
    qualification_ids integer[]
);
 */

@Data
@RequiredArgsConstructor
public class AgeGroup {
    @Id
    private int id;
    private String title;
    private int minAge;
    private int maxAge;
    private int disciplineId;
    private Set<Integer> qualificationIds = new HashSet<>();
}