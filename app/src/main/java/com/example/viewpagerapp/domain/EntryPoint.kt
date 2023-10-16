package com.example.viewpagerapp.domain

data class EntryPoint(
    override val id: Int,
    override val subId: Int,
    val image: String,
) : ContentKey
