package com.example.duocode.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.duocode.model.Dificuldade
import com.example.duocode.model.Linguagem
import com.example.duocode.model.Questao
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

private fun corDaSiglaQuestao(sigla: String): Color = when (sigla) {
    "JS" -> DuoAmber
    "PY" -> DuoBlue
    "KT" -> DuoPurple
    "SQL" -> DuoGreen
    else -> DuoTextSecondary
}

private fun corDaDificuldadeQuestao(dificuldade: Dificuldade): Color = when (dificuldade) {
    Dificuldade.FACIL -> DuoGreen
    Dificuldade.MEDIO -> DuoAmber
    Dificuldade.DIFICIL -> DuoOrange
}

@Composable
fun QuestoesScreen(
    navController: NavController,
    linguagens: SnapshotStateList<Linguagem>,
    questoes: SnapshotStateList<Questao>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(DuoBg)
            .padding(horizontal = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Questões",
                    color = DuoText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${questoes.size} questões",
                    color = DuoTextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            }
        }
        item {
            NovaQuestaoFormulario(
                linguagens = linguagens,
                onAdicionar = { enunciado, resposta, linguagemId, dificuldade ->
                    val novoId = (questoes.maxOfOrNull { it.id } ?: 0) + 1
                    questoes.add(
                        Questao(
                            id = novoId,
                            enunciado = enunciado,
                            resposta = resposta,
                            linguagemId = linguagemId,
                            dificuldade = dificuldade
                        )
                    )
                }
            )
        }
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
            QuestaoCard(
                questao = questao,
                linguagem = linguagens.find { it.id == questao.linguagemId },
                onAbrir = { navController.navigate("detalhe_questao/${questao.id}") },
                onRemover = { questoes.remove(questao) }
            )
        }
        item { Spacer(modifier = Modifier.height(4.dp)) }
    }
}

@Composable
private fun NovaQuestaoFormulario(
    linguagens: List<Linguagem>,
    onAdicionar: (enunciado: String, resposta: String, linguagemId: Int, dificuldade: Dificuldade) -> Unit
) {
    var enunciado by remember { mutableStateOf("") }
    var resposta by remember { mutableStateOf("") }
    var linguagemId by remember { mutableStateOf(linguagens.firstOrNull()?.id) }
    var dificuldade by remember { mutableStateOf(Dificuldade.FACIL) }

    // Se a linguagem escolhida for removida, volta para a primeira disponível
    val linguagemSelecionada = linguagens.find { it.id == linguagemId } ?: linguagens.firstOrNull()
    val podeAdicionar = enunciado.isNotBlank() && linguagemSelecionada != null

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DuoCard),
        border = BorderStroke(1.dp, DuoCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("// nova questão", color = DuoTextSecondary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Enunciado", color = DuoTextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = enunciado,
                onValueChange = { enunciado = it },
                placeholder = { Text("ex: O que typeof null retorna?", color = DuoTextSecondary) },
                minLines = 2,
                shape = RoundedCornerShape(10.dp),
                colors = coresCampoQuestao(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Resposta", color = DuoTextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = resposta,
                onValueChange = { resposta = it },
                placeholder = { Text("ex: 4 (opcional)", color = DuoTextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = coresCampoQuestao(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Linguagem", color = DuoTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Seletor(
                        textoSelecionado = linguagemSelecionada?.nome ?: "—",
                        opcoes = linguagens,
                        rotulo = { it.nome },
                        onSelecionar = { linguagemId = it.id }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dificuldade", color = DuoTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Seletor(
                        textoSelecionado = dificuldade.rotulo.replaceFirstChar { it.uppercase() },
                        opcoes = Dificuldade.entries,
                        rotulo = { it.rotulo.replaceFirstChar { c -> c.uppercase() } },
                        onSelecionar = { dificuldade = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (linguagemSelecionada != null) {
                        onAdicionar(enunciado.trim(), resposta.trim(), linguagemSelecionada.id, dificuldade)
                        enunciado = ""
                        resposta = ""
                    }
                },
                enabled = podeAdicionar,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DuoBlue,
                    contentColor = DuoBg,
                    disabledContainerColor = DuoCardBorder,
                    disabledContentColor = DuoTextSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Adicionar questão", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> Seletor(
    textoSelecionado: String,
    opcoes: List<T>,
    rotulo: (T) -> String,
    onSelecionar: (T) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = textoSelecionado,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            shape = RoundedCornerShape(10.dp),
            colors = coresCampoQuestao(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            containerColor = DuoCard
        ) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(rotulo(opcao), color = DuoText) },
                    onClick = {
                        onSelecionar(opcao)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
private fun coresCampoQuestao() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = DuoText,
    unfocusedTextColor = DuoText,
    focusedBorderColor = DuoBlue,
    unfocusedBorderColor = DuoCardBorder,
    focusedContainerColor = DuoBg,
    unfocusedContainerColor = DuoBg,
    focusedTrailingIconColor = DuoText,
    unfocusedTrailingIconColor = DuoText
)

@Composable
private fun QuestaoCard(questao: Questao, linguagem: Linguagem?, onAbrir: () -> Unit, onRemover: () -> Unit) {
    val sigla = linguagem?.sigla ?: "?"

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DuoCard),
        border = BorderStroke(1.dp, DuoCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAbrir() }
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sigla,
                        color = DuoBg,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(corDaSiglaQuestao(sigla), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = questao.dificuldade.rotulo,
                        color = corDaDificuldadeQuestao(questao.dificuldade),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "#${questao.id}",
                        color = DuoTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = questao.enunciado, color = DuoText, fontSize = 15.sp)
            }
            IconButton(onClick = onRemover) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = DuoTextSecondary
                )
            }
        }
    }
}
