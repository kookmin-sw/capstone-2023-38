package com.example.demo2;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity //User 클래스가 스프링이 시작할 때, MySQL에 테이블을 생성한다.
@Table(name = "user")
public class User {
    @Id //Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // auto_increment

    @Column(nullable = true, length = 50)
    private String userId;

    @Column(nullable = true, length = 50)
    private String email;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Like> likes;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Feed> feeds;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Wishlist> wishlist;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
