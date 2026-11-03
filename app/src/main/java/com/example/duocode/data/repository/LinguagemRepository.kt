package com.example.duocode.data.repository

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
}
