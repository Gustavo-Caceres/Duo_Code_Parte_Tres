package com.example.duocode.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.duocode.data.AppDatabase
import com.example.duocode.data.repository.LinguagemRepository
import com.example.duocode.data.repository.QuestaoRepository
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoCardBorder
import com.example.duocode.ui.theme.DuoOrange
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary
import com.example.duocode.viewmodel.LinguagensViewModel
import com.example.duocode.viewmodel.LinguagensViewModelFactory

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
    viewModel: LinguagensViewModel = viewModel(
        factory = LinguagensViewModelFactory(
            LinguagemRepository(AppDatabase.getDatabase(LocalContext.current).linguagemDao()),
            QuestaoRepository(AppDatabase.getDatabase(LocalContext.current).questaoDao())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val linguagens = uiState.linguagens
    val novoNome = uiState.novoNome
    val erroNovoNome = uiState.erroNovoNome

    uiState.linguagemParaRemover?.let { linguagem ->
        val totalDeQuestoes = uiState.questoesDaLinguagemParaRemover
        AlertDialog(
            onDismissRequest = { viewModel.pedirRemocao(null) },
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
                    onClick = { viewModel.removerLinguagem(linguagem) },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoOrange, contentColor = DuoBg)
                ) {
                    Text("Remover")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.pedirRemocao(null) }) {
                    Text("Cancelar", color = DuoTextSecondary)
                }
            }
        )
    }

    uiState.linguagemEmEdicao?.let { linguagem ->
        EditarLinguagemDialogo(
            nome = uiState.nomeEdicao,
            sigla = uiState.siglaEdicao,
            descricao = uiState.descricaoEdicao,
            erro = uiState.erroEdicao,
            onNomeChange = { viewModel.alterarNomeEdicao(it) },
            onSiglaChange = { viewModel.alterarSiglaEdicao(it) },
            onDescricaoChange = { viewModel.alterarDescricaoEdicao(it) },
            onCancelar = { viewModel.editarLinguagem(null) },
            onSalvar = { viewModel.atualizarLinguagem() }
        )
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
                onValueChange = { viewModel.alterarNovoNome(it) },
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
                onClick = { viewModel.adicionarLinguagem() },
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
                        IconButton(onClick = { viewModel.editarLinguagem(linguagem) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = DuoTextSecondary
                            )
                        }
                        IconButton(onClick = { viewModel.pedirRemocao(linguagem) }) {
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

@Composable
private fun EditarLinguagemDialogo(
    nome: String,
    sigla: String,
    descricao: String,
    erro: String?,
    onNomeChange: (String) -> Unit,
    onSiglaChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onCancelar: () -> Unit,
    onSalvar: () -> Unit
) {
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
                    onValueChange = onNomeChange,
                    singleLine = true,
                    colors = coresCampoLinguagem(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Sigla", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = sigla,
                    onValueChange = onSiglaChange,
                    singleLine = true,
                    colors = coresCampoLinguagem(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Descrição", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = onDescricaoChange,
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
                onClick = onSalvar,
                enabled = erro == null,
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
