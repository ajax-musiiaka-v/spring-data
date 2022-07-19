package com.example.springdata.service

import com.example.springdata.entity.Address
import com.example.springdata.entity.User
import com.example.springdata.repository.AddressRepository
import com.example.springdata.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository,
                      private val addressRepository: AddressRepository) : UserService {

    override fun createUser(userName: String, userEmail: String): User {
        val userAddress = createAddress()
        addressRepository.save(userAddress)

        val user = User(
                name = userName,
                email = userEmail,
                address = userAddress,
                enabled = true
            )
        userRepository.save(user)

        return user
    }

    override fun getAll(): Collection<User> {
        return userRepository.findAll()
    }

    fun findById(id: String): User =
        userRepository.findById(id).get()


    override fun deleteUser(id: String) {
        val userToDelete = findById(id)
        userRepository.delete(userToDelete)
    }

    private fun createAddress(): Address =
        Address(
            street = "street",
            city = "city"
        )
}