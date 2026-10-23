package com.example.duocode.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.duocode.data.AppDatabase
import com.example.duocode.data.inserirDadosIniciais
import com.example.duocode.data.recarregarListas
import com.example.duocode.model.Linguagem
import com.example.duocode.model.Questao
import com.example.duocode.ui.screens.DetalheLinguagemScreen
import com.example.duocode.ui.screens.DetalheQuestaoScreen
import com.example.duocode.ui.screens.LinguagensScreen
import com.example.duocode.ui.screens.PerfilScreen
import com.example.duocode.ui.screens.PerguntaScreen
import com.example.duocode.ui.screens.QuestoesScreen
import com.example.duocode.ui.screens.ResultadoScreen
import com.example.duocode.ui.screens.TrilhaScreen
import com.example.duocode.ui.theme.DuoBg
import com.example.duocode.ui.theme.DuoBlue
import com.example.duocode.ui.theme.DuoCard
import com.example.duocode.ui.theme.DuoTextSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private data class AbaNav(val rota: String, val label: String, val icone: androidx.compose.ui.graphics.vector.ImageVector)

private val abas = listOf(
    AbaNav(Rotas.TRILHA, "trilha", Icons.Default.Timeline),
    AbaNav(Rotas.LINGUAGENS, "linguagens", Icons.Default.Code),
    AbaNav(Rotas.QUESTOES, "questões", Icons.AutoMirrored.Filled.List),
    AbaNav(Rotas.PERFIL, "perfil", Icons.Default.Person)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val banco = remember { AppDatabase.getDatabase(context) }
    val linguagemDao = banco.linguagemDao()
    val questaoDao = banco.questaoDao()

    val linguagens = remember { mutableStateListOf<Linguagem>() }
    val questoes = remember { mutableStateListOf<Questao>() }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                inserirDadosIniciais(linguagemDao, questaoDao)
                recarregarListas(linguagemDao, questaoDao, linguagens, questoes)
            } catch (e: Exception) {
                Log.e("AppNavigation", "Erro ao carregar os dados do banco", e)
            }
        }
    }

    Scaffold(
        containerColor = DuoBg,
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val rotaAtual = backStackEntry?.destination

            NavigationBar(containerColor = DuoCard) {
                abas.forEach { aba ->
                    val selecionada = rotaAtual?.hierarchy?.any { it.route == aba.rota } == true
                    NavigationBarItem(
                        selected = selecionada,
                        onClick = {
                            navController.navigate(aba.rota) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(aba.icone, contentDescription = aba.label) },
                        label = { Text(aba.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DuoBlue,
                            selectedTextColor = DuoBlue,
                            unselectedIconColor = DuoTextSecondary,
                            unselectedTextColor = DuoTextSecondary,
                            indicatorColor = DuoBg
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rotas.TRILHA,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rotas.TRILHA) { TrilhaScreen(navController) }
            composable(
                route = Rotas.PERGUNTA,
                arguments = listOf(navArgument("linguagemId") { type = NavType.IntType })
            ) { backStackEntry ->
                val linguagemId = backStackEntry.arguments?.getInt("linguagemId") ?: -1
                PerguntaScreen(navController, linguagemId, linguagens, questoes, linguagemDao, questaoDao)
            }
            composable(
                route = Rotas.RESULTADO,
                arguments = listOf(
                    navArgument("acertos") { type = NavType.IntType },
                    navArgument("total") { type = NavType.IntType },
                    navArgument("linguagens") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val acertos = backStackEntry.arguments?.getInt("acertos") ?: 0
                val total = backStackEntry.arguments?.getInt("total") ?: 0
                val praticadas = backStackEntry.arguments?.getString("linguagens") ?: "programação"
                ResultadoScreen(navController, acertos, total, praticadas)
            }
            composable(Rotas.LINGUAGENS) { LinguagensScreen(navController, linguagens, questoes, linguagemDao, questaoDao) }
            composable(Rotas.QUESTOES) { QuestoesScreen(navController, linguagens, questoes, linguagemDao, questaoDao) }
            composable(Rotas.PERFIL) { PerfilScreen() }
            composable(
                route = Rotas.DETALHE_LINGUAGEM,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                DetalheLinguagemScreen(navController, id, linguagens, questoes, linguagemDao, questaoDao)
            }
            composable(
                route = Rotas.DETALHE_QUESTAO,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                DetalheQuestaoScreen(navController, id, linguagens, questoes, linguagemDao, questaoDao)
            }
        }
    }
}
