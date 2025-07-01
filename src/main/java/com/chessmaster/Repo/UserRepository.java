package com.chessmaster.Repo;

import org.springframework.data.repository.CrudRepository;
import com.chessmaster.Models.Users;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
}