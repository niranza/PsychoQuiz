package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import com.niran.psychoquiz.repositories.QuizSettingRepository
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class QuizSettingsViewModel(private val repository: QuizSettingRepository) : ViewModel() {

    fun insertBooleanSetting(booleanSetting: BooleanSetting) = viewModelScope.launch {
        repository.insertBooleanSetting(booleanSetting)
    }

    fun getAllWordFirstLettersSettingsAsLiveData() =
        repository.getAllWordFirstLetterSettingsWithFlow().asLiveData()

    fun getAllWordTypeSettingsAsLiveData() =
        repository.getAllWordTypeSettingsWithFlow().asLiveData()

    fun <T : BooleanSetting> selectAllSettings(clazz: KClass<T>, value: Boolean) =
        viewModelScope.launch { repository.selectAllSettings(clazz, value) }
}

class QuizSettingsViewModelFactory(
    private val repository: QuizSettingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}