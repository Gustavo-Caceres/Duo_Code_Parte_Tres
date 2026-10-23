package com.example.duocode.model

data class Linguagem(val id: Int, val nome: String, val sigla: String, val descricao: String = "")

enum class Dificuldade(val rotulo: String) {
    FACIL("fácil"),
    MEDIO("médio"),
    DIFICIL("difícil")
}

data class Questao(
    val id: Int,
    val enunciado: String,
    val resposta: String,
    val linguagemId: Int,
    val dificuldade: Dificuldade = Dificuldade.FACIL,
    val resolvida: Boolean = false
)
