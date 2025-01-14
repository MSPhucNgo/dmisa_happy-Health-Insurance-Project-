package com.example.happylife.backendhappylife.repo;

import com.example.happylife.backendhappylife.entity.Contract;
import com.example.happylife.backendhappylife.entity.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepo extends MongoRepository<Contract, ObjectId> {
    List<Contract> findByRegisInfo_CustomerInfoId(String userId);
    Optional<Contract> findByRegisInfo_RegisId(String regisId);
}
