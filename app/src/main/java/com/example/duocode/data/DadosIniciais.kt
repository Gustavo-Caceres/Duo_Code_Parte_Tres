package com.example.duocode.data

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.duocode.model.Dificuldade
import com.example.duocode.model.Linguagem
import com.example.duocode.model.Questao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun inserirDadosIniciais(linguagemDao: LinguagemDao, questaoDao: QuestaoDao) {
    if (linguagemDao.buscarTodos().isNotEmpty()) return

    listOf(
        Linguagem(nome = "JavaScript", sigla = "JS", descricao = "Web, front-end e Node"),
        Linguagem(nome = "Python", sigla = "PY", descricao = "Scripts, dados e automação"),
        Linguagem(nome = "Kotlin", sigla = "KT", descricao = "Android nativo"),
        Linguagem(nome = "SQL", sigla = "SQL", descricao = "Consultas e bancos relacionais")
    ).forEach { linguagemDao.inserir(it) }

    val ids = linguagemDao.buscarTodos().associate { it.sigla to it.id }

    listOf(
        Questao(enunciado = "Qual é a saída de arr.length após arr.push(4)?", resposta = "4", linguagemId = ids.getValue("JS"), dificuldade = Dificuldade.FACIL, resolvida = true),
        Questao(enunciado = "Qual a diferença entre let e const?", resposta = "const não pode ser reatribuída", linguagemId = ids.getValue("JS"), dificuldade = Dificuldade.FACIL, resolvida = true),
        Questao(enunciado = "O que [1, 2, 3].map(x => x * 2) retorna?", resposta = "[2, 4, 6]", linguagemId = ids.getValue("JS"), dificuldade = Dificuldade.MEDIO),
        Questao(enunciado = "Qual a ordem de execução do event loop?", resposta = "call stack, microtasks, macrotasks", linguagemId = ids.getValue("JS"), dificuldade = Dificuldade.DIFICIL),
        Questao(enunciado = "O que len(\"dev\") retorna?", resposta = "3", linguagemId = ids.getValue("PY"), dificuldade = Dificuldade.FACIL),
        Questao(enunciado = "Qual palavra-chave declara um valor imutável em Kotlin?", resposta = "val", linguagemId = ids.getValue("KT"), dificuldade = Dificuldade.MEDIO),
        Questao(enunciado = "Qual cláusula filtra grupos depois de um GROUP BY?", resposta = "HAVING", linguagemId = ids.getValue("SQL"), dificuldade = Dificuldade.DIFICIL)
    ).forEach { questaoDao.inserir(it) }
}

suspend fun recarregarListas(
    linguagemDao: LinguagemDao,
    questaoDao: QuestaoDao,
    linguagens: SnapshotStateList<Linguagem>,
    questoes: SnapshotStateList<Questao>
) {
    val todasLinguagens = linguagemDao.buscarTodos()
    val todasQuestoes = questaoDao.buscarTodos()
    withContext(Dispatchers.Main) {
        linguagens.clear()
        linguagens.addAll(todasLinguagens)
        questoes.clear()
        questoes.addAll(todasQuestoes)
    }
}
