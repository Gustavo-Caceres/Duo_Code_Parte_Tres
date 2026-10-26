package com.example.duocode.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
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
import com.example.duocode.ui.theme.DuoOrange
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun corDaSigla(sigla: String): Color = when (sigla) {
    "JS" -> com.example.duocode.ui.theme.DuoAmber
    "PY" -> DuoBlue
    "KT" -> com.example.duocode.ui.theme.DuoPurple
    "SQL" -> com.example.duocode.ui.theme.DuoGreen
    else -> DuoTextSecondary
}

@Composable
fun LinguagensScreen(
    navController: NavController,
    linguagens: SnapshotStateList<Linguagem>,
    questoes: SnapshotStateList<Questao>,
    linguagemDao: LinguagemDao,
    questaoDao: QuestaoDao
) {
    var novoNome by remember { mutableStateOf("") }
    var linguagemEmEdicao by remember { mutableStateOf<Linguagem?>(null) }
    var linguagemParaRemover by remember { mutableStateOf<Linguagem?>(null) }
    val erroNovoNome = validarNomeLinguagem(novoNome, linguagens)

    linguagemParaRemover?.let { linguagem ->
        val totalDeQuestoes = questoes.count { it.linguagemId == linguagem.id }
        AlertDialog(
            onDismissRequest = { linguagemParaRemover = null },
            containerColor = DuoCard,
            title = { Text("Remover ${linguagem.nome}?", color = DuoText, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = if (totalDeQuestoes > 0) {
                        "As $totalDeQuestoes questões dessa linguagem também serão apagadas."
                    } else {
                        "Essa linguagem não tem questões cadastradas."
                    },
                    color = DuoTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                linguagemDao.deletar(linguagem)
                                recarregarListas(linguagemDao, questaoDao, linguagens, questoes)
                            } catch (e: Exception) {
                                Log.e("LinguagensScreen", "Erro ao remover a linguagem", e)
                            }
                        }
                        linguagemParaRemover = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoOrange, contentColor = DuoBg)
                ) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(onClick = { linguagemParaRemover = null }) {
                    Text("Cancelar", color = DuoTextSecondary)
                }
            }
        )
    }

    linguagemEmEdicao?.let { linguagem ->
        EditarLinguagemDialogo(
            linguagem = linguagem,
            linguagens = linguagens,
            onCancelar = { linguagemEmEdicao = null },
            onSalvar = { atualizada ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        linguagemDao.atualizar(atualizada)
                        recarregarListas(linguagemDao, questaoDao, linguagens, questoes)
                    } catch (e: Exception) {
                        Log.e("LinguagensScreen", "Erro ao atualizar a linguagem", e)
                    }
                }
                linguagemEmEdicao = null
            }
        )
    }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                recarregarListas(linguagemDao, questaoDao, linguagens, questoes)
            } catch (e: Exception) {
                Log.e("LinguagensScreen", "Erro ao buscar as linguagens", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoBg)
            .padding(16.dp)
    ) {
        Text(
            text = "Linguagens",
            color = DuoText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = novoNome,
                onValueChange = { novoNome = it },
                placeholder = { Text("Nova linguagem", color = DuoTextSecondary) },
                singleLine = true,
                isError = novoNome.isNotBlank() && erroNovoNome != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DuoText,
                    unfocusedTextColor = DuoText,
                    focusedBorderColor = DuoCardBorder,
                    unfocusedBorderColor = DuoCardBorder,
                    focusedContainerColor = DuoCard,
                    unfocusedContainerColor = DuoCard,
                    errorTextColor = DuoText,
                    errorBorderColor = DuoOrange,
                    errorContainerColor = DuoCard
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                enabled = erroNovoNome == null,
                onClick = {
                    val nome = novoNome.trim()
                    if (erroNovoNome == null) {
                        val sigla = nome.take(2).uppercase()
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                linguagemDao.inserir(Linguagem(nome = nome, sigla = sigla))
                                recarregarListas(linguagemDao, questaoDao, linguagens, questoes)
                            } catch (e: Exception) {
                                Log.e("LinguagensScreen", "Erro ao inserir a linguagem", e)
                            }
                        }
                        novoNome = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DuoBlue,
                    contentColor = DuoBg,
                    disabledContainerColor = DuoCardBorder,
                    disabledContentColor = DuoTextSecondary
                )
            ) {
                Text("Adicionar")
            }
        }

        if (novoNome.isNotBlank() && erroNovoNome != null) {
            Spacer(modifier = Modifier.height(6.dp))
            MensagemDeErro(erroNovoNome)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(linguagens, key = { it.id }) { linguagem ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DuoCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("detalhe_linguagem/${linguagem.id}") }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(corDaSigla(linguagem.sigla), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = linguagem.sigla,
                                color = DuoBg,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = linguagem.nome,
                            color = DuoText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { linguagemEmEdicao = linguagem }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = DuoTextSecondary
                            )
                        }
                        IconButton(onClick = { linguagemParaRemover = linguagem }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remover",
                                tint = DuoTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun validarNomeLinguagem(nome: String, linguagens: List<Linguagem>, idAtual: Int = 0): String? = when {
    nome.isBlank() -> "Digite o nome da linguagem"
    nome.trim().length < 2 -> "O nome precisa ter pelo menos 2 letras"
    nome.trim().length > 20 -> "O nome pode ter no máximo 20 letras"
    linguagens.any { it.id != idAtual && it.nome.equals(nome.trim(), ignoreCase = true) } ->
        "Já existe uma linguagem com esse nome"
    else -> null
}

@Composable
private fun EditarLinguagemDialogo(
    linguagem: Linguagem,
    linguagens: List<Linguagem>,
    onCancelar: () -> Unit,
    onSalvar: (Linguagem) -> Unit
) {
    var nome by remember { mutableStateOf(linguagem.nome) }
    var sigla by remember { mutableStateOf(linguagem.sigla) }
    var descricao by remember { mutableStateOf(linguagem.descricao) }

    val erro = validarNomeLinguagem(nome, linguagens, idAtual = linguagem.id)
        ?: if (sigla.isBlank()) "Informe a sigla (até 3 letras)" else null
    val podeSalvar = erro == null

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = DuoCard,
        title = { Text("Editar linguagem", color = DuoText, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Nome", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    singleLine = true,
                    colors = coresCampoLinguagem(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Sigla", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = sigla,
                    onValueChange = { texto -> sigla = texto.filter { it.isLetterOrDigit() }.uppercase().take(3) },
                    singleLine = true,
                    colors = coresCampoLinguagem(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Descrição", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    minLines = 2,
                    colors = coresCampoLinguagem(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (erro != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    MensagemDeErro(erro)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSalvar(
                        linguagem.copy(
                            nome = nome.trim(),
                            sigla = sigla.trim(),
                            descricao = descricao.trim()
                        )
                    )
                },
                enabled = podeSalvar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DuoBlue,
                    contentColor = DuoBg,
                    disabledContainerColor = DuoCardBorder,
                    disabledContentColor = DuoTextSecondary
                )
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = DuoTextSecondary)
            }
        }
    )
}

@Composable
private fun coresCampoLinguagem() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = DuoText,
    unfocusedTextColor = DuoText,
    focusedBorderColor = DuoBlue,
    unfocusedBorderColor = DuoCardBorder,
    focusedContainerColor = DuoBg,
    unfocusedContainerColor = DuoBg
)
