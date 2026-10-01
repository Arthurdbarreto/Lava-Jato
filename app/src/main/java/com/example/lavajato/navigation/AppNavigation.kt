package com.example.lavajato.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lavajato.ui.screens.AgendamentoScreen
import com.example.lavajato.ui.screens.CadastroScreen
import com.example.lavajato.ui.screens.HomeScreen
import com.example.lavajato.ui.screens.LoginScreen
import com.example.lavajato.ui.screens.MeusAgendamentosScreen

/**
 * Rotas do aplicativo Lava Jato.
 */
object Routes {
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"
    const val HOME = "home"
    const val AGENDAMENTO = "agendamento"
    const val AGENDAMENTOS = "agendamentos"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onEntrarClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onCadastreSeClick = {
                    navController.navigate(Routes.CADASTRO)
                }
            )
        }

        composable(Routes.CADASTRO) {
            CadastroScreen(
                onVoltarClick = { navController.popBackStack() },
                onEntrarClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.CADASTRO) { inclusive = true }
                    }
                },
                onCadastrarClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.CADASTRO) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onAgendarServicoClick = { navController.navigate(Routes.AGENDAMENTO) },
                onMeusAgendamentosClick = { navController.navigate(Routes.AGENDAMENTOS) }
            )
        }

        composable(Routes.AGENDAMENTO) {
            AgendamentoScreen(
                onVoltarClick = { navController.popBackStack() },
                onProximoClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.AGENDAMENTOS) {
            MeusAgendamentosScreen(
                onVoltarClick = { navController.popBackStack() },
                onInicioClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
    }
}
