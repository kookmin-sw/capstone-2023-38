package com.example.demo2.Controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo2.domian.WishlistData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@EnableScheduling
public class RecImgResController {
    private final AmazonS3 amazonS3;
    private final Functions functions;

    @PostMapping("/uploadWishlist")  //위시리스트에 이미지를 업로드하는 기능
    public ResponseEntity<List<String>> uploadImageUrls(@RequestBody WishlistData request) {
        try {
            List<String> uploadedImageUrls = functions.uploadUrlsWishlist(request.getImageUrls(), request.getUserId());
            return ResponseEntity.ok(uploadedImageUrls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
