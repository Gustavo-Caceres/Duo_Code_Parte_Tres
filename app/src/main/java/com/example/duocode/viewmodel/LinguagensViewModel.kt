package com.example.duocode.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duocode.data.Linguagem
import com.example.duocode.data.repository.LinguagemRepository
import com.example.duocode.data.repository.QuestaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LinguagensViewModel(
    private val linguagemRepository: LinguagemRepository,
    private val questaoRepository: QuestaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LinguagensUiState())
    val uiState: StateFlow<LinguagensUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                linguagemRepository.getAllLinguagens(),
                questaoRepository.getAllQuestoes()
            ) { linguagens, questoes -> linguagens to questoes }
                .catch { e -> Log.e("LinguagensViewModel", "Erro ao buscar as linguagens", e) }
                .collect { (linguagens, questoes) ->
                    _uiState.update { it.copy(linguagens = linguagens, questoes = questoes) }
                }
        }
    }

    fun alterarNovoNome(nome: String) {
        _uiState.update { it.copy(novoNome = nome) }
    }

    fun adicionarLinguagem() {
        val state = _uiState.value
        if (state.erroNovoNome != null) return

        val nome = state.novoNome.trim()
        val linguagem = Linguagem(nome = nome, sigla = nome.take(2).uppercase())

        viewModelScope.launch {
            try {
                linguagemRepository.inserir(linguagem)
                _uiState.update { it.copy(novoNome = "") }
            } catch (e: Exception) {
                Log.e("LinguagensViewModel", "Erro ao inserir a linguagem", e)
            }
        }
    }

    fun editarLinguagem(linguagem: Linguagem?) {
        _uiState.update {
            it.copy(
                linguagemEmEdicao = linguagem,
                nomeEdicao = linguagem?.nome ?: "",
                siglaEdicao = linguagem?.sigla ?: "",
                descricaoEdicao = linguagem?.descricao ?: ""
            )
        }
    }

    fun alterarNomeEdicao(nome: String) {
        _uiState.update { it.copy(nomeEdicao = nome) }
    }

    fun alterarSiglaEdicao(sigla: String) {
        _uiState.update { it.copy(siglaEdicao = sigla.filter { c -> c.isLetterOrDigit() }.uppercase().take(3)) }
    }

    fun alterarDescricaoEdicao(descricao: String) {
        _uiState.update { it.copy(descricaoEdicao = descricao) }
    }

    fun atualizarLinguagem() {
        val state = _uiState.value
        val linguagem = state.linguagemEmEdicao ?: return
        if (state.erroEdicao != null) return

        val atualizada = linguagem.copy(
            nome = state.nomeEdicao.trim(),
            sigla = state.siglaEdicao.trim(),
            descricao = state.descricaoEdicao.trim()
        )

        viewModelScope.launch {
            try {
                linguagemRepository.atualizar(atualizada)
                editarLinguagem(null)
            } catch (e: Exception) {
                Log.e("LinguagensViewModel", "Erro ao atualizar a linguagem", e)
            }
        }
    }

    fun pedirRemocao(linguagem: Linguagem?) {
        _uiState.update { it.copy(linguagemParaRemover = linguagem) }
    }

    fun removerLinguagem(linguagem: Linguagem) {
        viewModelScope.launch {
            try {
                linguagemRepository.deletar(linguagem)
            } catch (e: Exception) {
                Log.e("LinguagensViewModel", "Erro ao remover a linguagem", e)
            }
        }
        pedirRemocao(null)
    }
}
