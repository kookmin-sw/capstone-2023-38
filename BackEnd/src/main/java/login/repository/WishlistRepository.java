package acho.repository;


import acho.domain.User;
import acho.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    // You can add custom repository methods here if needed
    List<Wishlist> findByUser(User user);

    Wishlist findByUrl(String url);
}
