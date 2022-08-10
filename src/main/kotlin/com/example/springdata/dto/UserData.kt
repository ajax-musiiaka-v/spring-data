package com.example.springdata.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class UserData @JsonCreator constructor(
    @param:JsonProperty("id") val id: String,
    @param:JsonProperty("name") val name: String,
    @param:JsonProperty("email") val email: String,
    @param:JsonProperty("enabled") val enabled: Boolean?
    // TODO add address and bank account
)
