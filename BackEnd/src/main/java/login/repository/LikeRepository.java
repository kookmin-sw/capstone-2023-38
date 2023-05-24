package acho.repository;


import acho.domain.Feed;
import acho.domain.Like;
import acho.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    Like findByUserAndFeed(User user, Feed feed);

    List<Like> findByUser(User user);

    boolean existsByUserAndFeed(User user, Feed feed);
}
