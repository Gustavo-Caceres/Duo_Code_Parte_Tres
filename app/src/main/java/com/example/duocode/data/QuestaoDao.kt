package com.example.duocode.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.duocode.model.Questao

@Dao
interface QuestaoDao {
    @Insert
    suspend fun inserir(questao: Questao)

    @Query("SELECT * FROM questoes")
    suspend fun buscarTodos(): List<Questao>

    @Query("SELECT * FROM questoes WHERE id = :id")
    suspend fun buscarPorId(id: Int): Questao?

    @Query("SELECT * FROM questoes WHERE linguagemId = :linguagemId")
    suspend fun buscarPorLinguagem(linguagemId: Int): List<Questao>

    @Update
    suspend fun atualizar(questao: Questao)

    @Delete
    suspend fun deletar(questao: Questao)
}
