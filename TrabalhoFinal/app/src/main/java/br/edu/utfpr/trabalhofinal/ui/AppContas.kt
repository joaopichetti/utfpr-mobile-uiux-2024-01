package br.edu.utfpr.trabalhofinal.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.utfpr.trabalhofinal.ui.conta.form.FormularioContaScreen
import br.edu.utfpr.trabalhofinal.ui.conta.lista.ListaContasScreen

private object Screens {
    const val LISTA_CONTAS = "listaContas"
    const val FORMULARIO_CONTA = "formularioConta"
}

object Arguments {
    const val ID_CONTA = "idConta"
}

private object Routes {
    const val LISTA_CONTAS = Screens.LISTA_CONTAS
    const val FORMULARIO_CONTA = "${Screens.FORMULARIO_CONTA}?${Arguments.ID_CONTA}={${Arguments.ID_CONTA}}"
}

@Composable
fun AppContas(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.LISTA_CONTAS
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Routes.LISTA_CONTAS) {
            ListaContasScreen(
                onAdicionarPressed = {
                    navController.navigate(Screens.FORMULARIO_CONTA)
                },
                onContaPressed = { conta ->
                    navController.navigate("${Screens.FORMULARIO_CONTA}?${Arguments.ID_CONTA}=${conta.id}")
                }
            )
        }
        composable(
            route = Routes.FORMULARIO_CONTA,
            arguments = listOf(
                navArgument(name = Arguments.ID_CONTA) { type = NavType.StringType; nullable = true }
            )
        ) {
            FormularioContaScreen(
                onVoltarPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}