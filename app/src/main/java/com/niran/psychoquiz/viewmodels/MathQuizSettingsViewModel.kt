package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import com.niran.psychoquiz.repositories.MathQuizSettingRepository
import com.niran.psychoquiz.utils.enums.MathType
import kotlinx.coroutines.launch

class MathQuizSettingsViewModel(
    private val repository: MathQuizSettingRepository
) : ViewModel() {

    fun getAllOutputOneSettingsAsLiveData(mathTypes: MathType) =
        repository.getAllOutputOneSettingsWithFlow(mathTypes).asLiveData()

    fun getAllOutputTwoSettingsAsLiveData(mathTypes: MathType) =
        repository.getAllOutputTwoSettingsWithFlow(mathTypes).asLiveData()

    fun insertBooleanSetting(booleanSetting: BooleanSetting) =
        viewModelScope.launch { repository.insertBooleanSetting(booleanSetting) }

    fun selectAllOutputOneSettings(mathType: MathType, value: Boolean) =
        viewModelScope.launch { repository.selectAllOutputOneSettings(mathType, value) }

    fun selectAllOutputTwoSettings(mathType: MathType, value: Boolean) =
        viewModelScope.launch { repository.selectAllOutputTwoSettings(mathType, value) }
}

class MathQuizSettingsViewModelFactory(
    private val repository: MathQuizSettingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MathQuizSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MathQuizSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}