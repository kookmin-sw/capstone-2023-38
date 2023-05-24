package acho.repository;

import acho.domain.MyCloset;
import acho.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyClosetRepository extends JpaRepository<MyCloset, Integer> {
    // You can add custom repository methods here if needed
    List<MyCloset> findByUser(User user);
    MyCloset findByUrl(String url);

}