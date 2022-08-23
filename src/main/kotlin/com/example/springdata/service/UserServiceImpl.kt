package com.example.springdata.service

import com.example.springdata.entity.Address
import com.example.springdata.entity.User
import com.example.springdata.repository.AddressRepository
import com.example.springdata.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserServiceImpl(private val userRepository: UserRepository,
                      private val addressRepository: AddressRepository
                      ) : UserService {

    override fun createUser(userName: String, userEmail: String): Mono<User> {
        val userAddress: Address = createAddress()
        val savedAddress: Mono<Address> = addressRepository.save(userAddress)

        return savedAddress.flatMap { userRepository.save(
            User(
                name = userName,
                email = userEmail,
                enabled = true
            )
        ) }
    }

     override fun findById(userId: ObjectId): Mono<User> =
         userRepository.findById(userId)
            .switchIfEmpty(Mono.error(NoSuchElementException()))

    override fun updateUser(user: User): Mono<User> =
        userRepository.save(user)

    override fun findByBankAccountId(bankAccountId: ObjectId): Mono<User> =
        userRepository.findByBankAccountId(bankAccountId)
            .switchIfEmpty(Mono.error(NoSuchElementException()))

    override fun getAll(): Flux<User> =
        userRepository.findAll()

    override fun deleteUser(userId: String): Mono<Void> {
        val userFound: Mono<User> = userRepository.findById(ObjectId(userId))

        return userFound
            .flatMap { user ->
                user.addressId?.let { addressRepository.deleteById(it) } // delete user's address
            }
            .then(
                userRepository.deleteById(ObjectId(userId)) // delete user
            )
    }

    override fun findByName(name: String): Mono<User> =
        userRepository.findByName(name)
            .switchIfEmpty(Mono.error(NoSuchElementException()))

    override fun findByEmail(email: String): Mono<User> =
        userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(NoSuchElementException()))

    private fun createAddress(): Address =
        Address(
            street = "street",
            city = "city"
        )
}
