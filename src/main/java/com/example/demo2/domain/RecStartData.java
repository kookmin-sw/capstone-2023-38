package com.example.demo2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecStartData {
    //@JsonProperty("imageUrls")
    private List<String> imageUrls;

    //@JsonProperty("userId")
    private String userId;

    //@JsonProperty("season")
    private String season;
}