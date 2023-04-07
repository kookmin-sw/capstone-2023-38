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

            // Check if "liked-by" metadata contains the given id
            String likedBy = objectMetadata.getUserMetaDataOf("liked-by");
            if (Arrays.asList(likedBy.split(",")).contains(id)) {
                Map<String, Object> imageMap = new LinkedHashMap<>();
                imageMap.put("number", count);
                imageMap.put("imgsrc", amazonS3.getUrl(bucket2, fileName).toString());
                imageMap.put("user-id", objectMetadata.getUserMetaDataOf("user-id"));
                imageMap.put("upload-time", objectMetadata.getUserMetaDataOf("upload-time"));
                imageMap.put("wcount", objectMetadata.getUserMetaDataOf("wcount"));
                imageMap.put("acount", objectMetadata.getUserMetaDataOf("acount"));

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
    @DeleteMapping("/deliteMylike/{id}") //내거 좋아요 누른 목록에서 이미지를 삭제하는 기능
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl, @PathVariable String id) {
        try {
// Get object metadata for the S3 object with the given image URL
            String bucket = bucket2;
            String key = getImageKeyFromUrl(imageUrl);
            ObjectMetadata metadata = amazonS3.getObjectMetadata(bucket, key);

            String likedBy = metadata.getUserMetaDataOf("liked-by");
            if (likedBy != null && likedBy.contains(id)) {

                List<String> likedByList = new ArrayList<>(Arrays.asList(likedBy.split(",")));
                likedByList.remove(id);
                String updatedLikedBy = String.join(",", likedByList);


                ObjectMetadata newMetadata = new ObjectMetadata();
                for (Map.Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
                    newMetadata.addUserMetadata(entry.getKey(), entry.getValue());
                }

                newMetadata.addUserMetadata("liked-by", updatedLikedBy);

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
