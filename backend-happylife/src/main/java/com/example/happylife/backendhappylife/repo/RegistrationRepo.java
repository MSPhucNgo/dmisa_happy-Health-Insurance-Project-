package com.example.happylife.backendhappylife.repo;

import com.example.happylife.backendhappylife.entity.Registration;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepo extends MongoRepository<Registration, ObjectId> {
    List<Registration> findByCustomerInfo_Id(String id);
    Optional<Registration> findByProductInfo_PlanId(String planId);
    @Query("{ 'productInfo.planId': ?0, 'approvalStatus': { $in: ?1 } }")
    List<Registration> findAllByProductInfoAndApprovalStatus(String id, List<String> approvalStatusList);

    @Query("{ 'productInfo.planId': ?0 }")
    List<Registration> findAllByPlanId(String id);

    default void setMongoTemplate(MongoTemplate mongoTemplate) {
        // Setter for MongoTemplate
    }
    //List<Registration> findAllByCustomerInfo_Id(String id);
}

