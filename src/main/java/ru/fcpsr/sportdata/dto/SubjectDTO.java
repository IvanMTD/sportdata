package ru.fcpsr.sportdata.dto;

import lombok.Data;
import ru.fcpsr.sportdata.models.FederalDistrict;
import ru.fcpsr.sportdata.models.Subject;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubjectDTO {
    private int id;
    private String title;
    private FederalDistrict federalDistrict;
    private List<TypeOfSportDTO> sports = new ArrayList<>();
    private List<ParticipantDTO> participants = new ArrayList<>();

    public SubjectDTO(Subject subject){
        setId(subject.getId());
        setTitle(subject.getTitle());
        if(subject.getFederalDistrict() != null){
            setFederalDistrict(subject.getFederalDistrict());
        }else{
            setFederalDistrict(FederalDistrict.NO);
        }
    }

    public void addSport(TypeOfSportDTO sport){
        sports.add(sport);
    }

    public void addParticipant(ParticipantDTO participant){
        participants.add(participant);
    }
}
