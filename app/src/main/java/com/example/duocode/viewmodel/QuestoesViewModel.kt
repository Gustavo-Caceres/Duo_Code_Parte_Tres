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

class QuestoesViewModel(
    private val linguagemRepository: LinguagemRepository,
    private val questaoRepository: QuestaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestoesUiState())
    val uiState: StateFlow<QuestoesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                linguagemRepository.getAllLinguagens(),
                questaoRepository.getAllQuestoes()
            ) { linguagens, questoes -> linguagens to questoes }
                .catch { e ->
                    Log.e("QuestoesViewModel", "Erro ao buscar as questões", e)
                    _uiState.update { it.copy(carregando = false) }
                }
                .collect { (linguagens, questoes) ->
                    _uiState.update { it.copy(linguagens = linguagens, questoes = questoes, carregando = false) }
                }
        }
    }

    fun alterarNovaQuestao(formulario: FormularioQuestao) {
        _uiState.update { it.copy(novaQuestao = formulario) }
    }

    fun abrirNovaQuestao(linguagemId: Int) {
        _uiState.update { it.copy(novaQuestao = FormularioQuestao(linguagemId = linguagemId), criandoQuestao = true) }
    }

    fun fecharNovaQuestao() {
        _uiState.update { it.copy(criandoQuestao = false) }
    }

    fun adicionarQuestao() {
        val state = _uiState.value
        val linguagem = state.linguagemDaNovaQuestao ?: return
        if (state.erroNovaQuestao != null) return

        val formulario = state.novaQuestao
        val questao = Questao(
            enunciado = formulario.enunciado.trim(),
            resposta = formulario.resposta.trim(),
            linguagemId = linguagem.id,
            dificuldade = formulario.dificuldade,
            alternativasErradas = formulario.alternativasPreenchidas
        )

        viewModelScope.launch {
            try {
                questaoRepository.inserir(questao)
                _uiState.update {
                    it.copy(
                        novaQuestao = FormularioQuestao(linguagemId = linguagem.id, dificuldade = formulario.dificuldade),
                        criandoQuestao = false
                    )
                }
            } catch (e: Exception) {
                Log.e("QuestoesViewModel", "Erro ao inserir a questão", e)
            }
        }
    }

    fun editarQuestao(questao: Questao?) {
        _uiState.update {
            it.copy(
                questaoEmEdicao = questao,
                edicao = if (questao == null) {
                    FormularioQuestao()
                } else {
                    FormularioQuestao(
                        enunciado = questao.enunciado,
                        resposta = questao.resposta,
                        alternativasErradas = List(QUANTIDADE_ALTERNATIVAS_ERRADAS) { i ->
                            questao.alternativasErradas.getOrElse(i) { "" }
                        },
                        linguagemId = questao.linguagemId,
                        dificuldade = questao.dificuldade
                    )
                }
            )
        }
    }

    fun alterarEdicao(formulario: FormularioQuestao) {
        _uiState.update { it.copy(edicao = formulario) }
    }

    fun atualizarQuestao() {
        val state = _uiState.value
        val questao = state.questaoEmEdicao ?: return
        val linguagem = state.linguagemDaEdicao ?: return
        if (state.erroEdicao != null) return

        val formulario = state.edicao
        val atualizada = questao.copy(
            enunciado = formulario.enunciado.trim(),
            resposta = formulario.resposta.trim(),
            linguagemId = linguagem.id,
            dificuldade = formulario.dificuldade,
            alternativasErradas = formulario.alternativasPreenchidas
        )

        viewModelScope.launch {
            try {
                questaoRepository.atualizar(atualizada)
                editarQuestao(null)
            } catch (e: Exception) {
                Log.e("QuestoesViewModel", "Erro ao atualizar a questão", e)
            }
        }
    }

    fun removerQuestao(questao: Questao) {
        viewModelScope.launch {
            try {
                questaoRepository.deletar(questao)
            } catch (e: Exception) {
                Log.e("QuestoesViewModel", "Erro ao remover a questão", e)
            }
        }
    }

    fun alternarResolvida(questao: Questao) {
        viewModelScope.launch {
            try {
                questaoRepository.atualizar(questao.copy(resolvida = !questao.resolvida))
            } catch (e: Exception) {
                Log.e("QuestoesViewModel", "Erro ao marcar a questão", e)
            }
        }
    }

    fun marcarComoResolvida(questao: Questao) {
        viewModelScope.launch {
            try {
                questaoRepository.atualizar(questao.copy(resolvida = true))
            } catch (e: Exception) {
                Log.e("QuestoesViewModel", "Erro ao marcar a questão como resolvida", e)
            }
        }
    }
}
