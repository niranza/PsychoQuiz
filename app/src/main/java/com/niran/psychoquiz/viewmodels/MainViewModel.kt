package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.niran.psychoquiz.repositories.DatabaseLoaderRepository
import kotlinx.coroutines.launch

class MainViewModel(private val databaseLoaderRepository: DatabaseLoaderRepository) : ViewModel() {

    init {
        loadData()
    }

    fun initViewModel() = Unit

    private fun loadData() = viewModelScope.launch { databaseLoaderRepository.loadDatabase() }
}

class MainViewModelFactory(private val databaseLoaderRepository: DatabaseLoaderRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(databaseLoaderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}