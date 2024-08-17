package com.blog.som;

import com.blog.som.domain.test.RestAPI;

public class Test {
    public static void main(String[] args) {
        RestAPI restAPI = new RestAPI();
        //restAPI.getPosts();

        String randomPhoto = restAPI.getRandomThumbnail();
        System.out.println(randomPhoto);
    }
}
