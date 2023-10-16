package com.example.viewpagerapp.data

import android.content.Context
import android.util.Log
import com.example.viewpagerapp.R
import com.example.viewpagerapp.data.models.ContentEntity
import com.example.viewpagerapp.data.models.EntryPointEntity
import com.example.viewpagerapp.domain.ContentKey
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

class DataSource(
    private val context: Context,
    private val objectMapper: ObjectMapper
) {

    private val typeReference = object : TypeReference<List<EntryPointEntity>>() {}

    fun getEntryPoints(): List<EntryPointEntity> = objectMapper.readValue(
        context.resources.openRawResource(R.raw.entry_points), typeReference
    )

    fun getContent(key: ContentKey, isNeedLog: Boolean = true): ContentEntity {
        val file = getFile(key)

        if (isNeedLog) {
            Log.d("ContentViewModel", "getContent: $key")
        }
        return objectMapper.readValue(
            context.resources.openRawResource(file),
            ContentEntity::class.java
        )
    }

    private fun getFile(key: ContentKey) = if (key.id == 1 && key.subId == 111) {
        ONE
    } else if (key.id == 2 && key.subId == 112) {
        TWO
    } else if (key.id == 3 && key.subId == 113) {
        THREE
    } else if (key.id == 4 && key.subId == 114) {
        FOUR
    } else if (key.id == 5 && key.subId == 115) {
        FIVE
    } else {
        throw IllegalArgumentException()
    }

    fun getContent(keys: List<ContentKey>): List<ContentEntity> {
        Log.d("ContentViewModel", "getContent: $keys")
        return keys.map { key -> getContent(key, false) }
    }

    private companion object {
        val ONE = R.raw.a1
        val TWO = R.raw.a2
        val THREE = R.raw.a3
        val FOUR = R.raw.a4
        val FIVE = R.raw.a5
    }
}