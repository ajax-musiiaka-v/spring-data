package com.example.springdata.service

import com.example.springdata.entity.AddressEntity
import com.example.springdata.entity.UserEntity
import com.example.springdata.repository.AddressRepository
import com.example.springdata.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class UserServiceImpl(private val userRepository: UserRepository,
                      private val addressRepository: AddressRepository) : UserService {


    @Transactional
    override fun createUser(name: String, email: String): String {
        val id: String = UUID.randomUUID().toString()
        val address: AddressEntity = createAddress()
        val userEntity = UserEntity()

        userEntity.id = id
        userEntity.address = address
        userEntity.name = name
        userEntity.email = email
        userEntity.enabled = true

        addressRepository.save(address)
        userRepository.save(userEntity)

        return id
    }

    override fun getAll(): Collection<UserEntity> {
        return userRepository.findAll()
    }

    @Transactional
    override fun deleteUser(id: String) {
        val userEntity = UserEntity()
        userEntity.id = id
        userRepository.delete(userEntity)
    }

    private fun createAddress(): AddressEntity {
        val id = UUID.randomUUID().toString()
        val addressEntity = AddressEntity()
        addressEntity.id = id
        addressEntity.street = "street"
        addressEntity.city = "city"

        return addressEntity
    }
}