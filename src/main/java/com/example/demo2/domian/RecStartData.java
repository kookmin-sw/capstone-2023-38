package com.example.demo2.domian;

import com.fasterxml.jackson.annotation.JsonProperty;
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