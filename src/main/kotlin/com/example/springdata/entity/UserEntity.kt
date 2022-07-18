package com.example.springdata.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id;
import java.util.*

@Document(collection="users")
class UserEntity {

    @Id
    internal lateinit var id: String

    internal lateinit var address: AddressEntity

    internal lateinit var name: String

    internal lateinit var email: String

    internal var enabled: Boolean? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is UserEntity) {
            return Objects.equals(id, other.id)
        }

        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                '}'
    }
}
