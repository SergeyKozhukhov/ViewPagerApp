package com.example.viewpagerapp.domain.content

import com.example.viewpagerapp.domain.ContentKey

sealed class Content : ContentKey {
    abstract override val id: Int
    abstract override val subId: Int
}