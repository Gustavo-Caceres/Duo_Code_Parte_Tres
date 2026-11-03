package com.example.duocode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.duocode.data.repository.LinguagemRepository
import com.example.duocode.data.repository.QuestaoRepository

class QuestoesViewModelFactory(
    private val linguagemRepository: LinguagemRepository,
    private val questaoRepository: QuestaoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestoesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuestoesViewModel(linguagemRepository, questaoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
