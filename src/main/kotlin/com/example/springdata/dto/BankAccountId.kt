package com.example.springdata.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class BankAccountId @JsonCreator constructor(
    @param:JsonProperty("id") val id: String
)