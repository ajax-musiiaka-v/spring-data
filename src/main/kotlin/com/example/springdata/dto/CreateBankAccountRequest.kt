package com.example.springdata.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class CreateBankAccountRequest @JsonCreator constructor(
    @param:JsonProperty("userId") val userId: String,
    @param:JsonProperty("name") val name: String
)