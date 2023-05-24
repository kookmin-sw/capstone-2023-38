package acho.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "wishlist")
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String url;

    public Wishlist() {
        // Default constructor required by JPA
    }

    public Wishlist(User user, String url) {
        this.user = user;
        this.url = url;
    }
}