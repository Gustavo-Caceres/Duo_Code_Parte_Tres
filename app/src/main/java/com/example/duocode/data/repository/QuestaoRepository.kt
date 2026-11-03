package com.example.duocode.data.repository

import com.example.duocode.data.Questao
import com.example.duocode.data.QuestaoDao
import kotlinx.coroutines.flow.Flow

class QuestaoRepository(private val questaoDao: QuestaoDao) {

    fun getAllQuestoes(): Flow<List<Questao>> = questaoDao.buscarTodos()

    suspend fun buscarPorId(id: Int): Questao? = questaoDao.buscarPorId(id)

    suspend fun buscarPorLinguagem(linguagemId: Int): List<Questao> = questaoDao.buscarPorLinguagem(linguagemId)

    suspend fun inserir(questao: Questao) {
        questaoDao.inserir(questao)
    }

    suspend fun atualizar(questao: Questao) {
        questaoDao.atualizar(questao)
    }

    suspend fun deletar(questao: Questao) {
        questaoDao.deletar(questao)
    }
}
