package com.example.module.repository;

import com.example.module.entity.ModuleEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModuleEntryRepository extends MongoRepository <ModuleEntry, ObjectId>{
}
