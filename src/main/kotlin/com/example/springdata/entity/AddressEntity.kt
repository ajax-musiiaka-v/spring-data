package com.example.springdata.entity

import javax.persistence.*

@Entity
@Table(name = "ADDRESSES")
class AddressEntity {

    @Id
    @Column(name = "id")
    internal lateinit var id: String

    @Column(name = "street")
    internal lateinit var street: String

    @Column(name = "city")
    internal lateinit var city: String
}