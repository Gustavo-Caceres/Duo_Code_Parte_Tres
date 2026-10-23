package com.example.duocode.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.duocode.navigation.Rotas
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoCardBorder
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary

private data class Pergunta(
    val enunciado: String,
    val codigo: String,
    val alternativas: List<String>,
    val corretaIndex: Int
)

private val perguntas = listOf(
    Pergunta(
        enunciado = "Qual é a saída no console?",
        codigo = "const arr = [1, 2, 3];\narr.push(4);\nconsole.log(arr.length);",
        alternativas = listOf("3", "4", "undefined", "Error"),
        corretaIndex = 1
    ),
    Pergunta(
        enunciado = "O que typeof null retorna?",
        codigo = "console.log(typeof null);",
        alternativas = listOf("\"null\"", "\"object\"", "\"undefined\"", "Error"),
        corretaIndex = 1
    ),
    Pergunta(
        enunciado = "O que [1, 2, 3].map(x => x * 2) retorna?",
        codigo = "const r = [1, 2, 3].map(x => x * 2);\nconsole.log(r);",
        alternativas = listOf("[1, 2, 3]", "[2, 4, 6]", "[1, 4, 9]", "undefined"),
        corretaIndex = 1
    )
)

@Composable
fun PerguntaScreen(navController: NavController) {
    var indiceAtual by remember { mutableIntStateOf(0) }
    var selecionada by remember { mutableIntStateOf(-1) }
    var acertos by remember { mutableIntStateOf(0) }

    val total = perguntas.size
    val pergunta = perguntas[indiceAtual]
    val ultimaPergunta = indiceAtual == total - 1

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
            text = "// questão ${indiceAtual + 1} de $total",
            color = DuoTextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(pergunta.enunciado, color = DuoText, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DuoCard),
            border = BorderStroke(1.dp, DuoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = pergunta.codigo,
                color = DuoText,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        pergunta.alternativas.forEachIndexed { indice, texto ->
            val marcada = indice == selecionada
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DuoCard),
                border = BorderStroke(if (marcada) 2.dp else 1.dp, if (marcada) DuoBlue else DuoCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clickable { selecionada = indice }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(if (marcada) DuoBlue else DuoCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ('A' + indice).toString(),
                            color = if (marcada) DuoBg else DuoTextSecondary,
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

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (selecionada == pergunta.corretaIndex) acertos++
                if (ultimaPergunta) {
                    navController.navigate("resultado/$acertos/$total") {
                        popUpTo(Rotas.TRILHA)
                        launchSingleTop = true
                    }
                } else {
                    indiceAtual++
                    selecionada = -1
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
            Text(if (ultimaPergunta) "Finalizar" else "Confirmar", fontWeight = FontWeight.Bold)
        }
    }
}
