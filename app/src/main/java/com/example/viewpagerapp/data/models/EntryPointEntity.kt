package com.example.viewpagerapp.data.models

import com.fasterxml.jackson.annotation.JsonProperty


data class EntryPointEntity(
    @JsonProperty("id") val id: Int,
    @JsonProperty("subId") val subId: Int,
    @JsonProperty("image") val image: String,
)
