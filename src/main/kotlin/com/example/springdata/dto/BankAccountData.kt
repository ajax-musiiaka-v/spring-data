package com.example.springdata.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class BankAccountData @JsonCreator constructor(
    @param:JsonProperty("id") val id: String,
    @param:JsonProperty("bankAccountName") val name: String,
    @param:JsonProperty("balance") val balance: Double
    )
