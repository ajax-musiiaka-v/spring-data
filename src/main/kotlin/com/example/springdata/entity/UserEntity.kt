package com.example.springdata.entity

import java.util.*
import javax.persistence.*


@Entity
@Table(name = "APP_USERS")
class UserEntity {

    @Id
    @Column(name = "id")
    internal lateinit var id: String

    @OneToOne
    @JoinColumn(name = "address")
    internal lateinit var address: AddressEntity

    @Column(name = "name")
    internal lateinit var name: String

    @Column(name = "email")
    internal lateinit var email: String

    @Column(name = "enabled")
    internal var enabled: Boolean? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is UserEntity){
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
