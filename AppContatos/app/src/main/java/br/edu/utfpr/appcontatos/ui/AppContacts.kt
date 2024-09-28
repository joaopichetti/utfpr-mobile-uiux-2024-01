package br.edu.utfpr.appcontatos.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.utfpr.appcontatos.ui.contact.details.ContactDetailsScreen
import br.edu.utfpr.appcontatos.ui.contact.form.ContactFormScreen
import br.edu.utfpr.appcontatos.ui.contact.list.ContactsListScreen

private object Screens {
    const val CONTACTS_LIST = "contactsList"
    const val CONTACT_DETAILS = "contactDetails"
    const val CONTACT_FORM = "contactForm"
}

object Arguments {
    const val CONTACT_ID = "contactId"
}

private object Routes {
    const val CONTACTS_LIST = Screens.CONTACTS_LIST
    // contactDetails/{contactId}
    const val CONTACT_DETAILS = "${Screens.CONTACT_DETAILS}/{${Arguments.CONTACT_ID}}"
    // contactForm?contactId={contactId}
    const val CONTACT_FORM = "${Screens.CONTACT_FORM}?${Arguments.CONTACT_ID}={${Arguments.CONTACT_ID}}"
}

@Composable
fun AppContacts(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.CONTACTS_LIST
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Routes.CONTACTS_LIST) {
            ContactsListScreen(
                onAddPressed = {
                    navController.navigate(Screens.CONTACT_FORM)
                },
                onContactPressed = { contact ->
                    navController.navigate("${Screens.CONTACT_DETAILS}/${contact.id}")
                }
            )
        }
        composable(
            route = Routes.CONTACT_DETAILS,
            arguments = listOf(
                navArgument(name = Arguments.CONTACT_ID) {
                    type = NavType.IntType
                }
            )
        ) { navBackStackEntry ->
            val contactId: Int = navBackStackEntry.arguments?.getInt(Arguments.CONTACT_ID) ?: 0
            ContactDetailsScreen(
                contactId = contactId,
                onBackPressed = {
                    navController.popBackStack()
                },
                onEditPressed = {
                    navController.navigate("${Screens.CONTACT_FORM}?${Arguments.CONTACT_ID}=$contactId")
                },
                onContactDeleted = {
                    navController.popBackStack(
                        route = Screens.CONTACTS_LIST,
                        inclusive = false
                    )
                }
            )
        }
        composable(
            route = Routes.CONTACT_FORM,
            arguments = listOf(
                navArgument(name = Arguments.CONTACT_ID) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            ContactFormScreen(
                onContactSaved = {
                    navController.popBackStack(
                        route = Screens.CONTACTS_LIST,
                        inclusive = false
                    )
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}
