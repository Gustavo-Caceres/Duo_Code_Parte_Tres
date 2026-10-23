package com.example.duocode.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "linguagens")
data class Linguagem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val sigla: String,
    val descricao: String = ""
)

enum class Dificuldade(val rotulo: String) {
    FACIL("fácil"),
    MEDIO("médio"),
    DIFICIL("difícil")
}

@Entity(
    tableName = "questoes",
    foreignKeys = [
        ForeignKey(
            entity = Linguagem::class,
            parentColumns = ["id"],
            childColumns = ["linguagemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("linguagemId")]
)
data class Questao(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val enunciado: String,
    val resposta: String,
    val linguagemId: Int,
    val dificuldade: Dificuldade = Dificuldade.FACIL,
    val resolvida: Boolean = false,
    // Alternativas que aparecem junto com a resposta certa na hora de praticar
    val alternativasErradas: List<String> = emptyList()
)
