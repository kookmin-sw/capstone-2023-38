package acho.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(AIserverUrl, HttpMethod.POST, requestEntity, String.class);
            String responseBody = responseEntity.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            String category = responseJson.get("category").asText();
            String filePath = responseJson.get("filePath").asText();
            if(category.equals(expected)){
                return filePath;
            }
            else {
                return "Wrong Category";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    public ResponseEntity<byte[]> recommand(List<String> imgList, String text) {
        RestTemplate restTemplate = new RestTemplate();
        String AIserverUrl = "http://172.30.59.0:9900/recommand";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("img", imgList);
        requestBody.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(AIserverUrl, HttpMethod.POST, requestEntity, byte[].class);

        return responseEntity;
    }
}
