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

    fun questao(sigla: String, dificuldade: Dificuldade, enunciado: String, resposta: String, vararg erradas: String) =
        Questao(
            enunciado = enunciado,
            resposta = resposta,
            linguagemId = ids.getValue(sigla),
            dificuldade = dificuldade,
            alternativasErradas = erradas.toList()
        )

    listOf(
        questao("JS", Dificuldade.FACIL, "Qual é a saída de arr.length após arr.push(4), se arr = [1, 2, 3]?", "4", "3", "undefined", "Error"),
        questao("JS", Dificuldade.FACIL, "Qual a diferença entre let e const?", "const não pode ser reatribuída", "let não pode ser reatribuída", "const só funciona em funções", "Não há diferença"),
        questao("JS", Dificuldade.FACIL, "O que typeof null retorna?", "\"object\"", "\"null\"", "\"undefined\"", "\"number\""),
        questao("JS", Dificuldade.FACIL, "Qual método transforma um texto JSON em objeto?", "JSON.parse", "JSON.stringify", "JSON.toObject", "Object.parse"),
        questao("JS", Dificuldade.MEDIO, "O que [1, 2, 3].map(x => x * 2) retorna?", "[2, 4, 6]", "[1, 2, 3]", "[1, 4, 9]", "undefined"),
        questao("JS", Dificuldade.MEDIO, "Qual o resultado de 0.1 + 0.2 === 0.3?", "false", "true", "undefined", "NaN"),
        questao("JS", Dificuldade.MEDIO, "O que [1, 2, 3, 4].filter(x => x % 2 === 0) retorna?", "[2, 4]", "[1, 3]", "[1, 2, 3, 4]", "2"),
        questao("JS", Dificuldade.MEDIO, "Qual operador compara valor e tipo ao mesmo tempo?", "===", "==", "=", "!="),
        questao("JS", Dificuldade.DIFICIL, "Qual a ordem de execução do event loop?", "call stack, microtasks, macrotasks", "macrotasks, microtasks, call stack", "microtasks, call stack, macrotasks", "call stack, macrotasks, microtasks"),
        questao("JS", Dificuldade.DIFICIL, "O que \"5\" - 2 retorna?", "3", "\"52\"", "NaN", "\"3\""),

        questao("PY", Dificuldade.FACIL, "O que len(\"dev\") retorna?", "3", "2", "4", "Error"),
        questao("PY", Dificuldade.FACIL, "Qual função mostra um texto na tela?", "print()", "echo()", "console.log()", "write()"),
        questao("PY", Dificuldade.FACIL, "Qual o resultado de 7 // 2?", "3", "3.5", "4", "1"),
        questao("PY", Dificuldade.FACIL, "Qual tipo representa uma sequência imutável?", "tuple", "list", "dict", "set"),
        questao("PY", Dificuldade.MEDIO, "O que [x * 2 for x in range(3)] retorna?", "[0, 2, 4]", "[2, 4, 6]", "[0, 1, 2]", "[1, 2, 3]"),
        questao("PY", Dificuldade.MEDIO, "Qual palavra-chave define uma função?", "def", "func", "function", "fun"),
        questao("PY", Dificuldade.MEDIO, "O que \"abc\"[::-1] retorna?", "\"cba\"", "\"abc\"", "\"c\"", "Error"),
        questao("PY", Dificuldade.MEDIO, "Qual o resultado de 2 ** 3?", "8", "6", "9", "5"),
        questao("PY", Dificuldade.DIFICIL, "O que um decorator recebe como argumento?", "Uma função", "Um módulo", "Uma lista de argumentos", "Uma exceção"),
        questao("PY", Dificuldade.DIFICIL, "Como se chama a função que produz valores sob demanda com yield?", "Gerador", "Lista", "Dicionário", "Tupla"),

        questao("KT", Dificuldade.FACIL, "Qual palavra-chave declara uma variável que pode mudar?", "var", "val", "let", "mut"),
        questao("KT", Dificuldade.FACIL, "Qual palavra-chave declara uma função?", "fun", "def", "function", "func"),
        questao("KT", Dificuldade.FACIL, "Qual o tipo de \"texto\"?", "String", "Char", "Text", "Str"),
        questao("KT", Dificuldade.MEDIO, "Qual palavra-chave declara um valor imutável em Kotlin?", "val", "var", "const", "let"),
        questao("KT", Dificuldade.MEDIO, "O que o operador ?: (Elvis) faz?", "Devolve um valor padrão quando é null", "Compara dois valores", "Converte o tipo", "Lança uma exceção"),
        questao("KT", Dificuldade.MEDIO, "Qual declaração aceita o valor null?", "String?", "String", "String!", "Nullable<String>"),
        questao("KT", Dificuldade.MEDIO, "O que listOf(1, 2, 3).sum() retorna?", "6", "3", "123", "[1, 2, 3]"),
        questao("KT", Dificuldade.MEDIO, "Que tipo de classe gera equals, hashCode e toString automaticamente?", "data class", "enum class", "object", "abstract class"),
        questao("KT", Dificuldade.DIFICIL, "Qual escopo de coroutine acompanha o ciclo de vida de uma ViewModel?", "viewModelScope", "GlobalScope", "lifecycleScope", "MainScope"),
        questao("KT", Dificuldade.DIFICIL, "Qual palavra-chave deixa uma função pausar sem bloquear a thread?", "suspend", "async", "await", "pause"),

        questao("SQL", Dificuldade.FACIL, "Qual comando busca dados de uma tabela?", "SELECT", "GET", "FETCH", "SHOW"),
        questao("SQL", Dificuldade.FACIL, "Qual comando adiciona uma linha na tabela?", "INSERT", "ADD", "UPDATE", "CREATE"),
        questao("SQL", Dificuldade.FACIL, "Qual cláusula filtra as linhas de uma consulta?", "WHERE", "HAVING", "ORDER BY", "GROUP BY"),
        questao("SQL", Dificuldade.FACIL, "Qual comando altera os dados de linhas que já existem?", "UPDATE", "ALTER", "INSERT", "MODIFY"),
        questao("SQL", Dificuldade.MEDIO, "Qual cláusula ordena o resultado?", "ORDER BY", "SORT BY", "GROUP BY", "ARRANGE"),
        questao("SQL", Dificuldade.MEDIO, "Qual função conta o número de linhas?", "COUNT()", "SUM()", "TOTAL()", "LENGTH()"),
        questao("SQL", Dificuldade.MEDIO, "Qual JOIN traz só as linhas que existem nas duas tabelas?", "INNER JOIN", "LEFT JOIN", "FULL JOIN", "CROSS JOIN"),
        questao("SQL", Dificuldade.MEDIO, "Como verificar se uma coluna é nula?", "IS NULL", "= NULL", "== NULL", "IS EMPTY"),
        questao("SQL", Dificuldade.DIFICIL, "Qual cláusula filtra grupos depois de um GROUP BY?", "HAVING", "WHERE", "ORDER BY", "LIMIT"),
        questao("SQL", Dificuldade.DIFICIL, "O que uma chave estrangeira garante?", "Que o valor exista na tabela referenciada", "Que o valor seja único", "Que a coluna não seja nula", "Que a tabela tenha um índice")
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
