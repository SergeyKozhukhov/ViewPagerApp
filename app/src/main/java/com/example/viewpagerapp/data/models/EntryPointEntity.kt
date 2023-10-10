package com.example.viewpagerapp.data.models

import com.fasterxml.jackson.annotation.JsonProperty


data class EntryPointEntity(
    @JsonProperty("id") val id: Int,
    @JsonProperty("image") val image: String,
)
