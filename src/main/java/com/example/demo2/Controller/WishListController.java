package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.demo2.domain.FeedPageData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class WishListController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Value("${cloud.aws.s3.bucket3}")
    private String bucket3;

    @GetMapping("/getWishlist/{id}")
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


    @DeleteMapping("/deleteWishlist")  //위시리스트에서 이미지를 삭제하는 기능
    public ResponseEntity<Void> deleteImage2(@RequestParam("imageUrl") String imageUrl) {
        try {
            functions.deleteWishlist(imageUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/postFeedpage")  //피드 페이지에 이미지를 업로드하는 기능
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
