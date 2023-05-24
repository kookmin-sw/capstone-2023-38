package acho.Controller;

import com.amazonaws.services.s3.AmazonS3;

import io.swagger.v3.oas.annotations.Operation;
import acho.domain.Feed;
import acho.domain.Like;
import acho.domain.User;
import acho.repository.FeedRepository;
import acho.repository.LikeRepository;
import acho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private final LikeRepository likeRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FeedRepository feedRepository;

    @GetMapping("/getMylike/{id}")
    @Operation(summary = "내가 좋아요 누른 게시물을 보여주는 기능 - 완료")
    public ResponseEntity<List<Map<String, Object>>> getImageUrlsById(@PathVariable String id) {
        List<Map<String, Object>> response = new ArrayList<>();
        List<Map<String, Object>> imageUrls = new ArrayList<>();

        User user = userRepository.findByUserId(id);
        List<Like> likes = likeRepository.findByUser(user);

        int count = 1;
        for (Like like : likes) {
            Feed feed = like.getFeed();
            String imageUrl = feed.getUrl();

            Map<String, Object> imageMap = new LinkedHashMap<>();
            imageMap.put("number", count);
            imageMap.put("imgsrc", imageUrl);
            imageMap.put("imageUserId", feed.getUser().getUserId());
            imageMap.put("upload-time", feed.getUploadTime().toString());
            imageMap.put("like", feed.getLikeCount());

            imageUrls.add(imageMap);
            count++;
        }

        Map<String, Object> idImageMap = new LinkedHashMap<>();
        idImageMap.put("id", id);
        idImageMap.put("images", imageUrls);

        response.add(idImageMap);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteLike/{id}")
    @Operation(summary = "좋아요 누른 목록에서 삭제 - 완료")
    public ResponseEntity<Void> deleteLikeRecord(@PathVariable String id, @RequestParam("imageUrl") String imageUrl) {
        User user = userRepository.findByUserId(id);
        Feed feed = feedRepository.findByUrl(imageUrl);

        if (user != null && feed != null) {
            Like like = likeRepository.findByUserAndFeed(user, feed);

            if (like != null) {
                likeRepository.delete(like);
                int likeCount = feed.getLikeCount();
                likeCount--;
                feed.setLikeCount(likeCount);
                feedRepository.save(feed);
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.notFound().build();
    }
}