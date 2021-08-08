package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.niran.psychoquiz.database.models.settings.superclasses.BooleanSetting
import com.niran.psychoquiz.repositories.QuizSettingRepository
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class QuizSettingsViewModel(private val settingRepository: QuizSettingRepository) : ViewModel() {

    fun insertBooleanSetting(booleanSetting: BooleanSetting) = viewModelScope.launch {
        settingRepository.insertBooleanSetting(booleanSetting)
    }

    fun getAllWordFirstLettersSettingsAsLiveData() =
        settingRepository.getAllWordFirstLetterSettingsWithFlow().asLiveData()

    fun getAllWordTypeSettingsAsLiveData() =
        settingRepository.getAllWordTypeSettingsWithFlow().asLiveData()

    fun <T : BooleanSetting> selectAllSettings(clazz: KClass<T>, value: Boolean) =
        viewModelScope.launch { settingRepository.selectAllSettings(clazz, value) }
}

class QuizSettingsViewModelFactory(
    private val quizSettingRepository: QuizSettingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizSettingsViewModel(quizSettingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}