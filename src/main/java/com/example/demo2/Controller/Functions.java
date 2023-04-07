package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class Functions {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.bucket2}")
    private String bucket2;

    @Value("${cloud.aws.s3.bucket3}")
    private String bucket3;

    @Value("${cloud.aws.s3.bucket4}")
    private String bucket4;

    private String userID = null;

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<String> uploadUrls(List<String> imageUrls, int temperature, String userId) throws IOException {
        List<String> uploadedImageUrls = new ArrayList<>();
        setUserID(userId);
        for (String imageUrl : imageUrls) {
            URL url = new URL(imageUrl);
            File downloadFile = download(url)
                    .orElseThrow(() -> new IllegalArgumentException("URL 다운로드 실패"));
            String uploadedImageUrl = upload(downloadFile, temperature);
            uploadedImageUrls.add(uploadedImageUrl);
        }
        return uploadedImageUrls;
    }

    public String upload(File uploadFile, int temperature) {
        String fileName = uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName, temperature);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName, int temperature) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        // Save temperature metadata as a string value
        String temperatureString = Integer.toString(temperature);
        objectMetadata.addUserMetadata("temperature", temperatureString);

        objectMetadata.addUserMetadata("user-id", userID);
        //objectMetadata.addUserMetadata("upload-time", new Date().toString());

        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withMetadata(objectMetadata)
        );
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public List<String> uploadUrlsFeed(List<String> imageUrls, String userId, int wcount, int acount) throws IOException {
        List<String> uploadedImageUrls = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            URL url = new URL(imageUrl);
            File downloadFile = download(url)
                    .orElseThrow(() -> new IllegalArgumentException("URL 다운로드 실패"));
            String uploadedImageUrl = uploadFeed(downloadFile, userId, wcount, acount);
            uploadedImageUrls.add(uploadedImageUrl);
        }
        return uploadedImageUrls;
    }

    public String uploadFeed(File uploadFile, String userId, int wcount, int acount) {
        String fileName = uploadFile.getName();
        String uploadImageUrl = putS3Feed(uploadFile, fileName, userId, wcount, acount);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private String putS3Feed(File uploadFile, String fileName, String userId, int wcount, int acount) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.addUserMetadata("user-id", userId);
        objectMetadata.addUserMetadata("wcount", Integer.toString(wcount));
        objectMetadata.addUserMetadata("acount", Integer.toString(acount));
        objectMetadata.addUserMetadata("upload-time", new Date().toString());
        objectMetadata.addUserMetadata("liked-by", "");  // "liked-by" 이름의 빈 문자열을 추가

        amazonS3.putObject(
                new PutObjectRequest(bucket2, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withMetadata(objectMetadata)
        );
        return amazonS3.getUrl(bucket2, fileName).toString();
    }



    public List<String> uploadUrlsWishlist(List<String> imageUrls, String userId) throws IOException {
        List<String> uploadedImageUrls = new ArrayList<>();
        setUserID(userId);
        for (String imageUrl : imageUrls) {
            URL url = new URL(imageUrl);
            File downloadFile = download(url)
                    .orElseThrow(() -> new IllegalArgumentException("URL 다운로드 실패"));
            String uploadedImageUrl = uploadWishlist(downloadFile);
            uploadedImageUrls.add(uploadedImageUrl);
        }
        return uploadedImageUrls;
    }

    public String uploadWishlist(File uploadFile) {
        String fileName = uploadFile.getName();
        String uploadImageUrl = putS3Wishlist(uploadFile, fileName);

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private String putS3Wishlist(File uploadFile, String fileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        objectMetadata.addUserMetadata("user-id", userID);
        objectMetadata.addUserMetadata("upload-time", new Date().toString());

        amazonS3.putObject(
                new PutObjectRequest(bucket3, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
                        .withMetadata(objectMetadata)
        );
        return amazonS3.getUrl(bucket3, fileName).toString();
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
    public int getWcount(String imageUrl) {
        S3Object object = amazonS3.getObject(bucket2, getFileNameFromUrl(imageUrl));
        String wcountString = object.getObjectMetadata().getUserMetaDataOf("wcount");
        return Integer.parseInt(wcountString);
    }

    public int getAcount(String imageUrl) {
        S3Object object = amazonS3.getObject(bucket2, getFileNameFromUrl(imageUrl));
        String acountString = object.getObjectMetadata().getUserMetaDataOf("acount");
        return Integer.parseInt(acountString);
    }
    public void updateWcountAndAcount(String imageUrl, String userId) {
        String fileName = getFileNameFromUrl(imageUrl);
        S3Object object = amazonS3.getObject(bucket2, fileName);
        ObjectMetadata metadata = object.getObjectMetadata();
        // 중복된 아이디가 있는지 체크
        String likedByString = metadata.getUserMetaDataOf("liked-by");
        if (likedByString.contains(userId)) { // 이미 좋아요를 누른 경우
            return;
        }

        int wcount = Integer.parseInt(metadata.getUserMetaDataOf("wcount")) + 1;
        int acount = Integer.parseInt(metadata.getUserMetaDataOf("acount")) + 1;

        // 좋아요를 누른 사용자 아이디 추가
        String newLikedByString = likedByString + userId + ",";
        metadata.addUserMetadata("liked-by", newLikedByString);

        // wcount와 acount 업데이트
        metadata.addUserMetadata("wcount", Integer.toString(wcount));
        metadata.addUserMetadata("acount", Integer.toString(acount));

        CopyObjectRequest copyObjectMetadataRequest = new CopyObjectRequest(bucket2, fileName, bucket2, fileName)
                .withNewObjectMetadata(metadata);

        amazonS3.copyObject(copyObjectMetadataRequest);
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
        amazonS3.deleteObject(bucket4, fileName);
    }
    public void deleteMyfeed(String imageUrl) {  //피드에서 내가 올린 이미지 삭제, 즉 Feed버킷에서 삭제
        String fileName = getFileNameFromUrl(imageUrl);
        amazonS3.deleteObject(bucket2, fileName);
    }
}
