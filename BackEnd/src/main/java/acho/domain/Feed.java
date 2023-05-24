package acho.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "feed")
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int likeCount;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime uploadTime;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String url;

    public Feed() {
    }

    public Feed(int likeCount, LocalDateTime uploadTime, User user, String url) {
        this.likeCount = likeCount;
        this.uploadTime = uploadTime;
        this.user = user;
        this.url = url;
    }
}