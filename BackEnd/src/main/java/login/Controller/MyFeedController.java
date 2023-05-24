package login.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class MyFeedController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Value("${cloud.aws.s3.bucket2}")
    private String bucket2;

    @GetMapping("/getMYfeed/{id}")  //feedpage 버킷에서 내가 올린 게시글 이미지만 조회
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsById(@PathVariable String id) throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> imageUrls = new ArrayList<>();

        List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket2).getObjectSummaries();

        int count = 1;
        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String fileName = s3ObjectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucket2, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

            String userId = objectMetadata.getUserMetaDataOf("user-id");

            if (userId.equals(id)) {
                String imageLink = amazonS3.getUrl(bucket2, fileName).toString();
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

    @DeleteMapping("/deliteMyfeed")  //feedpage 버킷에서 내가 올린 게시글 이미지를 삭제
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            functions.deleteMyfeed(imageUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
