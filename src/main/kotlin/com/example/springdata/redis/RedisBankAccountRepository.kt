package com.example.springdata.redis

import com.example.springdata.entity.BankAccount;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RedisBankAccountRepository : CrudRepository<BankAccount, ObjectId>
