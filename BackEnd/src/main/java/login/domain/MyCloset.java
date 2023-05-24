package acho.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "my_closet")
public class MyCloset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    private ClothingCategory category;

    public enum ClothingCategory {
        OUTER,
        TOP,
        BOTTOM,
        SHOES,
        ACCESSORY
    }

    public MyCloset() {
    }

    public MyCloset(User user, String url, ClothingCategory category) {
        this.user = user;
        this.url = url;
        this.category = category;
    }
}