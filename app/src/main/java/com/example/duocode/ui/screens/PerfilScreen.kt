package com.example.duocode.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.duocode.ui.theme.DuoAmber
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoCardBorder
import com.example.duocode.ui.theme.DuoGreen
import com.example.duocode.ui.theme.DuoPurple
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary

private data class Badge(val label: String, val cor: Color)
private data class RankingLinha(val posicao: Int, val nome: String, val xp: Int, val voce: Boolean = false)

@Composable
fun PerfilScreen() {
    val badges = listOf(
        Badge("JS", DuoAmber),
        Badge("PYTHON", DuoBlue),
        Badge("SQL", DuoGreen),
        Badge("+3", DuoTextSecondary)
    )
    val ranking = listOf(
        RankingLinha(1, "luiza.dev", 610),
        RankingLinha(2, "kaique_ts", 540),
        RankingLinha(3, "Andre R. (você)", 340, voce = true),
        RankingLinha(4, "pedro.py", 298),
        RankingLinha(5, "bia_codes", 265)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoBg)
            .padding(16.dp)
    ) {
        Text("Perfil", color = DuoText, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(DuoPurple, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("AR", color = DuoBg, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Andre R.", color = DuoText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Dev nível 12 · Liga Bronze", color = DuoTextSecondary, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            badges.forEach { badge ->
                Box(
                    modifier = Modifier
                        .background(DuoCard, RoundedCornerShape(8.dp))
                        .border(BorderStroke(1.dp, badge.cor), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badge.label,
                        color = badge.cor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EstatisticaCard("12", "dias de streak", DuoGreen, Modifier.weight(1f))
            EstatisticaCard("340", "xp na semana", DuoAmber, Modifier.weight(1f))
            EstatisticaCard("4", "unidades", DuoBlue, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DuoCard),
            border = BorderStroke(1.dp, DuoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Liga Bronze", color = DuoText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("termina em 3d", color = DuoTextSecondary, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                ranking.forEach { linha ->
                    val rowModifier = if (linha.voce) {
                        Modifier
                            .fillMaxWidth()
                            .background(DuoBlue.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.dp, DuoBlue), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    }
                    Row(
                        modifier = rowModifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${linha.posicao}",
                            color = if (linha.voce) DuoBlue else DuoTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(24.dp)
                        )
                        Text(
                            text = linha.nome,
                            color = DuoText,
                            fontWeight = if (linha.voce) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${linha.xp} xp",
                            color = DuoTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EstatisticaCard(valor: String, rotulo: String, cor: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DuoCard),
        border = BorderStroke(1.dp, DuoCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, color = cor, fontWeight = FontWeight.Bold, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(4.dp))
            Text(rotulo, color = DuoTextSecondary, fontSize = 12.sp)
        }
    }
}
