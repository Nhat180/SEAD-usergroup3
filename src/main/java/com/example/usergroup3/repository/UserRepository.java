package com.example.usergroup3.repository;

import com.example.usergroup3.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName (String name);
    Optional<User> findByEmail (String email);
    Page<User> findAllByRole (String role, Pageable pageable);
    List<User> findAllByType (String type);

    @Query("SELECT u FROM User u WHERE CONCAT(u.name, ' ', u.email) LIKE %?1%")
    public Page<User> search(String keyword, Pageable pageable);
}
