package com.example.duocode.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "linguagens")
data class Linguagem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val sigla: String,
    val descricao: String = ""
)
