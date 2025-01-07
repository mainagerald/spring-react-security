package com.auth.SECURITY.repository;


import com.auth.SECURITY.enums.Role;
import com.auth.SECURITY.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("select u from User u where u.role = :role")
    List<User> findByRole(@Param("role") Role role);
    @Query("select u from User u where u.role =:role")
    List<User> findByAdmin(Role role);
    User findByVerificationToken(String token);

    User findByPublicId(String publicId);
}
