package com.example.lavajato.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lavajato.ui.screens.AgendamentoScreen
import com.example.lavajato.ui.screens.AvaliacoesScreen
import com.example.lavajato.ui.screens.CadastroScreen
import com.example.lavajato.ui.screens.HomeScreen
import com.example.lavajato.ui.screens.LoginScreen
import com.example.lavajato.ui.screens.MeusAgendamentosScreen
import com.example.lavajato.ui.screens.PerfilScreen

object AppRoutes {
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"
    const val HOME = "home"
    const val AGENDAMENTO = "agendamento"
    const val MEUS_AGENDAMENTOS = "meus_agendamentos"
    const val AVALIACOES = "avaliacoes"
    const val PERFIL = "perfil"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onEntrarClick = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onCadastreSeClick = {
                    navController.navigate(AppRoutes.CADASTRO)
                }
            )
        }

        composable(AppRoutes.CADASTRO) {
            CadastroScreen(
                onCadastrarClick = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onJaTenhoContaClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.HOME) {
            HomeScreen(
                onAgendarServicoClick = {
                    navController.navigate(AppRoutes.AGENDAMENTO)
                },
                onMeusAgendamentosClick = {
                    navController.navigate(AppRoutes.MEUS_AGENDAMENTOS)
                },
                onAvaliacoesClick = {
                    navController.navigate(AppRoutes.AVALIACOES)
                },
                onPerfilClick = {
                    navController.navigate(AppRoutes.PERFIL)
                }
            )
        }

        composable(AppRoutes.AGENDAMENTO) {
            AgendamentoScreen(
                onVoltarClick = { navController.popBackStack() },
                onConcluidoClick = {
                    navController.navigate(AppRoutes.MEUS_AGENDAMENTOS) {
                        popUpTo(AppRoutes.HOME)
                    }
                }
            )
        }

        composable(AppRoutes.MEUS_AGENDAMENTOS) {
            MeusAgendamentosScreen(
                onVoltarClick = { navController.popBackStack() },
                onInicioClick = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.AVALIACOES) {
            AvaliacoesScreen(onVoltarClick = { navController.popBackStack() })
        }

        composable(AppRoutes.PERFIL) {
            PerfilScreen(
                onInicioClick = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.HOME) { inclusive = true }
                    }
                },
                onAgendamentosClick = {
                    navController.navigate(AppRoutes.MEUS_AGENDAMENTOS)
                },
                onSairClick = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}