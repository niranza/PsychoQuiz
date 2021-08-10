package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.niran.psychoquiz.repositories.DatabaseLoaderRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: DatabaseLoaderRepository) : ViewModel() {

    init {
        loadData()
    }

    fun initViewModel() = Unit

    private fun loadData() = viewModelScope.launch { repository.loadDatabase() }
}

class MainViewModelFactory(private val repository: DatabaseLoaderRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}