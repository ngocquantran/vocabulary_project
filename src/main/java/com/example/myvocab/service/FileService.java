package com.example.myvocab.service;

import com.example.myvocab.MyvocabApplication;
import com.example.myvocab.exception.BadRequestException;
import com.example.myvocab.util.FileUploadUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    //    Path folder asset
    private final Path rootPath = Paths.get("upload");

    public FileService() {
        createFolder(rootPath.toString());
    }

    //    Create folder
    public void createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void uploadFile(String uploadDir, String fileName, MultipartFile file) {
        // Solution 2
        createFolder(uploadDir);
        Path uploadPath = Paths.get(uploadDir);

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload");
        }

    }

    public void validate(MultipartFile file) {
//       Check file name
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.equals("")) {
            throw new BadRequestException("Tên file không hợp lệ");
        }

//        Check file extension
        String fileExtension = FileUploadUtil.getExtensionFile(fileName);
        if (!FileUploadUtil.checkFileExtension(fileExtension)) {
            throw new BadRequestException("File không hợp lệ");
        }

//        Check file size < 2MB
        if ((double) file.getSize() / 1_000_000L > 2) {
            throw new BadRequestException("File không được vượt quá 2MB");
        }
    }


}
