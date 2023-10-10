package com.example.viewpagerapp.data

import android.content.Context
import android.util.Log
import com.example.viewpagerapp.R
import com.example.viewpagerapp.data.models.ContentEntity
import com.example.viewpagerapp.data.models.EntryPointEntity
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

private const val TAG = "StoriesScreen"

class DataSource(
    private val context: Context,
    private val objectMapper: ObjectMapper
) {

    private val typeReference = object : TypeReference<List<EntryPointEntity>>() {}

    fun getEntryPoints(): List<EntryPointEntity> = objectMapper.readValue(
        context.resources.openRawResource(R.raw.entry_points), typeReference
    )

    fun getContent(id: Int): ContentEntity {
        val file = when (id) {
            1 -> ONE
            2 -> TWO
            3 -> THREE
            4 -> FOUR
            5 -> FIVE
            else -> throw IllegalArgumentException()
        }

        Log.d(TAG, "getContent: $id")
        return objectMapper.readValue(
            context.resources.openRawResource(file), ContentEntity::class.java
        )
    }

    fun getContent2(id: Int): ContentEntity {
        val file = when (id) {
            1 -> ONE
            2 -> TWO
            3 -> THREE
            4 -> FOUR
            5 -> FIVE
            else -> throw IllegalArgumentException()
        }

        Log.d(TAG, "getContent: $id")
        return objectMapper.readValue(
            context.resources.openRawResource(file), ContentEntity::class.java
        )
    }

    private companion object {
        val ONE = R.raw.a1
        val TWO = R.raw.a2
        val THREE = R.raw.a3
        val FOUR = R.raw.a4
        val FIVE = R.raw.a5
    }
}