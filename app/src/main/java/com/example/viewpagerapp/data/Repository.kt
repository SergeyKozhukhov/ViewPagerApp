package com.example.viewpagerapp.data

import com.example.viewpagerapp.data.converters.ContentConverter
import com.example.viewpagerapp.data.converters.EntryPointConverter
import com.example.viewpagerapp.domain.content.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class Repository(
    private val dataSource: DataSource,
    private val entryPointConverter: EntryPointConverter,
    private val contentConverter: ContentConverter,
) {

    suspend fun getEntryPoints() = withContext(Dispatchers.IO) {
        entryPointConverter.convert(dataSource.getEntryPoints())
    }

    suspend fun getContent(id: Int): Content = withContext(Dispatchers.IO) {
        delay(2000)
        contentConverter.convert(dataSource.getContent(id))
    }
}