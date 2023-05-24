package com.example.demo2.repository;

import com.example.demo2.Feed;
import com.example.demo2.MyCloset;
import com.example.demo2.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Integer> {
    List<Feed> findAllByOrderByUploadTimeAsc();
    List<Feed> findAllByOrderByLikeCountDesc();

    List<Feed> findByUser(User user);

    Feed findByUrl(String url);
}
