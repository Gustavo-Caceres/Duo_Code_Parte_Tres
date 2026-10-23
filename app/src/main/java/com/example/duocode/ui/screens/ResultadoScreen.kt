package com.example.duocode.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ResultadoScreen(navController: NavController, acertos: Int, total: Int) {
    val percentual = if (total == 0) 0 else (acertos * 100) / total
    val xpGanho = acertos * 6
    val moedas = acertos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuoBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(88.dp)
                .background(DuoCard, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = DuoAmber, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Lição concluída!", color = DuoText, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("Laços & iteração · unidade 4", color = DuoTextSecondary, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EstatisticaResultado("+$xpGanho", "XP", DuoBlue, Modifier.weight(1f))
            EstatisticaResultado("$percentual%", "ACERTOS", DuoGreen, Modifier.weight(1f))
            EstatisticaResultado("+$moedas", "MOEDAS", DuoAmber, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DuoCard),
            border = BorderStroke(1.dp, DuoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Streak de commits", color = DuoText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("12 dias", color = DuoGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                repeat(2) { linha ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = if (linha == 0) 0.dp else 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(7) { coluna ->
                            val apagado = linha == 1 && coluna >= 5
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .background(
                                        if (apagado) DuoGreen.copy(alpha = 0.3f) else DuoGreen,
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = DuoBg),
            border = BorderStroke(1.dp, DuoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$ git commit -m \"feat: completei laços de repetição\"",
                color = DuoAmber,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                modifier = Modifier.padding(14.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                navController.navigate(Rotas.TRILHA) {
                    popUpTo(Rotas.TRILHA) { inclusive = true }
                    launchSingleTop = true
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoBlue, contentColor = DuoBg),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun EstatisticaResultado(valor: String, rotulo: String, cor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
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
            Text(valor, color = cor, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(4.dp))
            Text(rotulo, color = DuoTextSecondary, fontSize = 11.sp)
        }
    }
}
