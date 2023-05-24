package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.demo2.*;
import com.example.demo2.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;

@Slf4j
@RequiredArgsConstructor
@Service
public class Functions {

    private final UserRepository userRepository;
    private final MyClosetRepository myClosetRepository;
    private final FeedRepository feedRepository;
    private final WishlistRepository wishlistRepository;
    private final LikeRepository likeRepository;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket2}")
    private String bucket2;

//-----------------------------------------------------------------------------------------------------------
    //url을 통한 피드 페이지 등록
    public List<String> uploadUrlsGeneric(String imageUrl) throws IOException {
        List<String> uploadedImageUrls = new ArrayList<>();

        URL url = new URL(imageUrl);
        File downloadFile = download(url)
                .orElseThrow(() -> new IllegalArgumentException("URL 다운로드 실패"));
        String uploadedImageUrl = uploadGeneric(downloadFile, (file, fileName) -> putS3Image(file, fileName));
        uploadedImageUrls.add(uploadedImageUrl);

        return uploadedImageUrls;
    }

    public String uploadGeneric(File uploadFile, BiFunction<File, String, String> putS3Method) {
        String fileName = uploadFile.getName();
        String uploadImageUrl = putS3Method.apply(uploadFile, fileName);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    public List<String> uploadUrlsFeed(String imageUrl) throws IOException {
        return uploadUrlsGeneric(imageUrl);
    }

    public String uploadFeed(File uploadFile) {
        return uploadGeneric(uploadFile, (file, name) -> putS3Image(file, name));
    }

    private String putS3Image(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket2, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3.getUrl(bucket2, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> download(URL url) throws IOException {
        String originalFileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
        String modifiedFileName = UUID.randomUUID() + "_" + originalFileName;

        File downloadFile = new File(modifiedFileName);
        if(downloadFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(downloadFile)) {
                fos.write(url.openStream().readAllBytes());
            }
            return Optional.of(downloadFile);
        }
        return Optional.empty();
    }

//-----------------------------------------------------------------------------------------------------------
    //form data를 통한 내 옷장 등록
    public String uploadImagesS3andMysql(List<MultipartFile> files, String userId, MyCloset.ClothingCategory category, String bucketName) {
        try {
            // Save user information to MySQL
            User user = userRepository.findByUserId(userId);
            if (user == null) {
                user = new User();
                user.setUserId(userId);
            }
            userRepository.save(user);

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String key = UUID.randomUUID().toString() + "_" + fileName;

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());

                amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);

                // Save image URL and information to MyCloset entity
                MyCloset myCloset = new MyCloset();
                myCloset.setUser(userRepository.findByUserId(userId));
                String objectUrl = amazonS3.getUrl(bucketName, key).toString();
                myCloset.setUrl(objectUrl);
                myCloset.setCategory(category);
                myClosetRepository.save(myCloset);
            }

            return "Images uploaded successfully!";
        } catch (Exception e) {
            return "Image upload failed.";
        }
    }

//-----------------------------------------------------------------------------------------------------------
    //form data를 통한 위시리스트 등록
    public String uploadImagesS3andMysql2(List<MultipartFile> files, String userId, String bucketName) {
        try {
            // Save user information to MySQL
            User user = userRepository.findByUserId(userId);
            if (user == null) {
                user = new User();
                user.setUserId(userId);
            }
            userRepository.save(user);

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String key = UUID.randomUUID().toString() + "_" + fileName;

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());

                amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);

                // Save image URL and information to Wishlist entity
                Wishlist wishlist = new Wishlist();
                wishlist.setUser(userRepository.findByUserId(userId));
                String objectUrl = amazonS3.getUrl(bucketName, key).toString();
                wishlist.setUrl(objectUrl);
                wishlistRepository.save(wishlist);
            }

            return "Images uploaded successfully!";
        } catch (Exception e) {
            return "Image upload failed.";
        }
    }

//-----------------------------------------------------------------------------------------------------------
    //카테고리가 있는 경우
    public void deleteImageFromS3(String bucketName, String folderName, String fileName) {
        String key = folderName + "/" + fileName;
        amazonS3.deleteObject(bucketName, key);
    }

    public static String getBucketNameFromUrl(String imageUrl) {
        int startIndex = imageUrl.indexOf("/", 30);
        int endIndex = imageUrl.indexOf("/", 40);

        return imageUrl.substring(startIndex + 1, endIndex);
    }

    public static String getFolderNameFromUrl(String imageUrl) {
        int startIndex = imageUrl.indexOf("/", 40);
        int endIndex = imageUrl.lastIndexOf("/");

        return imageUrl.substring(startIndex + 1, endIndex);
    }

    public static String getFileNameFromUrl(String imageUrl) {
        int startIndex = imageUrl.lastIndexOf("/") + 1;
        int endIndex = imageUrl.length();

        return imageUrl.substring(startIndex, endIndex);
    }

//-----------------------------------------------------------------------------------------------------------
    //카테고리가 없는 경우
    public void deleteImageFromS32(String bucketName, String fileName) {
        String key = fileName;
        amazonS3.deleteObject(bucketName, key);
}
    public static String getBucketNameFromUrl2(String imageUrl) {
        int startIndex = imageUrl.indexOf("//") + 2;
        int endIndex = imageUrl.indexOf(".", startIndex);

        return imageUrl.substring(startIndex, endIndex);
    }

    public static String getFileNameFromUrl2(String imageUrl) {
        int startIndex = imageUrl.lastIndexOf("/") + 1;
        int endIndex = imageUrl.length();

        return imageUrl.substring(startIndex, endIndex);
    }

//-----------------------------------------------------------------------------------------------------------

    public void updateAcount(String imageUrl, String userId) {
        User user = userRepository.findByUserId(userId);
        Feed feed = feedRepository.findByUrl(imageUrl);

        // Check if the user has already liked the feed
        Like existingLike = likeRepository.findByUserAndFeed(user, feed);
        if (existingLike == null) {
            // Create and save new Like entity
            Like like = new Like();
            like.setUser(user);
            like.setFeed(feed);
            likeRepository.save(like);

            // Increment like count in the Feed entity
            int likeCount = feed.getLikeCount();
            likeCount++;
            feed.setLikeCount(likeCount);
            feedRepository.save(feed);
        } else {
            // User has already liked the feed, remove like
            likeRepository.delete(existingLike);

            // Decrement like count in the Feed entity
            int likeCount = feed.getLikeCount();
            likeCount--;
            feed.setLikeCount(likeCount);
            feedRepository.save(feed);
        }
    }


}
