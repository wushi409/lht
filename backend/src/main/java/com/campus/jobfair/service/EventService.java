package com.campus.jobfair.service;

import com.campus.jobfair.dto.EventRegistrationRequest;
import com.campus.jobfair.entity.EventRegistration;
import com.campus.jobfair.entity.JobFair;
import com.campus.jobfair.entity.JobFairEvent;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.RegistrationStatus;
import com.campus.jobfair.repository.EventRegistrationRepository;
import com.campus.jobfair.repository.JobFairEventRepository;
import com.campus.jobfair.repository.JobFairRepository;
import com.campus.jobfair.repository.StudentRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EventService {

    private final JobFairRepository jobFairRepository;
    private final JobFairEventRepository jobFairEventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final StudentRepository studentRepository;

    public EventService(JobFairRepository jobFairRepository,
                        JobFairEventRepository jobFairEventRepository,
                        EventRegistrationRepository eventRegistrationRepository,
                        StudentRepository studentRepository) {
        this.jobFairRepository = jobFairRepository;
        this.jobFairEventRepository = jobFairEventRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.studentRepository = studentRepository;
    }

    public List<JobFair> listJobFairs() {
        return jobFairRepository.findAll();
    }

    public List<JobFairEvent> listEventsByFair(Long fairId) {
        JobFair fair = jobFairRepository.findById(fairId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "双选会不存在"));
        return jobFairEventRepository.findByJobFair(fair);
    }

    @Transactional
    public EventRegistration register(String studentUsername, EventRegistrationRequest req) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        JobFairEvent event = jobFairEventRepository.findById(req.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "活动不存在"));

        eventRegistrationRepository.findByEventAndStudent(event, student).ifPresent(r -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "已报名该活动");
        });

        if (event.getCapacity() != null) {
            long registered = eventRegistrationRepository.countByEventAndStatus(event, RegistrationStatus.REGISTERED);
            if (registered >= event.getCapacity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "名额已满");
            }
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setStudent(student);
        registration.setSeatNo(req.getSeatNo());
        registration.setStatus(RegistrationStatus.REGISTERED);
        return eventRegistrationRepository.save(registration);
    }

    @Transactional
    public EventRegistration checkIn(Long registrationId) {
        EventRegistration registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "报名不存在"));
        registration.setStatus(RegistrationStatus.CHECKED_IN);
        registration.setCheckinTime(Instant.now());
        return eventRegistrationRepository.save(registration);
    }
}
