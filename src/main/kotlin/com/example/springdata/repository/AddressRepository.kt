package com.example.springdata.repository

import com.example.springdata.entity.Address
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : ReactiveMongoRepository<Address, ObjectId>