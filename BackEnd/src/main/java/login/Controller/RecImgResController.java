package login.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import login.domain.WishlistData;
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
public class RecImgResController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Value("${cloud.aws.s3.bucket6}")
    private String bucket6;

    @PostMapping("/uploadWishlist")  //위시리스트에 이미지를 업로드하는 기능
    public ResponseEntity<List<String>> uploadImageUrls(@RequestBody WishlistData request) {
        try {
            List<String> uploadedImageUrls = functions.uploadUrlsWishlist(request.getImageBytes(), request.getUserId());
            return ResponseEntity.ok(uploadedImageUrls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/getRecResult/{id}")   //추천 받은 이미지 보여주기
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsById(@PathVariable String id) throws IOException {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> imageUrls = new ArrayList<>();

        List<S3ObjectSummary> s3ObjectSummaries = amazonS3.listObjects(bucket6).getObjectSummaries();

        int count = 1;
        for (S3ObjectSummary s3ObjectSummary : s3ObjectSummaries) {
            String fileName = s3ObjectSummary.getKey();
            S3Object s3Object = amazonS3.getObject(bucket6, fileName);
            ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

            String userId = objectMetadata.getUserMetaDataOf("user-id");

            if (userId.equals(id)) {
                String imageLink = amazonS3.getUrl(bucket6, fileName).toString();
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
}
