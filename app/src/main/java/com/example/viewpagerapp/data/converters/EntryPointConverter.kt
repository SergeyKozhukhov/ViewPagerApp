package com.example.viewpagerapp.data.converters

import com.example.viewpagerapp.data.models.EntryPointEntity
import com.example.viewpagerapp.domain.EntryPoint

class EntryPointConverter {

    fun convert(list: List<EntryPointEntity>) = list.map { convert(it) }

    private fun convert(source: EntryPointEntity) = EntryPoint(
        id = source.id,
        image = source.image
    )
}