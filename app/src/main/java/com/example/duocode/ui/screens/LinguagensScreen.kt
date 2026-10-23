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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.duocode.model.Linguagem
import com.example.duocode.model.Questao
import com.example.duocode.navigation.Rotas
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoCardBorder
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary

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
    questoes: SnapshotStateList<Questao>
) {
    var novoNome by remember { mutableStateOf("") }

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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DuoText,
                    unfocusedTextColor = DuoText,
                    focusedBorderColor = DuoCardBorder,
                    unfocusedBorderColor = DuoCardBorder,
                    focusedContainerColor = DuoCard,
                    unfocusedContainerColor = DuoCard
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val nome = novoNome.trim()
                    if (nome.isNotEmpty()) {
                        val novoId = (linguagens.maxOfOrNull { it.id } ?: 0) + 1
                        val sigla = nome.take(2).uppercase()
                        linguagens.add(Linguagem(id = novoId, nome = nome, sigla = sigla))
                        novoNome = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DuoBlue,
                    contentColor = DuoBg
                )
            ) {
                Text("Adicionar")
            }
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
                        IconButton(onClick = {
                            linguagens.remove(linguagem)
                            questoes.removeAll { it.linguagemId == linguagem.id }
                        }) {
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
