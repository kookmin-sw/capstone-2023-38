package acho.Controller;

import com.amazonaws.services.s3.AmazonS3;
import io.swagger.v3.oas.annotations.Operation;
import acho.domain.MyCloset;
import acho.domain.User;
import acho.repository.MyClosetRepository;
import acho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class RecImgController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    private final AI ai;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyClosetRepository myClosetRepository;

    @Value("${cloud.aws.s3.bucket3}")
    private String bucket3;
    @GetMapping("/getMycloset/{id}")
    @Operation(summary = "내 옷장을 보여주는 기능, 이미지는 S3로부터 데이터는 mysql로부터 - 완료")
    public ResponseEntity<Map<String, Object>> getImageUrlsById2(@PathVariable String id) {
        User user = userRepository.findByUserId(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<MyCloset> myClosetList = myClosetRepository.findByUser(user);
        Map<String, List<Map<String, Object>>> imageUrlsByCategory = new HashMap<>();
        Map<String, Integer> imageCountByCategory = new HashMap<>();
        Arrays.asList("top", "bottom", "outer", "shoes", "accessory").forEach(category -> imageCountByCategory.put(category, 1));

        for (MyCloset myCloset : myClosetList) {
            String imageUrl = myCloset.getUrl();
            MyCloset.ClothingCategory category = myCloset.getCategory();
            String categoryName = category.name().toLowerCase();

            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("number", imageCountByCategory.get(categoryName));
            imageInfo.put("imgsrc", imageUrl);
            imageCountByCategory.put(categoryName, imageCountByCategory.get(categoryName) + 1);

            imageUrlsByCategory.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(imageInfo);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", id);
        response.put("images", imageUrlsByCategory);
        return ResponseEntity.ok(response);
    }

    private String getCategoryName(String folderName) {
        if (folderName.contains("top")) return "top";
        if (folderName.contains("bottom")) return "bottom";
        if (folderName.contains("outer")) return "outer";
        if (folderName.contains("shoes")) return "shoes";
        if (folderName.contains("accessory")) return "accessory";
        return "etc";
    }

    @PostMapping (value = "/recommand", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public byte[] recommandCoordi(
            @RequestParam("files") List<String> imgList,
            @RequestParam("text") String text){
        return ai.recommand(imgList, text).getBody();
    }

    @PostMapping(value = "/wishlist/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "위시리스트에 이미지를 저장하고 데이터는 mysql에 저장 - 완료")
    public String uploadWishlistImagesToBucket(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("userId") String userId
    ) {
        return functions.uploadImagesS3andMysql2(files, userId, bucket3);
    }
}
