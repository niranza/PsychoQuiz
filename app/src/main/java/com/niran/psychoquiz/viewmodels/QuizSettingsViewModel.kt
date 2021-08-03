package com.niran.psychoquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.niran.psychoquiz.repositories.QuizSettingRepository
import kotlinx.coroutines.launch

class QuizSettingsViewModel(private val settingRepository: QuizSettingRepository) : ViewModel() {

    fun getAllWordFirstLettersSettings() =
        settingRepository.getAllWordFirstLetterSettings().asLiveData()

    fun insertFirstLetterSetting(id: Int, value: Boolean) =
        viewModelScope.launch { settingRepository.insertWordFirstLetterSetting(id, value) }

    fun getAllWordTypeSettings() = settingRepository.getAllWordTypeSettings().asLiveData()

    fun insertWordTypeSetting(id: Int, value: Boolean) =
        viewModelScope.launch { settingRepository.insertWordTypeSetting(id, value) }

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