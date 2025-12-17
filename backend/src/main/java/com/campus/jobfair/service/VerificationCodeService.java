package com.campus.jobfair.service;

import com.campus.jobfair.entity.VerificationCode;
import com.campus.jobfair.entity.enums.VerificationScene;
import com.campus.jobfair.repository.VerificationCodeRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VerificationCodeService {

    private static final Logger log = LoggerFactory.getLogger(VerificationCodeService.class);

    private final VerificationCodeRepository verificationCodeRepository;
    private final Random random = new Random();

    public VerificationCodeService(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    public String generate(String username, VerificationScene scene) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        VerificationCode entity = new VerificationCode();
        entity.setUsername(username);
        entity.setScene(scene);
        entity.setCode(code);
        entity.setExpireAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        entity.setUsed(false);
        verificationCodeRepository.save(entity);
        log.info("Generated verification code for username={}, scene={}: {}", username, scene, code);
        return code;
    }

    public void validate(String username, VerificationScene scene, String code) {
        Optional<VerificationCode> latestOpt =
                verificationCodeRepository.findTopByUsernameAndSceneOrderByCreatedAtDesc(username, scene);
        VerificationCode latest = latestOpt.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码不存在或已失效"));
        if (latest.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码已使用");
        }
        if (latest.getExpireAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码已过期");
        }
        if (!latest.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "验证码错误");
        }
        latest.setUsed(true);
        verificationCodeRepository.save(latest);
    }
}
