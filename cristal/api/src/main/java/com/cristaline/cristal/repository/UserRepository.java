package com.cristaline.cristal.repository;

import com.cristaline.cristal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
