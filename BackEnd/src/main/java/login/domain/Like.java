package acho.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "like_list")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Feed feed;

    public Like() {
    }

    public Like(User user, Feed feed) {
        this.user = user;
        this.feed = feed;
    }
}