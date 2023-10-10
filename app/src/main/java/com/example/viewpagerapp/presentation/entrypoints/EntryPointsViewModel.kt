package com.example.viewpagerapp.presentation.entrypoints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.viewpagerapp.data.DataSource
import com.example.viewpagerapp.data.converters.EntryPointConverter
import com.example.viewpagerapp.data.Repository
import com.example.viewpagerapp.data.converters.ContentConverter
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EntryPointsViewModel(
    private val repository: Repository
) : ViewModel() {

    val uiState get() = _uiState.asStateFlow()
    private val _uiState = MutableStateFlow(EntryPointsUiState())

    fun getEntryPoints() = viewModelScope.launch {
        _uiState.update { current -> current.copy(isLoading = true) }
        val entryPoints = repository.getEntryPoints()
        _uiState.update { current -> current.copy(isLoading = false, entryPoints = entryPoints) }
    }

    companion object {

        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val repository =
                    Repository(
                        dataSource = DataSource(
                            context = application,
                            objectMapper = ObjectMapper()
                        ),
                        entryPointConverter = EntryPointConverter(),
                        contentConverter = ContentConverter()
                    )
                return EntryPointsViewModel(repository) as T
            }
        }
    }
}