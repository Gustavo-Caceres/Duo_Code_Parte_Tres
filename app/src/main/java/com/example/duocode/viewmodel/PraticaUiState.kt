package com.example.duocode.viewmodel

import com.example.duocode.data.Linguagem
import com.example.duocode.data.Questao

data class Pergunta(
    val questao: Questao,
    val alternativas: List<String>,
    val corretaIndex: Int
)

data class PraticaUiState(
    val linguagemId: Int = -1,
    val linguagens: List<Linguagem> = emptyList(),
    val questoes: List<Questao> = emptyList(),
    val desmarcadas: Set<Int> = emptySet(),
    val perguntas: List<Pergunta>? = null,
    val indiceAtual: Int = 0,
    val selecionada: Int = -1,
    val respondida: Boolean = false,
    val acertos: Int = 0
) {
    val titulo: String
        get() = linguagens.find { it.id == linguagemId }?.let { "Praticar ${it.nome}" } ?: "Praticar"

    val questoesDaPratica: List<Questao>
        get() = questoes.filter { linguagemId == -1 || it.linguagemId == linguagemId }

    val praticaveis: List<Questao>
        get() = questoesDaPratica.filter { it.resposta.isNotBlank() }

    val selecionadas: List<Questao>
        get() = praticaveis.filter { it.id !in desmarcadas }

    val perguntaAtual: Pergunta?
        get() = perguntas?.getOrNull(indiceAtual)

    val total: Int
        get() = perguntas?.size ?: 0

    val ultimaPergunta: Boolean
        get() = indiceAtual == total - 1

    val acertou: Boolean
        get() = perguntaAtual?.corretaIndex == selecionada

    val linguagensPraticadas: String
        get() {
            val nomes = perguntas.orEmpty()
                .mapNotNull { pergunta -> linguagens.find { it.id == pergunta.questao.linguagemId }?.nome }
                .distinct()
            return when (nomes.size) {
                0 -> "programação"
                1 -> nomes.first()
                else -> nomes.dropLast(1).joinToString(", ") + " e " + nomes.last()
            }
        }
}
