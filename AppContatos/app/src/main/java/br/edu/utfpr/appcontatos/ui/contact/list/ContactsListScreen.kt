package br.edu.utfpr.appcontatos.ui.contact.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.appcontatos.R
import br.edu.utfpr.appcontatos.data.Contact
import br.edu.utfpr.appcontatos.data.ContactDatasource
import br.edu.utfpr.appcontatos.data.generateContacts
import br.edu.utfpr.appcontatos.data.groupByInitial
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme
import br.edu.utfpr.appcontatos.ui.utils.composables.ContactAvatar
import br.edu.utfpr.appcontatos.ui.utils.composables.FavoriteIconButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ContactsListScreen(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    var isInitialComposition: Boolean by rememberSaveable { mutableStateOf(true) }
    var uiState: ContactsListUiState by remember { mutableStateOf(ContactsListUiState()) }

    val loadContacts: () -> Unit = {
        uiState = uiState.copy(
            isLoading = true,
            hasError = false
        )
        coroutineScope.launch {
            delay(2000)
            val contacts: List<Contact> = ContactDatasource.instance.findAll()
            uiState = uiState.copy(
                contacts = contacts.groupByInitial(),
                isLoading = false
            )
        }
    }

    val toggleFavorite: (Contact) -> Unit = { contact ->
        val updatedContact = contact.copy(isFavorite = !contact.isFavorite)
        ContactDatasource.instance.save(updatedContact)
        val contacts: List<Contact> = ContactDatasource.instance.findAll()
        uiState = uiState.copy(contacts = contacts.groupByInitial())
    }

    if (isInitialComposition) {
        loadContacts()
        isInitialComposition = false
    }

    val contentModifier = modifier.fillMaxSize()
    if (uiState.isLoading) {
        LoadingContent(modifier = contentModifier)
    } else if (uiState.hasError) {
        ErrorContent(
            modifier = contentModifier,
            onTryAgainPressed = loadContacts
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                AppBar(
                    onRefreshPressed = loadContacts
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Adicionar"
                    )
                    Spacer(Modifier.size(8.dp))
                    Text("Novo contato")
                }
            }
        ) { paddingValues ->
            val defaultModifier = Modifier.padding(paddingValues)
            if (uiState.contacts.isEmpty()) {
                EmptyList(modifier = defaultModifier)
            } else {
                List(
                    modifier = defaultModifier,
                    contacts = uiState.contacts,
                    onFavoritePressed = toggleFavorite
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    onRefreshPressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(stringResource(R.string.contacts))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
            IconButton(onClick = onRefreshPressed) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.refresh)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AppBarPreview() {
    AppContatosTheme {
        AppBar(onRefreshPressed = {})
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(60.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.loading_contacts),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun LoadingContentPreview() {
    AppContatosTheme {
        LoadingContent()
    }
}

@Composable
private fun ErrorContent(
    modifier: Modifier = Modifier,
    onTryAgainPressed: () -> Unit
 ) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val defaultColor = MaterialTheme.colorScheme.primary
        Icon(
            imageVector = Icons.Filled.CloudOff,
            contentDescription = stringResource(R.string.loading_error),
            tint = defaultColor,
            modifier = Modifier.size(80.dp)
        )
        val textPadding = PaddingValues(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp
        )
        Text(
            modifier = Modifier.padding(textPadding),
            text = stringResource(R.string.loading_error),
            style = MaterialTheme.typography.titleLarge,
            color = defaultColor
        )
        Text(
            modifier = Modifier.padding(textPadding),
            text = stringResource(R.string.wait_and_try_again),
            style = MaterialTheme.typography.titleSmall,
            color = defaultColor
        )
        ElevatedButton(
            onClick = onTryAgainPressed,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.try_again))
        }
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun ErrorContentPreview() {
    AppContatosTheme {
        ErrorContent(
            onTryAgainPressed = {}
        )
    }
}

@Composable
private fun EmptyList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(all = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.no_data),
            contentDescription = stringResource(R.string.no_data)
        )
        Text(
            text = stringResource(R.string.no_data),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.no_data_hint),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyListPreview() {
    AppContatosTheme {
        EmptyList()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun List(
    modifier: Modifier = Modifier,
    contacts: Map<String, List<Contact>>,
    onFavoritePressed: (Contact) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        contacts.forEach { (initial, contacts) ->
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = initial,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .padding(start = 16.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            items(contacts) { contact ->
                ContactListItem(
                    contact = contact,
                    onFavoritePressed = onFavoritePressed
                )
            }
        }
    }
}

@Composable
private fun ContactListItem(
    modifier: Modifier = Modifier,
    contact: Contact,
    onFavoritePressed: (Contact) -> Unit
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(contact.fullName) },
        leadingContent = {
            ContactAvatar(
                firstName = contact.firstName,
                lastName = contact.lastName
            )
        },
        trailingContent = {
            FavoriteIconButton(
                isFavorite = contact.isFavorite,
                onPressed = { onFavoritePressed(contact) }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ListPreview() {
    AppContatosTheme {
        List(
            contacts = generateContacts().groupByInitial(),
            onFavoritePressed = {}
        )
    }
}











