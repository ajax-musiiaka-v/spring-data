package com.example.springdata.repository

import com.example.springdata.entity.Address
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository : MongoRepository<Address, String>