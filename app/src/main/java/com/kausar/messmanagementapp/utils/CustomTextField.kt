package com.kausar.messmanagementapp.utils

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    input: String,
    onInputChange: (String) -> Unit,
    placeholder: @Composable() (() -> Unit)?,
    prefixIcon: @Composable() (() -> Unit)? = null,
    label: @Composable() (() -> Unit)?,
    keyboardOptions: KeyboardOptions,
    onComplete: () -> Unit

) {
    OutlinedTextField(
        value = input,
        onValueChange = onInputChange,
        placeholder = placeholder,
        leadingIcon = prefixIcon,
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