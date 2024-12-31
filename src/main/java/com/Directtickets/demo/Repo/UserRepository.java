package com.Directtickets.demo.Repo;

import com.Directtickets.demo.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);
    User findByUserName(String username);
    void deleteByUserName(String username);
}