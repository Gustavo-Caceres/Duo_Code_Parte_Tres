package com.example.duocode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.duocode.data.repository.LinguagemRepository
import com.example.duocode.data.repository.QuestaoRepository

class PraticaViewModelFactory(
    private val linguagemRepository: LinguagemRepository,
    private val questaoRepository: QuestaoRepository,
    private val linguagemId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PraticaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PraticaViewModel(linguagemRepository, questaoRepository, linguagemId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
