package com.example.duocode.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.duocode.model.Questao
import com.example.duocode.ui.theme.DuoOrange
import com.example.duocode.ui.theme.DuoTextSecondary

const val QUANTIDADE_ALTERNATIVAS_ERRADAS = 3

@Composable
fun lembrarAlternativasErradas(iniciais: List<String> = emptyList()): SnapshotStateList<String> = remember {
    mutableStateListOf(*Array(QUANTIDADE_ALTERNATIVAS_ERRADAS) { iniciais.getOrElse(it) { "" } })
}

fun alternativasPreenchidas(alternativas: List<String>): List<String> =
    alternativas.map { it.replace("\n", " ").trim() }.filter { it.isNotBlank() }

fun validarQuestao(
    enunciado: String,
    resposta: String,
    alternativasErradas: List<String>,
    questoesExistentes: List<Questao>,
    idAtual: Int = 0
): String? {
    val erradas = alternativasErradas.map { it.trim() }
    return when {
        enunciado.isBlank() -> "Escreva o enunciado da questão"
        enunciado.trim().length < 5 -> "O enunciado precisa ter pelo menos 5 caracteres"
        questoesExistentes.any { it.id != idAtual && it.enunciado.trim().equals(enunciado.trim(), ignoreCase = true) } ->
            "Já existe uma questão com esse enunciado"
        resposta.isBlank() -> "Informe a resposta certa"
        erradas.any { it.isBlank() } -> "Preencha as $QUANTIDADE_ALTERNATIVAS_ERRADAS alternativas erradas"
        erradas.any { it.equals(resposta.trim(), ignoreCase = true) } -> "Uma alternativa errada está igual à resposta certa"
        erradas.map { it.lowercase() }.distinct().size < erradas.size -> "As alternativas erradas precisam ser diferentes entre si"
        else -> null
    }
}

@Composable
fun MensagemDeErro(mensagem: String?) {
    if (mensagem != null) {
        Text(mensagem, color = DuoOrange, fontSize = 13.sp)
    }
}

@Composable
fun CamposAlternativasErradas(alternativas: SnapshotStateList<String>, cores: TextFieldColors) {
    Text("Alternativas erradas", color = DuoTextSecondary, fontSize = 13.sp)
    Spacer(modifier = Modifier.height(6.dp))
    alternativas.forEachIndexed { indice, texto ->
        OutlinedTextField(
            value = texto,
            onValueChange = { alternativas[indice] = it },
            placeholder = { Text("Errada ${indice + 1}", color = DuoTextSecondary) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = cores,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
}
