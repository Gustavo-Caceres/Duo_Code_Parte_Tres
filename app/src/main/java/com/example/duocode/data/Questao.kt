package com.example.duocode.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val alternativasErradas: List<String> = emptyList()
)
