package com.kausar.messmanagementapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
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
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldWithIncDec(
    modifier: Modifier = Modifier,
    input: String,
    label: String,
    onValueChange: (String) -> Unit,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions
) {
    val options = listOf("kg", "gm")
    var selectedId by remember {
        mutableIntStateOf(0)
    }
    val change = {
        selectedId = if (selectedId == 0) 1 else 0
    }

    OutlinedTextField(
        value = input,
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        label = { Text(text = label) },
        trailingIcon = if (input.isNotEmpty()) {
            {
                Row(
                    Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = options[selectedId])
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .wrapContentHeight()
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "up",
                            modifier = Modifier.clickable { change() })

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "down",
                            modifier = Modifier.clickable { change() })
                    }
                }
            }

        } else null,
        keyboardActions = keyboardActions
    )
}