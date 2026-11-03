package com.example.duocode.data.repository

import android.content.Context
import com.example.duocode.data.AppDatabase
import com.example.duocode.data.Linguagem
import com.example.duocode.data.LinguagemDao
import kotlinx.coroutines.flow.Flow

class LinguagemRepository(private val linguagemDao: LinguagemDao) {

    fun getAllLinguagens(): Flow<List<Linguagem>> = linguagemDao.buscarTodos()

    suspend fun buscarPorId(id: Int): Linguagem? = linguagemDao.buscarPorId(id)

    suspend fun inserir(linguagem: Linguagem) {
        linguagemDao.inserir(linguagem)
    }

    suspend fun atualizar(linguagem: Linguagem) {
        linguagemDao.atualizar(linguagem)
    }

    suspend fun deletar(linguagem: Linguagem) {
        linguagemDao.deletar(linguagem)
    }

    companion object {
        fun criar(context: Context): LinguagemRepository =
            LinguagemRepository(AppDatabase.getDatabase(context).linguagemDao())
    }
}
