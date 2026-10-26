package com.example.duocode.data

import androidx.room.TypeConverter

class Conversores {
    @TypeConverter
    fun deLista(lista: List<String>): String = lista.joinToString("\n")

    @TypeConverter
    fun paraLista(texto: String): List<String> = texto.split("\n").filter { it.isNotBlank() }
}
