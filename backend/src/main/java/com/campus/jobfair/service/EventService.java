package com.campus.jobfair.service;

import com.campus.jobfair.dto.EventRegistrationRequest;
import com.campus.jobfair.dto.JobFairEventRequest;
import com.campus.jobfair.dto.JobFairRequest;
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

    private Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Instant.parse(value);
    }

    public List<JobFair> listJobFairs() {
        return jobFairRepository.findAll();
    }

    @Transactional
    public JobFair createFair(JobFairRequest req) {
        JobFair fair = new JobFair();
        fair.setName(req.getName());
        fair.setLocation(req.getLocation());
        fair.setDescription(req.getDescription());
        fair.setCapacity(req.getCapacity());
        fair.setStartTime(parseInstant(req.getStartTime()));
        fair.setEndTime(parseInstant(req.getEndTime()));
        return jobFairRepository.save(fair);
    }

    @Transactional
    public JobFair updateFair(Long id, JobFairRequest req) {
        JobFair fair = jobFairRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "双选会不存在"));
        if (req.getName() != null) fair.setName(req.getName());
        if (req.getLocation() != null) fair.setLocation(req.getLocation());
        if (req.getDescription() != null) fair.setDescription(req.getDescription());
        if (req.getCapacity() != null) fair.setCapacity(req.getCapacity());
        if (req.getStartTime() != null) fair.setStartTime(parseInstant(req.getStartTime()));
        if (req.getEndTime() != null) fair.setEndTime(parseInstant(req.getEndTime()));
        return jobFairRepository.save(fair);
    }

    public List<JobFairEvent> listEventsByFair(Long fairId) {
        List<JobFairEvent> events = jobFairEventRepository.findByJobFairId(fairId);
        // 自动为没有签到码的活动生成签到码
        for (JobFairEvent event : events) {
            if (event.getCheckinCode() == null || event.getCheckinCode().isEmpty()) {
                event.setCheckinCode(generateEventCheckinCode());
                jobFairEventRepository.save(event);
            }
        }
        return events;
    }

    public List<JobFairEvent> listAllEvents() {
        List<JobFairEvent> events = jobFairEventRepository.findAll();
        // 自动为没有签到码的活动生成签到码
        for (JobFairEvent event : events) {
            if (event.getCheckinCode() == null || event.getCheckinCode().isEmpty()) {
                event.setCheckinCode(generateEventCheckinCode());
                jobFairEventRepository.save(event);
            }
        }
        return events;
    }

    @Transactional
    public JobFairEvent createEvent(JobFairEventRequest req) {
        JobFair fair = jobFairRepository.findById(req.getJobFairId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "双选会不存在"));
        JobFairEvent event = new JobFairEvent();
        event.setJobFair(fair);
        event.setName(req.getName());
        event.setType(req.getType());
        event.setLocation(req.getLocation());
        event.setCapacity(req.getCapacity());
        event.setDescription(req.getDescription());
        event.setStartTime(parseInstant(req.getStartTime()));
        event.setEndTime(parseInstant(req.getEndTime()));
        
        // 生成8位活动签到码
        event.setCheckinCode(generateEventCheckinCode());
        
        return jobFairEventRepository.save(event);
    }

    private String generateEventCheckinCode() {
        // 生成8位随机数字签到码
        return String.format("%08d", (int)(Math.random() * 100000000));
    }

    @Transactional
    public JobFairEvent updateEvent(Long id, JobFairEventRequest req) {
        JobFairEvent event = jobFairEventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "活动不存在"));
        if (req.getJobFairId() != null && (event.getJobFair() == null
                || !req.getJobFairId().equals(event.getJobFair().getId()))) {
            JobFair fair = jobFairRepository.findById(req.getJobFairId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "双选会不存在"));
            event.setJobFair(fair);
        }
        if (req.getName() != null) event.setName(req.getName());
        if (req.getType() != null) event.setType(req.getType());
        if (req.getLocation() != null) event.setLocation(req.getLocation());
        if (req.getDescription() != null) event.setDescription(req.getDescription());
        if (req.getCapacity() != null) event.setCapacity(req.getCapacity());
        if (req.getStartTime() != null) event.setStartTime(parseInstant(req.getStartTime()));
        if (req.getEndTime() != null) event.setEndTime(parseInstant(req.getEndTime()));
        return jobFairEventRepository.save(event);
    }

    @Transactional
    public EventRegistration register(String studentUsername, EventRegistrationRequest req) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        JobFairEvent event = jobFairEventRepository.findById(req.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "活动不存在"));

        // 检查是否已有有效报名（未取消的报名）
        eventRegistrationRepository.findByEventAndStudent(event, student).ifPresent(r -> {
            if (r.getStatus() != RegistrationStatus.CANCELLED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "已报名该活动");
            }
            // 如果是已取消的报名，删除旧记录，允许重新报名
            eventRegistrationRepository.delete(r);
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

    @Transactional
    public EventRegistration selfCheckIn(String studentUsername, Long registrationId, String checkinCode) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        EventRegistration registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "报名不存在"));
        if (!registration.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权签到该报名");
        }
        if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "已签到");
        }
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "报名已取消");
        }
        
        // 验证签到码
        JobFairEvent event = registration.getEvent();
        if (event.getCheckinCode() == null || !event.getCheckinCode().equals(checkinCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "签到码错误");
        }
        
        registration.setStatus(RegistrationStatus.CHECKED_IN);
        registration.setCheckinTime(Instant.now());
        return eventRegistrationRepository.save(registration);
    }

    @Transactional
    public EventRegistration cancelCheckIn(Long registrationId) {
        EventRegistration registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "报名不存在"));
        registration.setStatus(RegistrationStatus.REGISTERED);
        registration.setCheckinTime(null);
        return eventRegistrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<EventRegistration> listMyRegistrations(String studentUsername) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        List<EventRegistration> registrations = eventRegistrationRepository.findByStudent(student);
        // 强制加载关联对象
        for (EventRegistration reg : registrations) {
            if (reg.getEvent() != null) {
                reg.getEvent().getName();
            }
        }
        return registrations;
    }

    @Transactional(readOnly = true)
    public List<EventRegistration> listAllRegistrations() {
        List<EventRegistration> registrations = eventRegistrationRepository.findAll();
        // 强制加载关联对象
        for (EventRegistration reg : registrations) {
            if (reg.getStudent() != null) {
                reg.getStudent().getName();
                reg.getStudent().getStudentNo();
                reg.getStudent().getCollege();
            }
            if (reg.getEvent() != null) {
                reg.getEvent().getName();
            }
        }
        return registrations;
    }

    @Transactional(readOnly = true)
    public List<EventRegistration> listRegistrationsByEvent(Long eventId) {
        JobFairEvent event = jobFairEventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "活动不存在"));
        List<EventRegistration> registrations = eventRegistrationRepository.findByEvent(event);
        // 强制加载关联对象
        for (EventRegistration reg : registrations) {
            if (reg.getStudent() != null) {
                reg.getStudent().getName();
                reg.getStudent().getStudentNo();
                reg.getStudent().getCollege();
            }
        }
        return registrations;
    }

    @Transactional
    public void cancelRegistration(String studentUsername, Long registrationId) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        EventRegistration registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "报名不存在"));
        if (!registration.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权取消该报名");
        }
        if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "已签到，无法取消");
        }
        registration.setStatus(RegistrationStatus.CANCELLED);
        eventRegistrationRepository.save(registration);
    }

    @Transactional
    public java.util.Map<String, Object> checkinByEventId(String studentUsername, Long eventId) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        JobFairEvent event = jobFairEventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "活动不存在"));
        
        EventRegistration registration = eventRegistrationRepository.findByEventAndStudent(event, student)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "您未报名该活动，请先报名"));
        
        if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "您已签到，请勿重复签到");
        }
        
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "报名已取消");
        }
        
        registration.setStatus(RegistrationStatus.CHECKED_IN);
        registration.setCheckinTime(Instant.now());
        eventRegistrationRepository.save(registration);
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("eventName", event.getName());
        result.put("checkinTime", registration.getCheckinTime());
        return result;
    }

    @Transactional
    public java.util.Map<String, Object> adminCheckIn(Long eventId, Long registrationId) {
        JobFairEvent event = jobFairEventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "活动不存在"));
        
        EventRegistration registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "报名记录不存在"));
        
        // 验证报名是否属于该活动
        if (!registration.getEvent().getId().equals(eventId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "签到码与活动不匹配");
        }
        
        if (registration.getStatus() == RegistrationStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该学生已签到");
        }
        
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "报名已取消");
        }
        
        registration.setStatus(RegistrationStatus.CHECKED_IN);
        registration.setCheckinTime(Instant.now());
        eventRegistrationRepository.save(registration);
        
        // 强制加载关联对象
        Student student = registration.getStudent();
        if (student != null) {
            student.getName();
            student.getStudentNo();
            student.getCollege();
        }
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("studentName", student != null ? student.getName() : "未知");
        result.put("checkinTime", registration.getCheckinTime());
        result.put("student", student);
        return result;
    }

}