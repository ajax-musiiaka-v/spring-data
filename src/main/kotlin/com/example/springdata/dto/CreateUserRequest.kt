package com.example.springdata.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class CreateUserRequest @JsonCreator constructor(
    @param:JsonProperty("name") val name: String,
    @param:JsonProperty("email") val email: String
)
