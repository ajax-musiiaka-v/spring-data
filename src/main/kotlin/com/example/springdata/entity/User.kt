package com.example.springdata.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "users")
data class User(
    @Id internal val id: ObjectId = ObjectId.get(),
    internal var name: String,
    internal var email: String,
    internal var address: Address,
    internal var enabled: Boolean? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is User) {
            return Objects.equals(id, other.id)
        }

        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "UserEntity{id='$id', name='$name', email='$email', enabled=$enabled}"
    }
}
