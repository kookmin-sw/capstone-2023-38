package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
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

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket2}")
    private String bucket2;

    @Value("${cloud.aws.s3.bucket3}")
    private String bucket3;

    @Value("${cloud.aws.s3.bucket7}")
    private String bucket7;

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

    public List<String> uploadUrlsGeneric(List<String> imageUrls, String userId, BiFunction<File, String, String> uploadMethod) throws IOException {
        List<String> uploadedImageUrls = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            URL url = new URL(imageUrl);
            File downloadFile = download(url)
                    .orElseThrow(() -> new IllegalArgumentException("URL 다운로드 실패"));
            String uploadedImageUrl = uploadMethod.apply(downloadFile, userId);
            uploadedImageUrls.add(uploadedImageUrl);
        }
        return uploadedImageUrls;
    }

    public String uploadGeneric(File uploadFile, BiFunction<File, String, String> putS3Method, String userId) {
        String fileName = uploadFile.getName();
        String uploadImageUrl = putS3Method.apply(uploadFile, fileName);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    //--------------------------------피드 페이지 등록 기능--------------------------------------------------
    public List<String> uploadUrlsFeed(List<String> imageUrls, String userId, int acount) throws IOException {
        return uploadUrlsGeneric(imageUrls, userId, (file, uid) -> uploadFeed(file, uid, acount));
    }

    public String uploadFeed(File uploadFile, String userId, int acount) {
        return uploadGeneric(uploadFile, (file, name) -> putS3Feed(file, name, userId, acount), userId);
    }
    private String putS3Feed(File uploadFile, String fileName, String userId, int acount) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.addUserMetadata("user-id", userId);
        objectMetadata.addUserMetadata("acount", Integer.toString(acount));
        objectMetadata.addUserMetadata("upload-time", new Date().toString());

        amazonS3.putObject(
                new PutObjectRequest(bucket2, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withMetadata(objectMetadata)
        );
        return amazonS3.getUrl(bucket2, fileName).toString();
    }
    //------------------------------------------------------------------------------------------------

    //-----------------------------------위시리스트 등록 기능---------------------------------------------
    public List<String> uploadUrlsWishlist(List<String> imageUrls, String userId) throws IOException {
        return uploadUrlsGeneric(imageUrls, userId, (file, uid) -> uploadWishlist(file, uid));
    }

    public String uploadWishlist(File uploadFile, String userId) {
        return uploadGeneric(uploadFile, (file, name) -> putS3Wishlist(file, name, userId), userId);
    }

    private String putS3Wishlist(File uploadFile, String fileName, String userId) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.addUserMetadata("user-id", userId);
        objectMetadata.addUserMetadata("upload-time", new Date().toString());

        amazonS3.putObject(
                new PutObjectRequest(bucket3, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withMetadata(objectMetadata)
        );
        return amazonS3.getUrl(bucket3, fileName).toString();
    }
   //----------------------------------------------------------------------------------------------

   //-----------------------------추천 받을 이미지 임시 등록 기능---------------------------------------
   public List<String> uploadUrlsRecStart(List<String> imageUrls, String userId, String season) throws IOException {
       return uploadUrlsGeneric(imageUrls, userId, (file, uid) -> uploadWishlistRecStart(file, uid, season));
   }

    public String uploadWishlistRecStart(File uploadFile, String userId, String season) {
        return uploadGeneric(uploadFile, (file, name) -> putS3WishlistRecStart(file, name, userId, season), userId);
    }

    private String putS3WishlistRecStart(File uploadFile, String fileName, String userId, String season) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.addUserMetadata("user-id", userId);
        objectMetadata.addUserMetadata("season", season); // Add season as user metadata

        amazonS3.putObject(
                new PutObjectRequest(bucket7, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withMetadata(objectMetadata)
        );
        return amazonS3.getUrl(bucket7, fileName).toString();
    }

    //---------------------------------------------------------------------------------------------

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

    public int getAcount(String imageUrl) {
        S3Object object = amazonS3.getObject(bucket2, getFileNameFromUrl(imageUrl));
        String acountString = object.getObjectMetadata().getUserMetaDataOf("acount");
        return Integer.parseInt(acountString);
    }

    public void updateAcount(String imageUrl, String userId) {
        String fileName = getFileNameFromUrl(imageUrl);
        S3Object object = amazonS3.getObject(bucket2, fileName);
        ObjectMetadata metadata = object.getObjectMetadata();

        int acount = Integer.parseInt(metadata.getUserMetaDataOf("acount"));

        // 중복 처리를 위한 아이디 확인
        String likedKey = userId + "-liked";
        if (!metadata.getUserMetadata().containsKey(likedKey)) {
            // acount 값 1 증가
            acount++;

            // acount 업데이트
            metadata.addUserMetadata("acount", Integer.toString(acount));

            // Flag 값 추가
            metadata.addUserMetadata(likedKey, "true");

            CopyObjectRequest copyObjectMetadataRequest = new CopyObjectRequest(bucket2, fileName, bucket2, fileName)
                    .withNewObjectMetadata(metadata);

            amazonS3.copyObject(copyObjectMetadataRequest);
        } else {
            // 중복 처리된 아이디일 경우
            // Flag 값 삭제
            Map<String, String> updatedMetadata = new HashMap<>();
            for (Map.Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
                if (!entry.getKey().equals(likedKey)) {
                    updatedMetadata.put(entry.getKey(), entry.getValue());
                }
            }

            // acount 값 1 감소
            acount--;

            // acount 업데이트
            updatedMetadata.put("acount", Integer.toString(acount));

            ObjectMetadata newMetadata = new ObjectMetadata();
            newMetadata.setUserMetadata(updatedMetadata);

            CopyObjectRequest copyObjectMetadataRequest = new CopyObjectRequest(bucket2, fileName, bucket2, fileName)
                    .withNewObjectMetadata(newMetadata);

            amazonS3.copyObject(copyObjectMetadataRequest);
        }
    }


    private String getFileNameFromUrl(String imageUrl) {
        int slashIndex = imageUrl.lastIndexOf('/');
        int questionMarkIndex = imageUrl.lastIndexOf('?');

        if (questionMarkIndex > slashIndex) {
            return imageUrl.substring(slashIndex + 1, questionMarkIndex);
        } else {
            return imageUrl.substring(slashIndex + 1);
        }
    }

    public void deleteWishlist(String imageUrl) {  //위시리스트에서 이미지 삭제
        String fileName = getFileNameFromUrl(imageUrl);
        amazonS3.deleteObject(bucket3, fileName);
    }

    public void deleteMycloset(String imageUrl) {  //내 옷장에서 이미지 삭제
        String fileName = getFileNameFromUrl(imageUrl);
        amazonS3.deleteObject(bucket4_outer, fileName);
        amazonS3.deleteObject(bucket4_top, fileName);
        amazonS3.deleteObject(bucket4_bottom, fileName);
        amazonS3.deleteObject(bucket4_shoes, fileName);
        amazonS3.deleteObject(bucket4_accessory, fileName);
    }
    public void deleteMyfeed(String imageUrl) {  //피드에서 내가 올린 이미지 삭제, 즉 Feed버킷에서 삭제
        String fileName = getFileNameFromUrl(imageUrl);
        amazonS3.deleteObject(bucket2, fileName);
    }

    //임시 옷장 업로드
    public String uploadImagesToBucket(String bucket, List<MultipartFile> files, String userId) {
        try {
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String key = UUID.randomUUID().toString() + "_" + fileName;

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                metadata.addUserMetadata("user-id", userId);

                amazonS3.putObject(bucket, key, file.getInputStream(), metadata);
            }

            return "Images uploaded successfully!";
        } catch (Exception e) {
            return "Image upload failed.";
        }
    }
}
