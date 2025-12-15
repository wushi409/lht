package com.campus.jobfair.service;

import com.campus.jobfair.dto.AnnouncementRequest;
import com.campus.jobfair.entity.Announcement;
import com.campus.jobfair.repository.AnnouncementRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public List<Announcement> listAll() {
        return announcementRepository.findAll();
    }

    public Announcement getById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "公告不存在"));
    }

    @Transactional
    public Announcement create(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setTarget(request.getTarget());
        if (request.getPublishAt() != null) announcement.setPublishAt(Instant.parse(request.getPublishAt()));
        if (request.getExpireAt() != null) announcement.setExpireAt(Instant.parse(request.getExpireAt()));
        announcement.setPinned(request.isPinned());
        return announcementRepository.save(announcement);
    }
}
