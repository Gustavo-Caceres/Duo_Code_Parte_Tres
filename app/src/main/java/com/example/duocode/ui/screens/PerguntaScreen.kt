package com.example.duocode.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.duocode.data.LinguagemDao
import com.example.duocode.data.QuestaoDao
import com.example.duocode.data.recarregarListas
import com.example.duocode.model.Linguagem
import com.example.duocode.model.Questao
import com.example.duocode.navigation.Rotas
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoCardBorder
import com.example.duocode.ui.theme.DuoGreen
import com.example.duocode.ui.theme.DuoOrange
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private data class Pergunta(
    val questao: Questao,
    val alternativas: List<String>,
    val corretaIndex: Int
)

// Cada pergunta sempre tem 4 alternativas em ordem aleatória: a resposta certa e as 3 erradas cadastradas.
// Se faltar alguma errada (questão antiga), completa com respostas de outras questões, preferindo a mesma linguagem
private fun montarPerguntas(selecionadas: List<Questao>, todas: List<Questao>): List<Pergunta> =
    selecionadas.shuffled().map { questao ->
        val correta = questao.resposta.trim()
        val mesmaLinguagem = todas.filter { it.linguagemId == questao.linguagemId }.shuffled()
        val outras = todas.filter { it.linguagemId != questao.linguagemId }.shuffled()
        val reservas = (mesmaLinguagem + outras).map { it.resposta }
        val erradas = (questao.alternativasErradas + reservas)
            .map { it.trim() }
            .filter { it.isNotBlank() && !it.equals(correta, ignoreCase = true) }
            .distinctBy { it.lowercase() }
            .take(QUANTIDADE_ALTERNATIVAS_ERRADAS)
        val alternativas = (erradas + correta).shuffled()
        Pergunta(questao, alternativas, alternativas.indexOf(correta))
    }

// Junta os nomes das linguagens praticadas para o resultado: "Kotlin" ou "Python, Kotlin e SQL"
private fun nomesDasLinguagens(perguntas: List<Pergunta>, linguagens: List<Linguagem>): String {
    val nomes = perguntas
        .mapNotNull { pergunta -> linguagens.find { it.id == pergunta.questao.linguagemId }?.nome }
        .distinct()
    return when (nomes.size) {
        0 -> "programação"
        1 -> nomes.first()
        else -> nomes.dropLast(1).joinToString(", ") + " e " + nomes.last()
    }
}

// linguagemId = -1 pratica questões de todas as linguagens
@Composable
fun PerguntaScreen(
    navController: NavController,
    linguagemId: Int,
    linguagens: SnapshotStateList<Linguagem>,
    questoes: SnapshotStateList<Questao>,
    linguagemDao: LinguagemDao,
    questaoDao: QuestaoDao
) {
    var perguntas by remember { mutableStateOf<List<Pergunta>?>(null) }
    val perguntasAtuais = perguntas

    if (perguntasAtuais == null) {
        val linguagem = linguagens.find { it.id == linguagemId }
        SelecaoQuestoes(
            titulo = if (linguagem != null) "Praticar ${linguagem.nome}" else "Praticar",
            questoes = questoes.filter { linguagemId == -1 || it.linguagemId == linguagemId },
            linguagens = linguagens,
            onFechar = { navController.popBackStack() },
            onComecar = { selecionadas -> perguntas = montarPerguntas(selecionadas, questoes) }
        )
    } else {
        Pratica(
            navController = navController,
            perguntas = perguntasAtuais,
            linguagens = linguagens,
            onAcertar = { questao ->
                if (!questao.resolvida) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            questaoDao.atualizar(questao.copy(resolvida = true))
                            recarregarListas(linguagemDao, questaoDao, linguagens, questoes)
                        } catch (e: Exception) {
                            Log.e("PerguntaScreen", "Erro ao marcar a questão como resolvida", e)
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun SelecaoQuestoes(
    titulo: String,
    questoes: List<Questao>,
    linguagens: List<Linguagem>,
    onFechar: () -> Unit,
    onComecar: (List<Questao>) -> Unit
) {
    // Guarda as desmarcadas para que toda questão comece selecionada
    val desmarcadas = remember { mutableStateListOf<Int>() }
    val praticaveis = questoes.filter { it.resposta.isNotBlank() }
    val selecionadas = praticaveis.filter { it.id !in desmarcadas }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoBg)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFechar) {
                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = DuoTextSecondary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = titulo,
                color = DuoText,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${selecionadas.size}/${praticaveis.size}",
                color = DuoTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "// escolha as questões",
            color = DuoTextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (questoes.isEmpty()) {
                item {
                    Text(
                        text = "Nenhuma questão ainda. Crie a primeira!",
                        color = DuoTextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
            items(questoes, key = { it.id }) { questao ->
                val temResposta = questao.resposta.isNotBlank()
                val marcada = temResposta && questao.id !in desmarcadas
                val alternar = {
                    if (marcada) desmarcadas.add(questao.id) else desmarcadas.remove(questao.id)
                }
                val linguagem = linguagens.find { it.id == questao.linguagemId }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoCard),
                    border = BorderStroke(if (marcada) 2.dp else 1.dp, if (marcada) DuoBlue else DuoCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = temResposta) { alternar() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = marcada,
                            onCheckedChange = { alternar() },
                            enabled = temResposta,
                            colors = CheckboxDefaults.colors(
                                checkedColor = DuoBlue,
                                uncheckedColor = DuoCardBorder,
                                checkmarkColor = DuoBg
                            )
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = questao.enunciado,
                                color = if (temResposta) DuoText else DuoTextSecondary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = buildString {
                                    append(linguagem?.nome ?: "?")
                                    append(" · ")
                                    append(questao.dificuldade.rotulo)
                                    if (!temResposta) append(" · sem resposta cadastrada")
                                    else if (questao.resolvida) append(" · resolvida")
                                },
                                color = DuoTextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onComecar(selecionadas) },
            enabled = selecionadas.isNotEmpty(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DuoBlue,
                contentColor = DuoBg,
                disabledContainerColor = DuoCardBorder,
                disabledContentColor = DuoTextSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Começar (${selecionadas.size})", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun Pratica(
    navController: NavController,
    perguntas: List<Pergunta>,
    linguagens: List<Linguagem>,
    onAcertar: (Questao) -> Unit
) {
    var indiceAtual by remember { mutableIntStateOf(0) }
    var selecionada by remember { mutableIntStateOf(-1) }
    var respondida by remember { mutableStateOf(false) }
    var acertos by remember { mutableIntStateOf(0) }

    val total = perguntas.size
    val pergunta = perguntas[indiceAtual]
    val ultimaPergunta = indiceAtual == total - 1
    val acertou = selecionada == pergunta.corretaIndex
    val linguagem = linguagens.find { it.id == pergunta.questao.linguagemId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoBg)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Fechar", tint = DuoTextSecondary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            LinearProgressIndicator(
                progress = { (indiceAtual + 1).toFloat() / total },
                color = DuoBlue,
                trackColor = DuoCardBorder,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${indiceAtual + 1}/$total",
                color = DuoTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "// ${linguagem?.nome ?: "questão"} · ${pergunta.questao.dificuldade.rotulo}",
            color = DuoTextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(pergunta.questao.enunciado, color = DuoText, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        pergunta.alternativas.forEachIndexed { indice, texto ->
            val marcada = indice == selecionada
            // Depois de responder, a certa fica verde e a escolhida errada fica laranja
            val corDestaque = when {
                respondida && indice == pergunta.corretaIndex -> DuoGreen
                respondida && marcada -> DuoOrange
                marcada -> DuoBlue
                else -> null
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DuoCard),
                border = BorderStroke(if (corDestaque != null) 2.dp else 1.dp, corDestaque ?: DuoCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clickable(enabled = !respondida) { selecionada = indice }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(corDestaque ?: DuoCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ('A' + indice).toString(),
                            color = if (corDestaque != null) DuoBg else DuoTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(texto, color = DuoText, fontSize = 15.sp)
                }
            }
        }

        if (respondida) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (acertou) "✓ Resposta correta!" else "✗ A resposta certa é: ${pergunta.alternativas[pergunta.corretaIndex]}",
                color = if (acertou) DuoGreen else DuoOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (!respondida) {
                    respondida = true
                    if (acertou) {
                        acertos++
                        onAcertar(pergunta.questao)
                    }
                } else if (ultimaPergunta) {
                    val praticadas = nomesDasLinguagens(perguntas, linguagens)
                    navController.navigate("resultado/$acertos/$total/${Uri.encode(praticadas)}") {
                        popUpTo(Rotas.TRILHA)
                        launchSingleTop = true
                    }
                } else {
                    indiceAtual++
                    selecionada = -1
                    respondida = false
                }
            },
            enabled = selecionada != -1,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DuoBlue,
                contentColor = DuoBg,
                disabledContainerColor = DuoCardBorder,
                disabledContentColor = DuoTextSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = when {
                    !respondida -> "Confirmar"
                    ultimaPergunta -> "Finalizar"
                    else -> "Próxima"
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}
