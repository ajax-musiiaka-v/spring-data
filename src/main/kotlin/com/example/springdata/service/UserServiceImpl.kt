package com.example.springdata.service

import com.example.springdata.entity.Address
import com.example.springdata.entity.User
import com.example.springdata.repository.AddressRepository
import com.example.springdata.repository.BankAccountRepository
import com.example.springdata.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository,
                      private val addressRepository: AddressRepository
                      ) : UserService {

    override fun createUser(userName: String, userEmail: String): User {
        val userAddress = createAddress()
        val savedAddress = addressRepository.save(userAddress)

        val user = User(
                name = userName,
                email = userEmail,
                addressId = savedAddress.id,
                enabled = true
            )

        val savedUser = userRepository.save(user)

        return savedUser
    }

    override fun findById(userId: ObjectId): User? =
        userRepository.findById(userId).get()

    override fun updateUser(user: User): User =
        userRepository.save(user)

    override fun findByBankAccountId(bankAccountId: ObjectId): User? =
        userRepository.findByBankAccountId(bankAccountId)

    override fun getAll(): Collection<User> =
        userRepository.findAll()

    override fun deleteUser(id: String) {
        val addressId = userRepository.findById(ObjectId(id)).get().addressId
        if (addressId != null) {
            addressRepository.deleteById(addressId)
        }
        userRepository.deleteById(ObjectId(id))
    }

    override fun findByName(name: String): User? =
        userRepository.findByName(name)

    override fun findByEmail(email: String): User? =
        userRepository.findByEmail(email)

    private fun createAddress(): Address =
        Address(
            street = "street",
            city = "city"
        )
}