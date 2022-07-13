package com.example.springdata.repository

import com.example.springdata.entity.AddressEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AddressRepository : JpaRepository<AddressEntity,String> {
}