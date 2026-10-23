package com.example.duocode.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.duocode.model.Dificuldade
import com.example.duocode.model.Linguagem
import com.example.duocode.model.Questao
import com.example.duocode.navigation.Rotas
import com.example.duocode.ui.theme.DuoAmber
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoCardBorder
import com.example.duocode.ui.theme.DuoGreen
import com.example.duocode.ui.theme.DuoOrange
import com.example.duocode.ui.theme.DuoPurple
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary

private fun corDaLinguagem(sigla: String): Color = when (sigla) {
    "JS" -> DuoAmber
    "PY" -> DuoBlue
    "KT" -> DuoPurple
    "SQL" -> DuoGreen
    else -> DuoTextSecondary
}

private fun corDaDificuldade(dificuldade: Dificuldade): Color = when (dificuldade) {
    Dificuldade.FACIL -> DuoGreen
    Dificuldade.MEDIO -> DuoAmber
    Dificuldade.DIFICIL -> DuoOrange
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheLinguagemScreen(
    navController: NavController,
    linguagemId: Int,
    linguagens: SnapshotStateList<Linguagem>,
    questoes: SnapshotStateList<Questao>
) {
    val linguagem = linguagens.find { it.id == linguagemId }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DuoBg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Linguagem", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DuoBg,
                    titleContentColor = DuoText,
                    navigationIconContentColor = DuoText
                )
            )
        }
    ) { innerPadding ->
        if (linguagem == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Linguagem não encontrada", color = DuoTextSecondary)
            }
            return@Scaffold
        }

        // O detalhe combina as duas listas: tudo abaixo é calculado a partir das questões desta linguagem
        val questoesDaLinguagem = questoes.filter { it.linguagemId == linguagem.id }
        val resolvidas = questoesDaLinguagem.count { it.resolvida }
        val cor = corDaLinguagem(linguagem.sigla)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    CabecalhoLinguagem(linguagem, cor, resolvidas, questoesDaLinguagem.size)
                }
                item {
                    DistribuicaoDificuldade(questoesDaLinguagem)
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Questões de ${linguagem.nome}",
                            color = DuoText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "ver todas",
                            color = DuoBlue,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable {
                                navController.navigate(Rotas.QUESTOES) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
                if (questoesDaLinguagem.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma questão ainda. Crie a primeira!",
                            color = DuoTextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
                items(questoesDaLinguagem, key = { it.id }) { questao ->
                    QuestaoItem(
                        questao = questao,
                        onAlternarResolvida = {
                            val indice = questoes.indexOfFirst { it.id == questao.id }
                            if (indice >= 0) {
                                questoes[indice] = questao.copy(resolvida = !questao.resolvida)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { mostrarDialogo = true },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, DuoBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DuoBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Criar questão", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate(Rotas.PERGUNTA) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DuoBlue, contentColor = DuoBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Praticar ${linguagem.nome}", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (mostrarDialogo) {
            CriarQuestaoDialogo(
                nomeLinguagem = linguagem.nome,
                onCancelar = { mostrarDialogo = false },
                onConfirmar = { enunciado, resposta, dificuldade ->
                    val novoId = (questoes.maxOfOrNull { it.id } ?: 0) + 1
                    questoes.add(
                        Questao(
                            id = novoId,
                            enunciado = enunciado,
                            resposta = resposta,
                            linguagemId = linguagem.id,
                            dificuldade = dificuldade
                        )
                    )
                    mostrarDialogo = false
                }
            )
        }
    }
}

@Composable
private fun CabecalhoLinguagem(linguagem: Linguagem, cor: Color, resolvidas: Int, total: Int) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DuoCard),
        border = BorderStroke(1.dp, DuoCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(cor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = linguagem.sigla,
                        color = DuoBg,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(linguagem.nome, color = DuoText, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    if (linguagem.descricao.isNotBlank()) {
                        Text(linguagem.descricao, color = DuoTextSecondary, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Seu progresso", color = DuoTextSecondary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text(
                    text = "$resolvidas/$total resolvidas",
                    color = cor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { if (total == 0) 0f else resolvidas.toFloat() / total },
                color = cor,
                trackColor = DuoCardBorder,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }
    }
}

@Composable
private fun DistribuicaoDificuldade(questoes: List<Questao>) {
    val contagem = Dificuldade.entries.associateWith { dificuldade ->
        questoes.count { it.dificuldade == dificuldade }
    }

    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Questões por dificuldade",
                color = DuoText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${questoes.size} no total",
                color = DuoTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cada fatia da barra ocupa um peso proporcional à quantidade de questões daquela dificuldade
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(DuoCardBorder)
        ) {
            contagem.filterValues { it > 0 }.forEach { (dificuldade, quantidade) ->
                Box(
                    modifier = Modifier
                        .weight(quantidade.toFloat())
                        .fillMaxHeight()
                        .background(corDaDificuldade(dificuldade))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            contagem.forEach { (dificuldade, quantidade) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(corDaDificuldade(dificuldade), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${dificuldade.rotulo} $quantidade", color = DuoTextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun QuestaoItem(questao: Questao, onAlternarResolvida: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DuoCard),
        border = BorderStroke(1.dp, DuoCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAlternarResolvida() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = questao.resolvida,
                onCheckedChange = { onAlternarResolvida() },
                colors = CheckboxDefaults.colors(
                    checkedColor = DuoGreen,
                    uncheckedColor = DuoCardBorder,
                    checkmarkColor = DuoBg
                )
            )
            Text(
                text = questao.enunciado,
                color = DuoText,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = questao.dificuldade.rotulo,
                color = corDaDificuldade(questao.dificuldade),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
    }
}

@Composable
private fun CriarQuestaoDialogo(
    nomeLinguagem: String,
    onCancelar: () -> Unit,
    onConfirmar: (enunciado: String, resposta: String, dificuldade: Dificuldade) -> Unit
) {
    var enunciado by remember { mutableStateOf("") }
    var resposta by remember { mutableStateOf("") }
    var dificuldade by remember { mutableStateOf(Dificuldade.FACIL) }

    val coresCampo = OutlinedTextFieldDefaults.colors(
        focusedTextColor = DuoText,
        unfocusedTextColor = DuoText,
        focusedBorderColor = DuoBlue,
        unfocusedBorderColor = DuoCardBorder,
        focusedContainerColor = DuoBg,
        unfocusedContainerColor = DuoBg,
        focusedLabelColor = DuoBlue,
        unfocusedLabelColor = DuoTextSecondary
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = DuoCard,
        title = { Text("Nova questão de $nomeLinguagem", color = DuoText, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = enunciado,
                    onValueChange = { enunciado = it },
                    label = { Text("Enunciado") },
                    minLines = 2,
                    colors = coresCampo,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = resposta,
                    onValueChange = { resposta = it },
                    label = { Text("Resposta") },
                    singleLine = true,
                    colors = coresCampo,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Dificuldade", color = DuoTextSecondary, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Dificuldade.entries.forEach { opcao ->
                        FilterChip(
                            selected = dificuldade == opcao,
                            onClick = { dificuldade = opcao },
                            label = { Text(opcao.rotulo) },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = DuoTextSecondary,
                                selectedLabelColor = DuoBg,
                                selectedContainerColor = corDaDificuldade(opcao)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(enunciado.trim(), resposta.trim(), dificuldade) },
                enabled = enunciado.isNotBlank() && resposta.isNotBlank()
            ) {
                Text("Adicionar", color = if (enunciado.isNotBlank() && resposta.isNotBlank()) DuoBlue else DuoTextSecondary)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = DuoTextSecondary)
            }
        }
    )
}
