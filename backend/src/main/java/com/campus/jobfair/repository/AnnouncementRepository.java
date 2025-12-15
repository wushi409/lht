package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Announcement;
import com.campus.jobfair.entity.enums.AnnouncementTarget;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTargetInAndPublishAtBeforeAndExpireAtAfterOrderByPinnedDescPublishAtDesc(
            List<AnnouncementTarget> targets, Instant now1, Instant now2);
}
