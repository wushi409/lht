package com.campus.jobfair.service;

import com.campus.jobfair.dto.EventRequest;
import com.campus.jobfair.dto.JobFairRequest;
import com.campus.jobfair.entity.JobFair;
import com.campus.jobfair.entity.JobFairEvent;
import com.campus.jobfair.repository.JobFairEventRepository;
import com.campus.jobfair.repository.JobFairRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class JobFairService {

    private final JobFairRepository jobFairRepository;
    private final JobFairEventRepository jobFairEventRepository;

    public JobFairService(JobFairRepository jobFairRepository, JobFairEventRepository jobFairEventRepository) {
        this.jobFairRepository = jobFairRepository;
        this.jobFairEventRepository = jobFairEventRepository;
    }

    public List<JobFair> listAll() {
        return jobFairRepository.findAll();
    }

    public JobFair getById(Long id) {
        return jobFairRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "双选会不存在"));
    }

    @Transactional
    public JobFair create(JobFairRequest request) {
        JobFair jobFair = new JobFair();
        jobFair.setName(request.getName());
        jobFair.setLocation(request.getLocation());
        jobFair.setDescription(request.getDescription());
        jobFair.setCapacity(request.getCapacity());
        if (request.getStartTime() != null) {
            jobFair.setStartTime(Instant.parse(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            jobFair.setEndTime(Instant.parse(request.getEndTime()));
        }
        return jobFairRepository.save(jobFair);
    }

    @Transactional
    public JobFair update(Long id, JobFairRequest request) {
        JobFair jobFair = getById(id);
        jobFair.setName(request.getName());
        jobFair.setLocation(request.getLocation());
        jobFair.setDescription(request.getDescription());
        jobFair.setCapacity(request.getCapacity());
        if (request.getStartTime() != null) {
            jobFair.setStartTime(Instant.parse(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            jobFair.setEndTime(Instant.parse(request.getEndTime()));
        }
        return jobFairRepository.save(jobFair);
    }

    public List<JobFairEvent> listEvents(Long jobFairId) {
        return jobFairEventRepository.findByJobFairId(jobFairId);
    }

    @Transactional
    public JobFairEvent createEvent(EventRequest request) {
        JobFair jobFair = getById(request.getJobFairId());
        JobFairEvent event = new JobFairEvent();
        event.setJobFair(jobFair);
        event.setName(request.getName());
        if (request.getType() != null) {
            event.setType(com.campus.jobfair.entity.enums.EventType.valueOf(request.getType()));
        }
        event.setLocation(request.getLocation());
        event.setDescription(request.getDescription());
        event.setCapacity(request.getCapacity());
        if (request.getStartTime() != null) {
            event.setStartTime(Instant.parse(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            event.setEndTime(Instant.parse(request.getEndTime()));
        }
        return jobFairEventRepository.save(event);
    }

    @Transactional
    public JobFairEvent updateEvent(Long id, EventRequest request) {
        JobFairEvent event = jobFairEventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "活动不存在"));
        event.setName(request.getName());
        if (request.getType() != null) {
            event.setType(com.campus.jobfair.entity.enums.EventType.valueOf(request.getType()));
        }
        event.setLocation(request.getLocation());
        event.setDescription(request.getDescription());
        event.setCapacity(request.getCapacity());
        if (request.getStartTime() != null) {
            event.setStartTime(Instant.parse(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            event.setEndTime(Instant.parse(request.getEndTime()));
        }
        return jobFairEventRepository.save(event);
    }

    public List<JobFairEvent> listAllEvents() {
        return jobFairEventRepository.findAll();
    }

    @Transactional
    public void deleteFair(Long id) {
        JobFair jobFair = getById(id);
        jobFairRepository.delete(jobFair);
    }

    @Transactional
    public void deleteEvent(Long id) {
        JobFairEvent event = jobFairEventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "活动不存在"));
        jobFairEventRepository.delete(event);
    }
}
