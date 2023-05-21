package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class MyLikeImgController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Value("${cloud.aws.s3.bucket2}")
    private String bucket2;

    @GetMapping("/getMylike/{id}")
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsById(@PathVariable String id) throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> imageUrls = new ArrayList<>();

        List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket2).getObjectSummaries();

        int count = 1;
        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String fileName = s3ObjectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucket2, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

            String likedKey = id + "-liked";
            boolean liked = objectMetadata.getUserMetadata().containsKey(likedKey) && "true".equals(objectMetadata.getUserMetaDataOf(likedKey));
            if (liked) {
                Map<String, Object> imageMap = new LinkedHashMap<>();
                imageMap.put("number", count);
                imageMap.put("imgsrc", amazonS3.getUrl(bucket2, fileName).toString());
                imageMap.put("imageUserId", objectMetadata.getUserMetaDataOf("user-id"));
                imageMap.put("upload-time", objectMetadata.getUserMetaDataOf("upload-time"));
                imageMap.put("acount", objectMetadata.getUserMetaDataOf("acount"));

                imageUrls.add(imageMap);
                count++;
            }
        }
        Map<String, Object> idImageMap = new LinkedHashMap<>();
        idImageMap.put("id", id);
        idImageMap.put("images", imageUrls);

        response.add(idImageMap);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deliteMylike/{id}")
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl, @PathVariable String id) {
        try {
            String bucket = bucket2;
            String key = getImageKeyFromUrl(imageUrl);
            ObjectMetadata metadata = amazonS3.getObjectMetadata(bucket, key);

            String likedKey = id + "-liked";
            boolean liked = metadata.getUserMetadata().containsKey(likedKey) && "true".equals(metadata.getUserMetaDataOf(likedKey));
            if (liked) {
                ObjectMetadata newMetadata = new ObjectMetadata();
                for (Map.Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
                    if (!entry.getKey().equals(likedKey)) {
                        newMetadata.addUserMetadata(entry.getKey(), entry.getValue());
                    }
                }

                String acountStr = metadata.getUserMetaDataOf("acount");
                int acount = acountStr != null ? Integer.parseInt(acountStr) : 0;
                acount--; // acount 값을 1 감소시킴

                newMetadata.addUserMetadata("acount", String.valueOf(acount)); // 감소된 acount 값을 새로운 메타데이터에 추가

                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, key, bucket, key)
                        .withNewObjectMetadata(newMetadata)
                        .withCannedAccessControlList(CannedAccessControlList.PublicRead);
                amazonS3.copyObject(copyObjectRequest);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    private String getImageKeyFromUrl(String imageUrl) throws MalformedURLException {
        URL url = new URL(imageUrl);
        String path = url.getPath();
        return path.substring(1);
    }
}
