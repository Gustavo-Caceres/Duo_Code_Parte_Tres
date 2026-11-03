package com.example.duocode.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LinguagemDao {
    @Insert
    suspend fun inserir(linguagem: Linguagem)

    @Query("SELECT * FROM linguagens")
    fun buscarTodos(): Flow<List<Linguagem>>

    @Query("SELECT * FROM linguagens WHERE id = :id")
    suspend fun buscarPorId(id: Int): Linguagem?

    @Update
    suspend fun atualizar(linguagem: Linguagem)

    @Delete
    suspend fun deletar(linguagem: Linguagem)
}
