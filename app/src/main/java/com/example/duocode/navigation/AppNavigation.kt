package com.example.duocode.navigation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.duocode.model.Dificuldade
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

    val linguagens = remember {
        mutableStateListOf(
            Linguagem(id = 1, nome = "JavaScript", sigla = "JS", descricao = "Web, front-end e Node"),
            Linguagem(id = 2, nome = "Python", sigla = "PY", descricao = "Scripts, dados e automação"),
            Linguagem(id = 3, nome = "Kotlin", sigla = "KT", descricao = "Android nativo"),
            Linguagem(id = 4, nome = "SQL", sigla = "SQL", descricao = "Consultas e bancos relacionais")
        )
    }
    val questoes = remember {
        mutableStateListOf(
            Questao(1, "Qual é a saída de arr.length após arr.push(4)?", "4", linguagemId = 1, dificuldade = Dificuldade.FACIL, resolvida = true),
            Questao(2, "Qual a diferença entre let e const?", "const não pode ser reatribuída", linguagemId = 1, dificuldade = Dificuldade.FACIL, resolvida = true),
            Questao(3, "O que [1, 2, 3].map(x => x * 2) retorna?", "[2, 4, 6]", linguagemId = 1, dificuldade = Dificuldade.MEDIO),
            Questao(4, "Qual a ordem de execução do event loop?", "call stack, microtasks, macrotasks", linguagemId = 1, dificuldade = Dificuldade.DIFICIL),
            Questao(5, "O que len(\"dev\") retorna?", "3", linguagemId = 2, dificuldade = Dificuldade.FACIL),
            Questao(6, "Qual palavra-chave declara um valor imutável em Kotlin?", "val", linguagemId = 3, dificuldade = Dificuldade.MEDIO),
            Questao(7, "Qual cláusula filtra grupos depois de um GROUP BY?", "HAVING", linguagemId = 4, dificuldade = Dificuldade.DIFICIL)
        )
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
            composable(Rotas.PERGUNTA) { PerguntaScreen(navController) }
            composable(
                route = Rotas.RESULTADO,
                arguments = listOf(
                    navArgument("acertos") { type = NavType.IntType },
                    navArgument("total") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val acertos = backStackEntry.arguments?.getInt("acertos") ?: 0
                val total = backStackEntry.arguments?.getInt("total") ?: 0
                ResultadoScreen(navController, acertos, total)
            }
            composable(Rotas.LINGUAGENS) { LinguagensScreen(navController, linguagens, questoes) }
            composable(Rotas.QUESTOES) { QuestoesScreen(navController, linguagens, questoes) }
            composable(Rotas.PERFIL) { PerfilScreen() }
            composable(
                route = Rotas.DETALHE_LINGUAGEM,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                DetalheLinguagemScreen(navController, id, linguagens, questoes)
            }
            composable(
                route = Rotas.DETALHE_QUESTAO,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                DetalheQuestaoScreen(navController, id, linguagens, questoes)
            }
        }
    }
}
