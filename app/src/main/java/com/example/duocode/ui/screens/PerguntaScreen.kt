package com.example.duocode.ui.screens

import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.duocode.data.AppDatabase
import com.example.duocode.data.Questao
import com.example.duocode.data.repository.LinguagemRepository
import com.example.duocode.data.repository.QuestaoRepository
import com.example.duocode.ui.navigation.Rotas
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoCardBorder
import com.example.duocode.ui.theme.DuoGreen
import com.example.duocode.ui.theme.DuoOrange
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary
import com.example.duocode.viewmodel.PraticaUiState
import com.example.duocode.viewmodel.PraticaViewModel
import com.example.duocode.viewmodel.PraticaViewModelFactory

@Composable
fun PerguntaScreen(
    navController: NavController,
    linguagemId: Int,
    viewModel: PraticaViewModel = viewModel(
        factory = PraticaViewModelFactory(
            LinguagemRepository(AppDatabase.getDatabase(LocalContext.current).linguagemDao()),
            QuestaoRepository(AppDatabase.getDatabase(LocalContext.current).questaoDao()),
            linguagemId
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.perguntas == null) {
        SelecaoQuestoes(
            uiState = uiState,
            onAlternar = { viewModel.alternarSelecao(it) },
            onFechar = { navController.popBackStack() },
            onComecar = { viewModel.comecar() }
        )
    } else {
        Pratica(
            uiState = uiState,
            onFechar = { navController.popBackStack() },
            onEscolher = { viewModel.escolherAlternativa(it) },
            onConfirmar = { viewModel.confirmar() },
            onProxima = { viewModel.proxima() },
            onFinalizar = {
                navController.navigate("resultado/${uiState.acertos}/${uiState.total}/${Uri.encode(uiState.linguagensPraticadas)}") {
                    popUpTo(Rotas.TRILHA)
                    launchSingleTop = true
                }
            }
        )
    }
}

@Composable
private fun SelecaoQuestoes(
    uiState: PraticaUiState,
    onAlternar: (Questao) -> Unit,
    onFechar: () -> Unit,
    onComecar: () -> Unit
) {
    val questoes = uiState.questoesDaPratica
    val selecionadas = uiState.selecionadas

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
                text = uiState.titulo,
                color = DuoText,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${selecionadas.size}/${uiState.praticaveis.size}",
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
                val marcada = temResposta && questao.id !in uiState.desmarcadas
                val alternar = { onAlternar(questao) }
                val linguagem = uiState.linguagens.find { it.id == questao.linguagemId }

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
            onClick = onComecar,
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
    uiState: PraticaUiState,
    onFechar: () -> Unit,
    onEscolher: (Int) -> Unit,
    onConfirmar: () -> Unit,
    onProxima: () -> Unit,
    onFinalizar: () -> Unit
) {
    val pergunta = uiState.perguntaAtual ?: return
    val indiceAtual = uiState.indiceAtual
    val total = uiState.total
    val selecionada = uiState.selecionada
    val respondida = uiState.respondida
    val acertou = uiState.acertou
    val linguagem = uiState.linguagens.find { it.id == pergunta.questao.linguagemId }

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
                    .clickable(enabled = !respondida) { onEscolher(indice) }
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
                when {
                    !respondida -> onConfirmar()
                    uiState.ultimaPergunta -> onFinalizar()
                    else -> onProxima()
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
                    uiState.ultimaPergunta -> "Finalizar"
                    else -> "Próxima"
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}
