package com.example.duocode.viewmodel

import com.example.duocode.data.Dificuldade
import com.example.duocode.data.Linguagem
import com.example.duocode.data.Questao

const val QUANTIDADE_ALTERNATIVAS_ERRADAS = 3

data class FormularioQuestao(
    val enunciado: String = "",
    val resposta: String = "",
    val alternativasErradas: List<String> = List(QUANTIDADE_ALTERNATIVAS_ERRADAS) { "" },
    val linguagemId: Int? = null,
    val dificuldade: Dificuldade = Dificuldade.FACIL
) {
    val comecouAPreencher: Boolean
        get() = enunciado.isNotBlank() || resposta.isNotBlank() || alternativasErradas.any { it.isNotBlank() }

    val alternativasPreenchidas: List<String>
        get() = alternativasErradas.map { it.replace("\n", " ").trim() }.filter { it.isNotBlank() }

    fun comAlternativa(indice: Int, texto: String): FormularioQuestao =
        copy(alternativasErradas = alternativasErradas.mapIndexed { i, atual -> if (i == indice) texto else atual })
}

data class QuestoesUiState(
    val linguagens: List<Linguagem> = emptyList(),
    val questoes: List<Questao> = emptyList(),
    val carregando: Boolean = true,
    val novaQuestao: FormularioQuestao = FormularioQuestao(),
    val criandoQuestao: Boolean = false,
    val questaoEmEdicao: Questao? = null,
    val edicao: FormularioQuestao = FormularioQuestao()
) {
    val linguagemDaNovaQuestao: Linguagem?
        get() = linguagens.find { it.id == novaQuestao.linguagemId } ?: linguagens.firstOrNull()

    val erroNovaQuestao: String?
        get() = if (linguagemDaNovaQuestao == null) {
            "Cadastre uma linguagem antes de criar questões"
        } else {
            validarQuestao(novaQuestao, questoes)
        }

    val linguagemDaEdicao: Linguagem?
        get() = linguagens.find { it.id == edicao.linguagemId } ?: linguagens.firstOrNull()

    val erroEdicao: String?
        get() = validarQuestao(edicao, questoes, questaoEmEdicao?.id ?: 0)
}

private fun validarQuestao(formulario: FormularioQuestao, questoes: List<Questao>, idAtual: Int = 0): String? {
    val enunciado = formulario.enunciado.trim()
    val resposta = formulario.resposta.trim()
    val erradas = formulario.alternativasErradas.map { it.trim() }
    return when {
        enunciado.isBlank() -> "Escreva o enunciado da questão"
        enunciado.length < 5 -> "O enunciado precisa ter pelo menos 5 caracteres"
        questoes.any { it.id != idAtual && it.enunciado.trim().equals(enunciado, ignoreCase = true) } ->
            "Já existe uma questão com esse enunciado"
        resposta.isBlank() -> "Informe a resposta certa"
        erradas.any { it.isBlank() } -> "Preencha as $QUANTIDADE_ALTERNATIVAS_ERRADAS alternativas erradas"
        erradas.any { it.equals(resposta, ignoreCase = true) } -> "Uma alternativa errada está igual à resposta certa"
        erradas.map { it.lowercase() }.distinct().size < erradas.size -> "As alternativas erradas precisam ser diferentes entre si"
        else -> null
    }
}
