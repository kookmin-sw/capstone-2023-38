package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.demo2.domain.FeedPageData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class FeedPageController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Value("${cloud.aws.s3.bucket2}")
    private String bucket2;

    @Value("${cloud.aws.s3.bucket3}")
    private String bucket3;
    @GetMapping("/getFeedpage/uploadTime")
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsSortedByUploadTime() throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();
        List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket2).getObjectSummaries();
        s3ObjectSummaries.sort(Comparator.nullsLast(Comparator.comparing(o -> {
            String fileName = o.getKey();
            S3Object s3Object = amazonS3.getObject(bucket2, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
            try {
                Date uploadTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(objectMetadata.getUserMetaDataOf("upload-time"));
                return uploadTime;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        })));

        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String fileName = s3ObjectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucket2, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

            String userId = objectMetadata.getUserMetaDataOf("user-id");

            Map<String, Object> imageMetadata = new HashMap<>();
            imageMetadata.put("imageUrl", amazonS3.getUrl(bucket2, fileName).toString());
            imageMetadata.put("userId", userId);
            imageMetadata.put("wcount", objectMetadata.getUserMetaDataOf("wcount"));
            imageMetadata.put("acount", objectMetadata.getUserMetaDataOf("acount"));
            imageMetadata.put("uploadTime", objectMetadata.getUserMetaDataOf("upload-time"));

            response.add(imageMetadata);
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/getFeedpage/wcount")
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsSortedByWcount() throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();

        List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket2).getObjectSummaries();
        s3ObjectSummaries.sort(Comparator.comparingInt((S3ObjectSummary o) -> {
            String fileName = o.getKey();
            S3Object s3Object = amazonS3.getObject(bucket2, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
            String acountStr = objectMetadata.getUserMetaDataOf("acount");
            return acountStr != null ? Integer.parseInt(acountStr) : 0;
        }).reversed());

        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String fileName = s3ObjectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucket2, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

            String userId = objectMetadata.getUserMetaDataOf("user-id");

            Map<String, Object> imageMetadata = new HashMap<>();
            imageMetadata.put("imageUrl", amazonS3.getUrl(bucket2, fileName).toString());
            imageMetadata.put("userId", userId);
            imageMetadata.put("wcount", objectMetadata.getUserMetaDataOf("wcount"));
            imageMetadata.put("acount", objectMetadata.getUserMetaDataOf("acount"));
            imageMetadata.put("uploadTime", objectMetadata.getUserMetaDataOf("upload-time"));

            response.add(imageMetadata);
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/getFeedpage/acount")
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsSortedByAcount() throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();

        List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket2).getObjectSummaries();
        s3ObjectSummaries.sort(Comparator.comparingInt((S3ObjectSummary o) -> {
            String fileName = o.getKey();
            S3Object s3Object = amazonS3.getObject(bucket2, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
            String acountStr = objectMetadata.getUserMetaDataOf("acount");
            return acountStr != null ? Integer.parseInt(acountStr) : 0;
        }).reversed());


        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String fileName = s3ObjectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucket2, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

            String userId = objectMetadata.getUserMetaDataOf("user-id");

            Map<String, Object> imageMetadata = new HashMap<>();
            imageMetadata.put("imageUrl", amazonS3.getUrl(bucket2, fileName).toString());
            imageMetadata.put("userId", userId);
            imageMetadata.put("wcount", objectMetadata.getUserMetaDataOf("wcount"));
            imageMetadata.put("acount", objectMetadata.getUserMetaDataOf("acount"));
            imageMetadata.put("uploadTime", objectMetadata.getUserMetaDataOf("upload-time"));

            response.add(imageMetadata);
        }

        return ResponseEntity.ok(response);
    }
    @PutMapping("/increaseCount")  //좋아요 수를 올려주는 기능
    public ResponseEntity<Void> incrementCounts(@RequestParam("imageUrl") String imageUrl, @RequestParam("userId") String userId) {
        try {
            functions.updateWcountAndAcount(imageUrl, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Scheduled(cron = "0 0 0 ? * MON", zone = "Asia/Seoul")  //주간 좋아요 초기화 기능
    public void weeklyReset() {
        try {
            List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket2).getObjectSummaries();
            for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
                String fileName = s3ObjectSummary.getKey();
                S3Object s3Object = amazonS3.getObject(bucket2, fileName);
                ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

                String wcountStr = objectMetadata.getUserMetaDataOf("wcount");
                if (wcountStr != null) {
                    int wcount = Integer.parseInt(wcountStr);
                    if (wcount > 0) {
                        // Update metadata with new wcount
                        ObjectMetadata newMetadata = new ObjectMetadata();
                        newMetadata.setUserMetadata(objectMetadata.getUserMetadata());
                        newMetadata.addUserMetadata("wcount", "0"); // 기존 메타데이터에 wcount 값을 추가하도록 수정

                        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket2, fileName, bucket2, fileName)
                                .withNewObjectMetadata(newMetadata);
                        amazonS3.copyObject(copyObjectRequest);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/getWishlist2/{id}")
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsById(@PathVariable String id) throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> imageUrls = new ArrayList<>();

        List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket3).getObjectSummaries();

        int count = 1;
        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String fileName = s3ObjectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucket3, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

            String userId = objectMetadata.getUserMetaDataOf("user-id");

            if (userId.equals(id)) {
                String imageLink = amazonS3.getUrl(bucket3, fileName).toString();
                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("number", count);
                imageMap.put("imgsrc", imageLink);
                imageUrls.add(imageMap);
                count++;
            }
        }

        Map<String, Object> idImageMap = new LinkedHashMap<>(); // LinkedHashMap 사용
        idImageMap.put("id", id);
        idImageMap.put("images", imageUrls);

        response.add(idImageMap);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/postFeedpage2")  //피드 페이지에 이미지를 업로드하는 기능
    public ResponseEntity<List<String>> uploadImageUrls2(@RequestBody FeedPageData request) {
        try {
            List<String> uploadedImageUrls = functions.uploadUrlsFeed(request.getImageUrls(), request.getUserId(), request.getWcount(), request.getAcount());
            return ResponseEntity.ok(uploadedImageUrls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
