package ru.fcpsr.sportdata.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.Place;
import ru.fcpsr.sportdata.models.Qualification;
import ru.fcpsr.sportdata.services.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/database/")
public class DataRestController {

    private final ParticipantService participantService;
    private final QualificationService qualificationService;
    private final TypeOfSportService sportService;
    private final DisciplineService disciplineService;
    private final AgeGroupService groupService;
    private final SportSchoolService schoolService;
    private final SubjectService subjectService;
    private final ArchiveSportService archiveSportService;
    private final PlaceService placeService;

    @GetMapping("/place/delete")
    public Mono<Place> deletePlace(@RequestParam(name = "query") String query){
        int placeId = Integer.parseInt(query);
        return placeService.deleteById(placeId).flatMap(place -> {
            log.info("'place' [{}] has been delete from db", place);
            return archiveSportService.deletePlaceFromASport(place).flatMap(archiveSport -> {
                log.info("place has been removed from archive sport [{}]",archiveSport);
                if(place.getNewQualificationId() != 0){
                    return qualificationService.deleteQualification(place.getNewQualificationId()).flatMap(qualification -> {
                        return sportService.removeQualificationFromSport(qualification).flatMap(typeOfSport -> {
                            return participantService.removeQualificationFromParticipant(qualification).flatMap(participant -> {
                                return Mono.just(place);
                            });
                        });
                    });
                }else{
                    return Mono.just(place);
                }
            });
        });
    }

    @GetMapping("/get/disciplines")
    public Flux<DisciplineDTO> getDisciplines(@RequestParam(name = "query") String query){
        int sportId = Integer.parseInt(query);
        return disciplineService.getAllBySportId(sportId).flatMap(discipline -> {
            DisciplineDTO disciplineDTO = new DisciplineDTO(discipline);
            return Mono.just(disciplineDTO);
        }).collectList().flatMapMany(dl -> {
            dl = dl.stream().sorted(Comparator.comparing(DisciplineDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(dl);
        }).flatMapSequential(Mono::just);
    }

    @GetMapping("/get/groups")
    public Flux<AgeGroupDTO> getGroups(@RequestParam(name = "query") String query){
        int sportId = Integer.parseInt(query);
        return groupService.getAllBySportId(sportId).flatMap(group -> {
            AgeGroupDTO ageGroupDTO = new AgeGroupDTO(group);
            return Mono.just(ageGroupDTO);
        }).collectList().flatMapMany(gl -> {
            gl = gl.stream().sorted(Comparator.comparing(AgeGroupDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(gl);
        }).flatMapSequential(Mono::just);
    }

    @GetMapping("/get/sport")
    public Mono<TypeOfSportDTO> getSport(@RequestParam(name = "query") String query){
        int id = Integer.parseInt(query);
        return sportService.getById(id).flatMap(sport -> {
            TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
            return disciplineService.getAllByIds(sport.getDisciplineIds()).flatMap(discipline -> {
                DisciplineDTO disciplineDTO = new DisciplineDTO(discipline);
                return Mono.just(disciplineDTO);
            }).collectList().flatMap(dl -> {
                dl = dl.stream().sorted(Comparator.comparing(DisciplineDTO::getTitle)).collect(Collectors.toList());
                typeOfSportDTO.setDisciplines(dl);
                return groupService.getAllByIds(sport.getAgeGroupIds()).flatMap(group -> {
                    AgeGroupDTO ageGroupDTO = new AgeGroupDTO(group);
                    return Mono.just(ageGroupDTO);
                }).collectList().flatMap(gl -> {
                    gl = gl.stream().sorted(Comparator.comparing(AgeGroupDTO::getTitle)).collect(Collectors.toList());
                    typeOfSportDTO.setGroups(gl);
                    return Mono.just(typeOfSportDTO);
                });
            });
        });
    }
    @GetMapping("/participant")
    public Mono<ParticipantDTO> getParticipant(@RequestParam(name = "query") String query){
        return getCompletedParticipant(query);
    }

    @GetMapping("/participants")
    public Flux<Participant> getParticipants(@RequestParam(name = "query") String query){
        return participantService.getAllBySearchQuery(query).take(10);
    }

    @GetMapping("/sport-participants")
    public Flux<ParticipantDTO> getParticipantsBySportId(@RequestParam(name = "search") String search, @RequestParam("sportId") int sportId){
        if(search.equals("")){
            return Flux.empty();
        }else {
            return qualificationService.getAllBySportId(sportId).collectList().flatMap(ql -> {
                Set<Integer> participantIds = new HashSet<>();
                for (Qualification qualification : ql) {
                    participantIds.add(qualification.getParticipantId());
                }
                return Mono.just(participantIds);
            }).flatMapMany(pidList -> {
                return participantService.getAllByIdInAndSearchQuery(pidList, search).flatMap(participant -> {
                    ParticipantDTO participantDTO = new ParticipantDTO(participant);
                    return qualificationService.getAllByIds(participant.getQualificationIds()).flatMap(qualification -> {
                        QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                        return sportService.findById(qualification.getTypeOfSportId()).flatMap(sport -> {
                            TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                            qualificationDTO.setSport(typeOfSportDTO);
                            return Mono.just(qualificationDTO);
                        });
                    }).collectList().flatMap(ql -> {
                        ql = ql.stream().sorted(Comparator.comparing(QualificationDTO::getCategory)).collect(Collectors.toList());
                        participantDTO.setQualifications(ql);
                        return Mono.just(participantDTO);
                    }).flatMap(participantDTO2 -> {
                        return schoolService.getAllByIdIn(participant.getSportSchoolIds()).flatMap(school -> {
                            SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                            return subjectService.getById(school.getSubjectId()).flatMap(subject -> {
                                SubjectDTO subjectDTO = new SubjectDTO(subject);
                                sportSchoolDTO.setSubject(subjectDTO);
                                return Mono.just(sportSchoolDTO);
                            });
                        }).collectList().flatMap(sl -> {
                            sl = sl.stream().sorted(Comparator.comparing(SportSchoolDTO::getTitle)).collect(Collectors.toList());
                            participantDTO2.setSchools(sl);
                            return Mono.just(participantDTO2);
                        });
                    });
                }).collectList().flatMapMany(pl -> {
                    pl = pl.stream().sorted(Comparator.comparing(ParticipantDTO::getFullName)).collect(Collectors.toList());
                    return Flux.fromIterable(pl);
                }).flatMapSequential(Mono::just);
            }).take(10);
        }
    }

    @GetMapping("/sport/participants")
    public Flux<ParticipantDTO> getParticipantsBySportId(@RequestParam(name = "query") String query){
        int sportId = Integer.parseInt(query);
        return getCompletedParticipantBySportId(sportId);
    }

    @PostMapping("/monitor")
    public Mono<ResponseEntity<InputStreamResource>> monitoring(@RequestBody String data) throws IOException {
        try {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new InternalError("VM does not support mandatory encoding UTF-8");
        }

        String[] paragraphs = data.split("\\\\n");
        String additionally = paragraphs[paragraphs.length - 1];
        List<String> paragraphList = new ArrayList<>();
        for(int i=0; i<paragraphs.length - 2; i++){
            String p = paragraphs[i].replace("\\","");
            if(i == 18){
                paragraphList.add(p + additionally.substring(0,additionally.length() - 1));
            }else{
                paragraphList.add(p);
            }
        }

        return Mono.just(createWord(paragraphList));
    }
    public ResponseEntity<InputStreamResource> createWord(List<String> paragraphList) throws IOException {
        // Создаем документ
        XWPFDocument document = new XWPFDocument();

        for(int i = 0; i < paragraphList.size(); i++) {
            String paragraphString = paragraphList.get(i);
            if(!paragraphString.equals("") || i == 1 || i == 7) {
                XWPFParagraph paragraph = document.createParagraph();

                if (i == 0) {
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    paragraphString = paragraphString.substring(1);
                } else {
                    paragraph.setAlignment(ParagraphAlignment.BOTH);
                    paragraph.setIndentationFirstLine(720);
                }

                // Создаем Run
                XWPFRun run = paragraph.createRun();
                run.setText(paragraphString);
                run.setFontFamily("Times New Roman");
                run.setFontSize(14);

                // Делаем текст жирным и устанавливаем размер шрифта для первого параграфа
                if (i == 0) {
                    run.setBold(true);
                }
                if (i == 14) {
                    run.setFontSize(8);
                }
            }
        }

        // Конвертируем документ в поток байтов
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        // Возвращаем поток байтов как ответ
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    private Flux<ParticipantDTO> getCompletedParticipantBySportId(int sportId) {
        return sportService.getById(sportId).flatMapMany(sport -> {
            log.info("FOUND SPORT: [{}]", sport);
            return qualificationService.getAllByIds(sport.getQualificationIds()).collectList().flatMap(ql -> {
                Set<Integer> participantIds = new HashSet<>();
                for(Qualification qualification : ql){
                    participantIds.add(qualification.getParticipantId());
                }
                return Mono.just(participantIds);
            }).flatMapMany(pidList -> {
                log.info("THIS IS PID LIST: [{}]", pidList);
                return participantService.getAllByIdIn(pidList).flatMap(participant -> {
                    ParticipantDTO participantDTO = new ParticipantDTO(participant);
                    return qualificationService.getAllByIds(participant.getQualificationIds()).flatMap(qualification -> {
                        QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                        return sportService.findById(qualification.getTypeOfSportId()).flatMap(qSport -> {
                            qualificationDTO.setSport(new TypeOfSportDTO(qSport));
                            return Mono.just(qualificationDTO);
                        });
                    }).collectList().flatMap(ql -> {
                        log.info("THIS IS QUALIFICATION LIST: [{}]", ql);
                        ql = ql.stream().sorted(Comparator.comparing(qualification -> qualification.getCategory().getTitle())).collect(Collectors.toList());
                        participantDTO.setQualifications(ql);
                        return schoolService.getAllByIdIn(participant.getSportSchoolIds()).flatMap(school -> {
                            SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                            return subjectService.getById(school.getSubjectId()).flatMap(subject -> {
                                sportSchoolDTO.setSubject(new SubjectDTO(subject));
                                return Mono.just(sportSchoolDTO);
                            });
                        }).collectList().flatMap(sl -> {
                            log.info("THIS IS SCHOOL LIST: [{}]", sl);
                            sl = sl.stream().sorted(Comparator.comparing(SportSchoolDTO::getTitle)).collect(Collectors.toList());
                            participantDTO.setSchools(sl);
                            return Mono.just(participantDTO);
                        });
                    });
                });
            }).collectList().flatMapMany(pl -> {
                log.info("THIS IS PARTICIPANT LIST: [{}]", pl);
                pl = pl.stream().sorted(Comparator.comparing(ParticipantDTO::getFullName)).collect(Collectors.toList());
                return Flux.fromIterable(pl);
            }).flatMapSequential(Mono::just);
        });
    }

    private Mono<ParticipantDTO> getCompletedParticipant(String fullName) {
        return participantService.findByFullName(fullName).flatMap(participant -> {
            ParticipantDTO participantDTO = new ParticipantDTO(participant);
            return qualificationService.getAllByIds(participant.getQualificationIds()).flatMap(qualification -> {
                QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                return sportService.getById(qualification.getTypeOfSportId()).flatMap(sport -> {
                    TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                    qualificationDTO.setSport(typeOfSportDTO);
                    return Mono.just(qualificationDTO);
                });
            }).collectList().flatMap(ql -> {
                ql = ql.stream().sorted(Comparator.comparing(QualificationDTO::getCategory)).collect(Collectors.toList());
                participantDTO.setQualifications(ql);
                return schoolService.getAllByIdIn(participant.getSportSchoolIds()).flatMap(school -> {
                    SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                    return subjectService.getById(school.getSubjectId()).flatMap(subject -> {
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        sportSchoolDTO.setSubject(subjectDTO);
                        return Mono.just(sportSchoolDTO);
                    });
                }).collectList().flatMap(schools -> {
                    schools = schools.stream().sorted(Comparator.comparing(SportSchoolDTO::getTitle)).collect(Collectors.toList());
                    participantDTO.setSchools(schools);
                    return Mono.just(participantDTO);
                });
            });
        });
    }
}
