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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.duocode.data.AppDatabase
import com.example.duocode.data.Dificuldade
import com.example.duocode.data.Linguagem
import com.example.duocode.data.Questao
import com.example.duocode.data.repository.LinguagemRepository
import com.example.duocode.data.repository.QuestaoRepository
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
import com.example.duocode.viewmodel.FormularioQuestao
import com.example.duocode.viewmodel.QuestoesViewModel
import com.example.duocode.viewmodel.QuestoesViewModelFactory

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
    viewModel: QuestoesViewModel = viewModel(
        factory = QuestoesViewModelFactory(
            LinguagemRepository(AppDatabase.getDatabase(LocalContext.current).linguagemDao()),
            QuestaoRepository(AppDatabase.getDatabase(LocalContext.current).questaoDao())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val linguagens = uiState.linguagens
    val questoes = uiState.questoes

    uiState.questaoEmEdicao?.let { questao ->
        EditarQuestaoDialogo(
            questao = questao,
            formulario = uiState.edicao,
            linguagens = linguagens,
            linguagemSelecionada = uiState.linguagemDaEdicao,
            erro = uiState.erroEdicao,
            onAlterar = { viewModel.alterarEdicao(it) },
            onCancelar = { viewModel.editarQuestao(null) },
            onSalvar = { viewModel.atualizarQuestao() }
        )
    }

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
                formulario = uiState.novaQuestao,
                linguagens = linguagens,
                linguagemSelecionada = uiState.linguagemDaNovaQuestao,
                erro = uiState.erroNovaQuestao,
                onAlterar = { viewModel.alterarNovaQuestao(it) },
                onAdicionar = { viewModel.adicionarQuestao() }
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
                onEditar = { viewModel.editarQuestao(questao) },
                onRemover = { viewModel.removerQuestao(questao) }
            )
        }
        item { Spacer(modifier = Modifier.height(4.dp)) }
    }
}

@Composable
private fun NovaQuestaoFormulario(
    formulario: FormularioQuestao,
    linguagens: List<Linguagem>,
    linguagemSelecionada: Linguagem?,
    erro: String?,
    onAlterar: (FormularioQuestao) -> Unit,
    onAdicionar: () -> Unit
) {
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
                value = formulario.enunciado,
                onValueChange = { onAlterar(formulario.copy(enunciado = it)) },
                placeholder = { Text("ex: O que typeof null retorna?", color = DuoTextSecondary) },
                minLines = 2,
                shape = RoundedCornerShape(10.dp),
                colors = coresCampoQuestao(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Resposta certa", color = DuoTextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = formulario.resposta,
                onValueChange = { onAlterar(formulario.copy(resposta = it)) },
                placeholder = { Text("ex: \"object\"", color = DuoTextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = coresCampoQuestao(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CamposAlternativasErradas(formulario.alternativasErradas, coresCampoQuestao()) { indice, texto ->
                onAlterar(formulario.comAlternativa(indice, texto))
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Linguagem", color = DuoTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Seletor(
                        textoSelecionado = linguagemSelecionada?.nome ?: "—",
                        opcoes = linguagens,
                        rotulo = { it.nome },
                        onSelecionar = { onAlterar(formulario.copy(linguagemId = it.id)) }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dificuldade", color = DuoTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Seletor(
                        textoSelecionado = formulario.dificuldade.rotulo.replaceFirstChar { it.uppercase() },
                        opcoes = Dificuldade.entries,
                        rotulo = { it.rotulo.replaceFirstChar { c -> c.uppercase() } },
                        onSelecionar = { onAlterar(formulario.copy(dificuldade = it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (formulario.comecouAPreencher || linguagemSelecionada == null) {
                MensagemDeErro(erro)
                if (erro != null) Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = onAdicionar,
                enabled = erro == null,
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

@Composable
private fun EditarQuestaoDialogo(
    questao: Questao,
    formulario: FormularioQuestao,
    linguagens: List<Linguagem>,
    linguagemSelecionada: Linguagem?,
    erro: String?,
    onAlterar: (FormularioQuestao) -> Unit,
    onCancelar: () -> Unit,
    onSalvar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = DuoCard,
        title = { Text("Editar questão #${questao.id}", color = DuoText, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Enunciado", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = formulario.enunciado,
                    onValueChange = { onAlterar(formulario.copy(enunciado = it)) },
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp),
                    colors = coresCampoQuestao(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Resposta certa", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = formulario.resposta,
                    onValueChange = { onAlterar(formulario.copy(resposta = it)) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = coresCampoQuestao(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                CamposAlternativasErradas(formulario.alternativasErradas, coresCampoQuestao()) { indice, texto ->
                    onAlterar(formulario.comAlternativa(indice, texto))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text("Linguagem", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Seletor(
                    textoSelecionado = linguagemSelecionada?.nome ?: "—",
                    opcoes = linguagens,
                    rotulo = { it.nome },
                    onSelecionar = { onAlterar(formulario.copy(linguagemId = it.id)) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Dificuldade", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Seletor(
                    textoSelecionado = formulario.dificuldade.rotulo.replaceFirstChar { it.uppercase() },
                    opcoes = Dificuldade.entries,
                    rotulo = { it.rotulo.replaceFirstChar { c -> c.uppercase() } },
                    onSelecionar = { onAlterar(formulario.copy(dificuldade = it)) }
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
                enabled = erro == null && linguagemSelecionada != null,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DuoBlue,
                    contentColor = DuoBg,
                    disabledContainerColor = DuoCardBorder,
                    disabledContentColor = DuoTextSecondary
                )
            ) {
                Text("Salvar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = DuoTextSecondary)
            }
        }
    )
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
private fun QuestaoCard(
    questao: Questao,
    linguagem: Linguagem?,
    onAbrir: () -> Unit,
    onEditar: () -> Unit,
    onRemover: () -> Unit
) {
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
            IconButton(onClick = onEditar) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = DuoTextSecondary
                )
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
