package ru.fcpsr.sportdata.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.SportSchool;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SportSchoolDTO {
    private int id;
    @NotBlank(message = "Поле названия спортивной организации не может быть пустым!")
    private String title;
    @NotBlank(message = "Поле адрес обязательно для заполнения!")
    private String address;
    private int subjectId;
    private SubjectDTO subject;
    private String inn;
    private List<ParticipantDTO> participants = new ArrayList<>();

    public SportSchoolDTO(SportSchool school) {
        setId(school.getId());
        setTitle(school.getTitle());
        setAddress(school.getAddress());
        setInn(school.getInn());
    }
}
