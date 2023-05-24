package com.example.demo2.repository;

import com.example.demo2.MyCloset;
import com.example.demo2.User;
import com.example.demo2.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    // You can add custom repository methods here if needed
    List<Wishlist> findByUser(User user);

    Wishlist findByUrl(String url);
}
