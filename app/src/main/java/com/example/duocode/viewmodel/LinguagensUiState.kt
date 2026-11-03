package com.example.duocode.viewmodel

import com.example.duocode.data.Linguagem
import com.example.duocode.data.Questao

data class LinguagensUiState(
    val linguagens: List<Linguagem> = emptyList(),
    val questoes: List<Questao> = emptyList(),
    val novoNome: String = "",
    val linguagemEmEdicao: Linguagem? = null,
    val nomeEdicao: String = "",
    val siglaEdicao: String = "",
    val descricaoEdicao: String = "",
    val linguagemParaRemover: Linguagem? = null
) {
    val erroNovoNome: String?
        get() = validarNomeLinguagem(novoNome, linguagens)

    val erroEdicao: String?
        get() = validarNomeLinguagem(nomeEdicao, linguagens, linguagemEmEdicao?.id ?: 0)
            ?: if (siglaEdicao.isBlank()) "Informe a sigla (até 3 letras)" else null

    val questoesDaLinguagemParaRemover: Int
        get() = questoes.count { it.linguagemId == linguagemParaRemover?.id }
}

private fun validarNomeLinguagem(nome: String, linguagens: List<Linguagem>, idAtual: Int = 0): String? = when {
    nome.isBlank() -> "Digite o nome da linguagem"
    nome.trim().length < 2 -> "O nome precisa ter pelo menos 2 letras"
    nome.trim().length > 20 -> "O nome pode ter no máximo 20 letras"
    linguagens.any { it.id != idAtual && it.nome.equals(nome.trim(), ignoreCase = true) } ->
        "Já existe uma linguagem com esse nome"
    else -> null
}
