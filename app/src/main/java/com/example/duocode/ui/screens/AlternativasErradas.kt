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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.duocode.ui.theme.DuoOrange
import com.example.duocode.ui.theme.DuoTextSecondary

@Composable
fun MensagemDeErro(mensagem: String?) {
    if (mensagem != null) {
        Text(mensagem, color = DuoOrange, fontSize = 13.sp)
    }
}

@Composable
fun CamposAlternativasErradas(
    alternativas: List<String>,
    cores: TextFieldColors,
    onAlterar: (indice: Int, texto: String) -> Unit
) {
    Text("Alternativas erradas", color = DuoTextSecondary, fontSize = 13.sp)
    Spacer(modifier = Modifier.height(6.dp))
    alternativas.forEachIndexed { indice, texto ->
        OutlinedTextField(
            value = texto,
            onValueChange = { onAlterar(indice, it) },
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
