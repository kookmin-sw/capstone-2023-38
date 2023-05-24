package com.example.demo2;

import jakarta.persistence.*;

import java.util.Objects;

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

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getUrl() {
        return url;
    }

    public ClothingCategory getCategory() {
        return category;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCategory(ClothingCategory category) {
        this.category = category;
    }
}
