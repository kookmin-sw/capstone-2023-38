package acho.Controller;

import com.amazonaws.services.s3.AmazonS3;
import acho.domain.MyCloset;
import acho.domain.User;
import acho.repository.MyClosetRepository;
import acho.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class MyClosetController {
    private AI ai;
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyClosetRepository myClosetRepository;

    @Value("${cloud.aws.s3.bucket4}")
    private String bucket4;

    @Value("${cloud.aws.s3.bucket4}/outer")
    private String bucket4_outer;
    @Value("${cloud.aws.s3.bucket4}/top")
    private String bucket4_top;
    @Value("${cloud.aws.s3.bucket4}/shoes")
    private String bucket4_shoes;
    @Value("${cloud.aws.s3.bucket4}/bottom")
    private String bucket4_bottom;
    @Value("${cloud.aws.s3.bucket4}/accessory")
    private String bucket4_accessory;

    @GetMapping("/getMycloset2/{id}")
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

    @DeleteMapping("/deleteMyCloset")
    @Operation(summary = "내 옷장에서 이미지를 삭제하는 기능 - 완료")
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            // URL에서 버킷, 폴더 및 파일 이름 추출
            String bucketName = functions.getBucketNameFromUrl(imageUrl);
            String folderName = functions.getFolderNameFromUrl(imageUrl);
            String fileName = functions.getFileNameFromUrl(imageUrl);

            System.out.println("Bucket Name: " + bucketName);
            System.out.println("Folder Name: " + folderName);
            System.out.println("File Name: " + fileName);

            // S3에서 이미지 삭제
            functions.deleteImageFromS3(bucketName, folderName, fileName);

            // mycloset 테이블에서 해당 이미지 정보 삭제
            MyCloset myCloset = myClosetRepository.findByUrl("https://s3.ap-northeast-2.amazonaws.com/" + bucketName + "/" + folderName + "/" + fileName);
            if (myCloset != null) {
                myClosetRepository.delete(myCloset);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/outer/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "outer 카테고리에 이미지를 저장하고 데이터는 mysql에 저장 - 완료")
    public String uploadOuterImagesToBucket(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("userId") String userId
    ) {
        String result = "Wrong Category";
        String expected = "outer";
        for (MultipartFile file : files) {
            result = ai.classify(file, expected);
            if(result.equals(expected)){
                functions.uploadImagesS3andMysql(files, userId, MyCloset.ClothingCategory.OUTER, bucket4_outer);
            }
        }
        return result;
        //return functions.uploadImagesS3andMysql(files, userId, MyCloset.ClothingCategory.OUTER, bucket4_outer);
    }

    @PostMapping(value = "/top/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "top 카테고리에 이미지를 저장하고 데이터는 mysql에 저장 - 완료")
    public String uploadTopImagesToBucket(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("userId") String userId
    ) {
        String result = "Wrong Category";
        String expected = "top";
        for (MultipartFile file : files) {
            result = ai.classify(file, expected);
            if(result.equals(expected)){
                functions.uploadImagesS3andMysql(files, userId, MyCloset.ClothingCategory.OUTER, bucket4_top);
            }
        }
        return result;
    }

    @PostMapping(value = "/bottom/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "bottom 카테고리에 이미지를 저장하고 데이터는 mysql에 저장 - 완료")
    public String uploadBottomImagesToBucket(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("userId") String userId
    ) {
        String result = "Wrong Category";
        String expected = "bottom";
        for (MultipartFile file : files) {
            result = ai.classify(file, expected);
            if(result.equals(expected)){
                functions.uploadImagesS3andMysql(files, userId, MyCloset.ClothingCategory.OUTER, bucket4_bottom);
            }
        }
        return result;
    }

    @PostMapping(value = "/shoes/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "shoes 카테고리에 이미지를 저장하고 데이터는 mysql에 저장 - 완료")
    public String uploadShoesImagesToBucket(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("userId") String userId
    ) {
        String result = "Wrong Category";
        String expected = "shoes";
        for (MultipartFile file : files) {
            result = ai.classify(file, expected);
            if(result.equals(expected)){
                functions.uploadImagesS3andMysql(files, userId, MyCloset.ClothingCategory.OUTER, bucket4_shoes);
            }
        }
        return result;
    }

    @PostMapping(value = "/accessory/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "accessory 카테고리에 이미지를 저장하고 데이터는 mysql에 저장 - 완료")
    public String uploadAccessoryImagesToBucket(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("userId") String userId
    ) {
        String result = "Wrong Category";
        String expected = "accessory";
        for (MultipartFile file : files) {
            result = ai.classify(file, expected);
            if(result.equals(expected)){
                functions.uploadImagesS3andMysql(files, userId, MyCloset.ClothingCategory.OUTER, bucket4_accessory);
            }
        }
        return result;
    }

}