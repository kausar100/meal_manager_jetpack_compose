package com.kausar.messmanagementapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    input: String,
    editable: Boolean = true,
    onInputChange: (String) -> Unit = {},
    placeholder: @Composable() (() -> Unit)? = null,
    prefixIcon: @Composable() (() -> Unit)? = null,
    showTrailingIcon: Boolean = false,
    suffixIcon: ImageVector? = null,
    onClickTrailingIcon: () -> Unit = {},
    label: @Composable() (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions,
    onComplete: () -> Unit

) {
    OutlinedTextField(
        value = input,
        onValueChange = onInputChange,
        enabled = editable,
        placeholder = placeholder,
        leadingIcon = prefixIcon,
        trailingIcon = {
            if (showTrailingIcon) {
                suffixIcon?.let {
                    IconButton(onClick = onClickTrailingIcon) {
                        Icon(imageVector = suffixIcon, contentDescription = "search")

                    }
                }
            }
        },
        singleLine = true,
        maxLines = 1,
        label = label,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = { onComplete() },
            onNext = { onComplete() }
        ),
        modifier = modifier

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    input: String,
    onInputChange: (String) -> Unit,
    header: @Composable() (() -> Unit)?,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions,
    onComplete: () -> Unit

) {
    TextField(
        value = input,
        onValueChange = onInputChange,
        modifier = modifier
            .padding(vertical = 8.dp),
        label = header,
        singleLine = true,
        readOnly = readOnly,
        maxLines = 1,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = {
                onComplete()
            },
            onNext = {
                onComplete()
            }
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.secondary
        )
    )
}