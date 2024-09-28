package br.edu.utfpr.appcontatos.ui.contact.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.appcontatos.R
import br.edu.utfpr.appcontatos.data.ContactTypeEnum
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme
import br.edu.utfpr.appcontatos.ui.utils.composables.DefaultErrorContent
import br.edu.utfpr.appcontatos.ui.utils.composables.DefaultLoadingContent
import br.edu.utfpr.appcontatos.utils.format
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun ContactFormScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactFormViewModel = viewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackPressed: () -> Unit,
    onContactSaved: () -> Unit
) {
    LaunchedEffect(viewModel.uiState.contactSaved) {
        if (viewModel.uiState.contactSaved) {
            onContactSaved()
        }
    }

    val context = LocalContext.current
    LaunchedEffect(snackbarHostState, viewModel.uiState.hasErrorSaving) {
        if (viewModel.uiState.hasErrorSaving) {
            snackbarHostState.showSnackbar(
                context.getString(R.string.error_saving)
            )
        }
    }

    val contentModifier: Modifier = modifier.fillMaxSize()
    if (viewModel.uiState.isLoading) {
        DefaultLoadingContent(modifier = contentModifier)
    } else if (viewModel.uiState.hasErrorLoading) {
        DefaultErrorContent(
            modifier = contentModifier,
            onTryAgainPressed = viewModel::loadContact
        )
    } else {
        Scaffold(
            modifier = contentModifier,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                AppBar(
                    isNewContact = viewModel.uiState.isNewContact,
                    onBackPressed = onBackPressed,
                    isSaving = viewModel.uiState.isSaving,
                    onSavePressed = { /*TODO */ }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    isNewContact: Boolean,
    onBackPressed: () -> Unit,
    isSaving: Boolean,
    onSavePressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            val title = stringResource(
                if (isNewContact) R.string.novo_contato
                else R.string.editar_contato
            )
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.voltar)
                )
            }
        },
        actions = {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = onSavePressed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.salvar)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AppBarPreview() {
    AppContatosTheme {
        AppBar(
            isNewContact = true,
            onBackPressed = {},
            isSaving = false,
            onSavePressed = {}
        )
    }
}

@Composable
private fun FormTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    errorMessageCode: Int = 0,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.Unspecified,
    keyboardImeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val hasError = errorMessageCode > 0
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChanged,
            label = { Text(label) },
            maxLines = 1,
            enabled = enabled,
            readOnly = readOnly,
            isError = hasError,
            keyboardOptions = KeyboardOptions(
                capitalization = keyboardCapitalization,
                imeAction = keyboardImeAction,
                keyboardType = keyboardType
            ),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon
        )
        if (hasError) {
            Text(
                text = stringResource(errorMessageCode),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormTextFieldPreview() {
    AppContatosTheme {
        FormTextField(
            modifier = Modifier.padding(20.dp),
            value = "Teste",
            onValueChanged = {},
            label = "Nome"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormTextFieldErrorPreview() {
    AppContatosTheme {
        FormTextField(
            modifier = Modifier.padding(20.dp),
            value = "Teste",
            onValueChanged = {},
            label = "Nome",
            errorMessageCode = R.string.loading_error
        )
    }
}

@Composable
private fun FormCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    enabled: Boolean = true,
    label: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckChanged,
            enabled = enabled
        )
        Text(label)
    }
}

@Preview(showBackground = true)
@Composable
private fun FormCheckboxPreview() {
    AppContatosTheme {
        FormCheckbox(
            modifier = Modifier.padding(20.dp),
            checked = false,
            onCheckChanged = {},
            label = "Favorito"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormCheckboxCheckedPreview() {
    AppContatosTheme {
        FormCheckbox(
            modifier = Modifier.padding(20.dp),
            checked = true,
            onCheckChanged = {},
            label = "Favorito"
        )
    }
}

@Composable
fun FormRadioButton(
    modifier: Modifier = Modifier,
    value: ContactTypeEnum,
    groupValue: ContactTypeEnum,
    onValueChanged: (ContactTypeEnum) -> Unit,
    enabled: Boolean = true,
    label: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = value == groupValue,
            onClick = { onValueChanged(value) },
            enabled = enabled
        )
        Text(label)
    }
}

@Preview(showBackground = true)
@Composable
private fun FormRadioButtonPreview() {
    AppContatosTheme {
        FormRadioButton(
            modifier = Modifier.padding(20.dp),
            value = ContactTypeEnum.PERSONAL,
            groupValue = ContactTypeEnum.PROFESSIONAL,
            onValueChanged = {},
            label = "Pessoal"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormRadioButtonSelectedPreview() {
    AppContatosTheme {
        FormRadioButton(
            modifier = Modifier.padding(20.dp),
            value = ContactTypeEnum.PERSONAL,
            groupValue = ContactTypeEnum.PERSONAL,
            onValueChanged = {},
            label = "Pessoal"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDatePicker(
    modifier: Modifier = Modifier,
    label: String,
    value: LocalDate,
    onValueChanged: (LocalDate) -> Unit,
    errorMessageCode: Int = 0,
    enabled: Boolean = true
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    FormTextField(
        modifier = modifier,
        value = value.format(),
        onValueChanged = {},
        label = label,
        readOnly = true,
        enabled = enabled,
        errorMessageCode = errorMessageCode,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.selecione_a_data)
                )
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = !showDatePicker },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant
                            .ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onValueChanged(date)
                    }
                }) {
                    Text(stringResource(R.string.ok))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormDatePickerPreview() {
    AppContatosTheme {
        FormDatePicker(
            modifier = Modifier.padding(20.dp),
            label = "Data",
            value = LocalDate.now(),
            onValueChanged = {}
        )
    }
}





