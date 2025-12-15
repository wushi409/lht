package com.campus.jobfair.service;

import com.campus.jobfair.entity.FileResource;
import com.campus.jobfair.repository.FileResourceRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileStorageService {

    @Value("${storage.upload-dir:uploads}")
    private String uploadDir;

    private final FileResourceRepository fileResourceRepository;

    public FileStorageService(FileResourceRepository fileResourceRepository) {
        this.fileResourceRepository = fileResourceRepository;
    }

    public FileResource store(String ownerType, Long ownerId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件为空");
        }
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            FileResource resource = new FileResource();
            resource.setOwnerType(ownerType);
            resource.setOwnerId(ownerId);
            resource.setFileName(file.getOriginalFilename());
            resource.setUrl(target.toString());
            resource.setContentType(file.getContentType());
            resource.setSize(file.getSize());
            return fileResourceRepository.save(resource);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "文件保存失败");
        }
    }
}
