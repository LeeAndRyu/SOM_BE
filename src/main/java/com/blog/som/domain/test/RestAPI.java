package com.blog.som.domain.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RestAPI {

    private String getResponseJson(String url){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<Object> request = new HttpEntity<>(headers);



        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        return response.getBody();
    }

    public List<Map<String, Object>> getPosts(){
        String url = "https://jsonplaceholder.typicode.com/posts";

        String responseJson = getResponseJson(url);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> resultList;
        try {
            resultList = objectMapper.readValue(responseJson, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }

    public String getRandomThumbnail(){
        int num = new Random().nextInt(5000 + 1);
        String url = "https://jsonplaceholder.typicode.com/photos/" + num;

        String responseJson = getResponseJson(url);
        System.out.println(responseJson);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> result;
        try {
            result = objectMapper.readValue(responseJson, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return result.get("thumbnailUrl").toString();
    }
}
