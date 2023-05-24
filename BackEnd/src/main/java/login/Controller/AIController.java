package login.Controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
public class AIController {
    //private String AIserverUrl = "http://192.168.35.177:9900";
    private final AI aiControll;
    //분류 모델
    @PostMapping (value = "/classify", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String a(@RequestParam("files") MultipartFile file, @RequestParam("expected") String expected){
        String response = aiControll.classify(file, expected);
        String category = "top";
        if(response.equals(category)){ // 옷장에 추가
            return "correct";
        }
        else{// 옷장에 추가 할 것인지 질문
            return response;
        }
    }


    //추천 모델
    @PostMapping (value = "/recommand", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String aa(@RequestParam("files") String path){
        List<String> imgList = new ArrayList<>();
            imgList.add(path);
            //imgList.add("203_3");
        ResponseEntity response = aiControll.recommand(imgList);
        return response.getStatusCode().toString();
    }
    @PostMapping (value = "/sendfile", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String testai(@RequestParam("files") MultipartFile file) throws IOException {
        String flaskEndpoint = "http://172.30.1.45:9900/testAI";
//        System.out.println(file.getName());
//        // MultipartFile을 File로 변환
//        File convertedFile = convertMultipartFileToFile(file);
//        // Multipart 요청을 위한 MultiValueMap 생성
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new FileSystemResource(convertedFile));
//
//        // HTTP 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        // HTTP 요청 생성
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        // RestTemplate을 사용하여 파일 업로드 요청 전송
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> responseEntity = restTemplate.exchange(flaskEndpoint, HttpMethod.POST, requestEntity, String.class);
//        // 응답 반환

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("image", contentsAsResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // RestTemplate을 사용하여 HTTP POST 요청 전송
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(flaskEndpoint, HttpMethod.POST, requestEntity, String.class);

        // 응답 반환
        return responseEntity.getBody();
    }
    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            // 파일 변환 실패 처리
            return null;
        }
    }
    @GetMapping ("testAI")
    public Object testAI() {
        RestTemplate restTemplate = new RestTemplate();
        String AIserverUrl = "http://192.168.35.35:9900/testAI";
        String testAIUrl = AIserverUrl + "/testAI";
        Map<String, String> requestBody = new HashMap<>();
        String clothes = "201_1 203_3";
        // 옷장에서 선택한 이미지
        requestBody.put("img", clothes);
        // 추천받고자 하는 카테고리
        String text = "winter";
        requestBody.put("text", text);
        Map<String, String> responseBody = restTemplate.postForObject(AIserverUrl, requestBody, Map.class);
        Object output = responseBody.get("output");
        // list 형태로 이미지 파일 이름 output
        return output;
    }
    @GetMapping("/aiConnect")
    public String a(){
        RestTemplate restTemplate = new RestTemplate();
        String AIserverUrl = "http://172.30.1.45:9900/connect";
        Map<String, String> requestBody = new HashMap<>();
        String text = "winter";
        requestBody.put("text", text);
        Map<String, String> responseBody = restTemplate.postForObject(AIserverUrl, requestBody, Map.class);
        Object output = responseBody.get("output");
        return output.toString();
    }
}