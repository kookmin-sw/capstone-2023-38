package acho.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class AI {

    public String classify(MultipartFile file, String expected) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String AIserverUrl = "http://172.30.1.68:9900/classify";
            Map<String, String> requestBody = new HashMap<>();
            String fileName = file.getOriginalFilename();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            // MultiValueMap 생성 및 파일 첨부
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);
            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // HTTP 요청 엔티티 생성
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(AIserverUrl, HttpMethod.POST, requestEntity, String.class);
            String answer = responseEntity.getBody();
            return responseEntity.getBody();
            // 응답 반환
            //return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
            //return body.get("expected").toString().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }

    public  ResponseEntity<byte[]> recommand(List<String> imgList) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String AIserverUrl = "http://172.30.59.0:9900/recommand";
            //String classifyRequest = AIserverUrl + "/classify";
//            Map<String, String> requestBody = new HashMap<>();
//            String fileName = file.getOriginalFilename();
//            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
//                @Override
//                public String getFilename() {
//                    return file.getOriginalFilename();
//                }
//            };
//
//            // MultiValueMap 생성 및 파일 첨부
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", resource);
//
            Map<String, Object> requestBody = new HashMap<>();
//            List<String> imgList = new ArrayList<>();
//            imgList.add("201_1");
//            imgList.add("203_3");
            // 옷장에서 선택한 이미지
            requestBody.put("img", imgList);
            // 추천받고자 하는 카테고리
            String text = "summer";
            requestBody.put("text", text);
            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // HTTP 요청 엔티티 생성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);



   //         Object output = responseBody.get("output");
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(AIserverUrl, HttpMethod.POST, requestEntity, byte[].class);
            //이미지 file 응답 받기
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                byte[] imageBytes = responseEntity.getBody();
                String savePath = "C:/capston/ai/";  // 이미지를 저장할 경로 설정
                String newFileName = "new_image.jpg";  // 새로운 이미지 파일명 설정
                File saveFile = new File(savePath + newFileName);
                // 이미지 파일 저장
                Files.write(saveFile.toPath(), imageBytes);
               // return "New image saved successfully.";
                return responseEntity;
            } else {
                //return "Failed to receive new image from Flask server.";
                return responseEntity;
            }
            // 응답 반환
            //return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        }
    }
}
