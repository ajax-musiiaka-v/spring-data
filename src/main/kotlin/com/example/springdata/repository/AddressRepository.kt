package com.example.springdata.repository

import com.example.springdata.entity.AddressEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface AddressRepository : MongoRepository<AddressEntity, String>