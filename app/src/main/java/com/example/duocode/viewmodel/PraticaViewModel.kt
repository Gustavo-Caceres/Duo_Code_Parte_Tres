package com.example.duocode.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duocode.data.Questao
import com.example.duocode.data.repository.LinguagemRepository
import com.example.duocode.data.repository.QuestaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PraticaViewModel(
    private val linguagemRepository: LinguagemRepository,
    private val questaoRepository: QuestaoRepository,
    linguagemId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(PraticaUiState(linguagemId = linguagemId))
    val uiState: StateFlow<PraticaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                linguagemRepository.getAllLinguagens(),
                questaoRepository.getAllQuestoes()
            ) { linguagens, questoes -> linguagens to questoes }
                .catch { e -> Log.e("PraticaViewModel", "Erro ao buscar as questões", e) }
                .collect { (linguagens, questoes) ->
                    _uiState.update { it.copy(linguagens = linguagens, questoes = questoes) }
                }
        }
    }

    fun alternarSelecao(questao: Questao) {
        _uiState.update {
            val desmarcadas = if (questao.id in it.desmarcadas) it.desmarcadas - questao.id else it.desmarcadas + questao.id
            it.copy(desmarcadas = desmarcadas)
        }
    }

    fun comecar() {
        val state = _uiState.value
        if (state.selecionadas.isEmpty()) return

        _uiState.update {
            it.copy(
                perguntas = montarPerguntas(state.selecionadas, state.questoes),
                indiceAtual = 0,
                selecionada = -1,
                respondida = false,
                acertos = 0
            )
        }
    }

    fun escolherAlternativa(indice: Int) {
        if (_uiState.value.respondida) return
        _uiState.update { it.copy(selecionada = indice) }
    }

    fun confirmar() {
        val state = _uiState.value
        val pergunta = state.perguntaAtual ?: return
        if (state.selecionada == -1 || state.respondida) return

        _uiState.update { it.copy(respondida = true, acertos = if (state.acertou) it.acertos + 1 else it.acertos) }

        if (state.acertou) {
            viewModelScope.launch {
                try {
                    questaoRepository.atualizar(pergunta.questao.copy(resolvida = true))
                } catch (e: Exception) {
                    Log.e("PraticaViewModel", "Erro ao marcar a questão como resolvida", e)
                }
            }
        }
    }

    fun proxima() {
        _uiState.update {
            it.copy(indiceAtual = it.indiceAtual + 1, selecionada = -1, respondida = false)
        }
    }
}

private fun montarPerguntas(selecionadas: List<Questao>, todas: List<Questao>): List<Pergunta> =
    selecionadas.shuffled().map { questao ->
        val correta = questao.resposta.trim()
        val mesmaLinguagem = todas.filter { it.linguagemId == questao.linguagemId }.shuffled()
        val outras = todas.filter { it.linguagemId != questao.linguagemId }.shuffled()
        val reservas = (mesmaLinguagem + outras).map { it.resposta }
        val erradas = (questao.alternativasErradas + reservas)
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.equals(correta, ignoreCase = true) }
            .distinctBy { it.lowercase() }
            .take(QUANTIDADE_ALTERNATIVAS_ERRADAS)
        val alternativas = (erradas + correta).shuffled()
        Pergunta(questao, alternativas, alternativas.indexOf(correta))
    }
