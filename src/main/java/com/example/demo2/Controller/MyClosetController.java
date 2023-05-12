package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.demo2.domian.MyClosetData;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class MyClosetController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Value("${cloud.aws.s3.bucket4}")
    private String bucket4;

    @Value("${cloud.aws.s3.bucket5}/outer")
    private String bucket5_outer;
    @Value("${cloud.aws.s3.bucket5}/top")
    private String bucket5_top;
    @Value("${cloud.aws.s3.bucket5}/shoes")
    private String bucket5_shoes;
    @Value("${cloud.aws.s3.bucket5}/pants")
    private String bucket5_pants;

    @GetMapping("/getMycloset2/{id}")
    public ResponseEntity<Map<String, Object>> getImageUrlsById2(@PathVariable String id) throws IOException {
        List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket4).getObjectSummaries();
        Map<String, List<Map<String, Object>>> imageUrlsByCategory = new HashMap<>();
        Map<String, Integer> imageCountByCategory = new HashMap<>();
        Arrays.asList("top", "pants", "outer", "shoes").forEach(category -> imageCountByCategory.put(category, 1));
        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String fileName = s3ObjectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucket4, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
            String userId = objectMetadata.getUserMetaDataOf("user-id");
            if (userId != null && userId.equals(id)) {
                String imageLink = amazonS3.getUrl(bucket4, fileName).toString();
                String folderName = fileName.split("/")[0];
                String categoryName = getCategoryName(folderName);

                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("number", imageCountByCategory.get(categoryName));
                imageInfo.put("imgsrc", imageLink);
                imageCountByCategory.put(categoryName, imageCountByCategory.get(categoryName) + 1);

                imageUrlsByCategory.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(imageInfo);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", id);
        response.put("images", imageUrlsByCategory);
        return ResponseEntity.ok(response);
    }

    private String getCategoryName(String folderName) {
        if (folderName.contains("top")) return "top";
        if (folderName.contains("pants")) return "pants";
        if (folderName.contains("outer")) return "outer";
        if (folderName.contains("shoes")) return "shoes";
        return "etc";
    }
    @DeleteMapping("/deleteMyCloset")  //내 옷장에서 이미지를 삭제하는 기능
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            functions.deleteMycloset(imageUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping(value = "/uploadTempCloset_outer", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String uploadImages(@RequestParam("files") List<MultipartFile> files, @RequestParam("userid") String userId) {
        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String key = UUID.randomUUID().toString() + "_" + fileName;

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                metadata.addUserMetadata("userid", userId);

                amazonS3.putObject(bucket5_outer, key, file.getInputStream(), metadata);
            }

            // All files uploaded successfully
            return "Images uploaded successfully!";
        } catch (Exception e) {
            // Upload failed
            return "Image upload failed.";
        }
    }

    @PostMapping(value = "/uploadTempCloset_top", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String uploadImage2(@RequestParam("file") MultipartFile file, @RequestParam("userid") String userId) {
        try {
            String fileName = file.getOriginalFilename();
            String key = UUID.randomUUID().toString() + "_" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.addUserMetadata("userid", userId);

            amazonS3.putObject(bucket5_top, key, file.getInputStream(), metadata);

            // 업로드가 성공적으로 완료되었다는 메시지를 반환합니다.
            return "Image uploaded successfully!";
        } catch (Exception e) {
            // 업로드 실패 시 예외 처리 로직을 추가합니다.
            return "Image upload failed.";
        }
    }
    @PostMapping(value = "/uploadTempCloset_pants", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String uploadImage3(@RequestParam("file") MultipartFile file, @RequestParam("userid") String userId) {
        try {
            String fileName = file.getOriginalFilename();
            String key = UUID.randomUUID().toString() + "_" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.addUserMetadata("userid", userId);

            amazonS3.putObject(bucket5_pants, key, file.getInputStream(), metadata);

            // 업로드가 성공적으로 완료되었다는 메시지를 반환합니다.
            return "Image uploaded successfully!";
        } catch (Exception e) {
            // 업로드 실패 시 예외 처리 로직을 추가합니다.
            return "Image upload failed.";
        }
    }
    @PostMapping(value = "/uploadTempCloset_shoes", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String uploadImage4(@RequestParam("file") MultipartFile file, @RequestParam("userid") String userId) {
        try {
            String fileName = file.getOriginalFilename();
            String key = UUID.randomUUID().toString() + "_" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.addUserMetadata("userid", userId);

            amazonS3.putObject(bucket5_shoes, key, file.getInputStream(), metadata);

            // 업로드가 성공적으로 완료되었다는 메시지를 반환합니다.
            return "Image uploaded successfully!";
        } catch (Exception e) {
            // 업로드 실패 시 예외 처리 로직을 추가합니다.
            return "Image upload failed.";
        }
    }
}
