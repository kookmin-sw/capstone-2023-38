package login.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedPageData {
    private List<String> imageUrls;
    private String userId;
    private int wcount=0;
    private int acount=0;
}
