package acho.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecStartData {
    private List<String> imageUrls;
    private List<String> paths;
    private String userId;
    private String season;

}