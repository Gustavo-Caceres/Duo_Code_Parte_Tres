package com.example.duocode.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.duocode.navigation.Rotas
import com.example.duocode.ui.theme.DuoAmber
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoCardBorder
import com.example.duocode.ui.theme.DuoGreen
import com.example.duocode.ui.theme.DuoText
import com.example.duocode.ui.theme.DuoTextSecondary

@Composable
fun TrilhaScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoBg)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(7) { indice ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (indice % 3 == 0) DuoGreen.copy(alpha = 0.4f) else DuoGreen,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("12 dias", color = DuoText, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            }
            Text(
                text = "340 xp",
                color = DuoAmber,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DuoCard),
            border = BorderStroke(1.dp, DuoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("// unidade 4", color = DuoTextSecondary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    Text("6/10", color = DuoBlue, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Laços & iteração", color = DuoText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("JavaScript · for, while, map", color = DuoTextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { 0.6f },
                    color = DuoBlue,
                    trackColor = DuoCardBorder,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            NoCompleto()
            NoCompleto()

            Text(
                text = "COMEÇAR",
                color = DuoBg,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .background(DuoBlue, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
            NoAtual(onClick = { navController.navigate(Rotas.PERGUNTA) })

            NoBloqueado()
            NoBloqueado()
            NoBloqueado()
        }
    }
}

@Composable
private fun NoCompleto() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(DuoBg, CircleShape)
            .border(BorderStroke(2.dp, DuoGreen), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Check, contentDescription = "Lição concluída", tint = DuoGreen)
    }
}

@Composable
private fun NoAtual(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(DuoBg, RoundedCornerShape(18.dp))
            .border(BorderStroke(2.dp, DuoBlue), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("for", color = DuoBlue, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
private fun NoBloqueado() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(DuoCard, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, DuoCardBorder), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = DuoTextSecondary)
    }
}
