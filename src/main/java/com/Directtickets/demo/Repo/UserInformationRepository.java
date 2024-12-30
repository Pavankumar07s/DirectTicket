package com.Directtickets.demo.Repo;

import com.Directtickets.demo.entity.UserInformation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserInformationRepository extends MongoRepository<UserInformation, String> {

    UserInformation findByEmail(String email);
}