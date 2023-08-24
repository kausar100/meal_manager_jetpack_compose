package com.kausar.messmanagementapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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

@Composable
fun CustomBasicTextField(
    modifier: Modifier = Modifier,
    input: String,
    onInputChange: (String) -> Unit,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions,
    onComplete: () -> Unit

) {
    BasicTextField(
        value = input,
        onValueChange = onInputChange,
        modifier = modifier
            .padding(vertical = 8.dp)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height + 16f),
                    end = Offset(size.width, size.height + 16f),
                    strokeWidth = 2f
                )
            },
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
        )
    )
}