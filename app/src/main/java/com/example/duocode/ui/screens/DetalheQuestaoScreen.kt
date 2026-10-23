package com.example.duocode.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

private fun corDaSiglaDetalhe(sigla: String) = when (sigla) {
    "JS" -> DuoAmber
    "PY" -> DuoBlue
    "KT" -> DuoPurple
    "SQL" -> DuoGreen
    else -> DuoTextSecondary
}

private fun corDaDificuldadeDetalhe(dificuldade: Dificuldade) = when (dificuldade) {
    Dificuldade.FACIL -> DuoGreen
    Dificuldade.MEDIO -> DuoAmber
    Dificuldade.DIFICIL -> DuoOrange
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheQuestaoScreen(
    navController: NavController,
    id: Int,
    linguagens: SnapshotStateList<Linguagem>,
    questoes: SnapshotStateList<Questao>
) {
    val questao = questoes.find { it.id == id }

    Scaffold(
        containerColor = DuoBg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Questão #$id", fontWeight = FontWeight.Bold) },
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
        if (questao == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Questão não encontrada", color = DuoTextSecondary)
            }
            return@Scaffold
        }

        val linguagem = linguagens.find { it.id == questao.linguagemId }
        val alternarResolvida: () -> Unit = {
            val indice = questoes.indexOfFirst { it.id == questao.id }
            if (indice >= 0) {
                questoes[indice] = questao.copy(resolvida = !questao.resolvida)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (linguagem != null) {
                val totalDaLinguagem = questoes.count { it.linguagemId == questao.linguagemId }
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoCard),
                    border = BorderStroke(1.dp, DuoCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("detalhe_linguagem/${linguagem.id}") }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(corDaSiglaDetalhe(linguagem.sigla), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = linguagem.sigla,
                                color = DuoBg,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(linguagem.nome, color = DuoText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = "$totalDaLinguagem questões cadastradas",
                                color = DuoTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = questao.dificuldade.rotulo,
                color = DuoBg,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .background(corDaDificuldadeDetalhe(questao.dificuldade), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = questao.enunciado,
                color = DuoText,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DuoCard),
                border = BorderStroke(1.dp, DuoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "// resposta",
                        color = DuoTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (questao.resposta.isBlank()) {
                        Text(
                            text = "Sem resposta cadastrada",
                            color = DuoTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp
                        )
                    } else {
                        Text(
                            text = questao.resposta,
                            color = DuoGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { alternarResolvida() }
            ) {
                Checkbox(
                    checked = questao.resolvida,
                    onCheckedChange = { alternarResolvida() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = DuoGreen,
                        uncheckedColor = DuoCardBorder,
                        checkmarkColor = DuoBg
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Marcar como resolvida", color = DuoText, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
