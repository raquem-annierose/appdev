package com.pup.taguig.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pup.taguig.app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
