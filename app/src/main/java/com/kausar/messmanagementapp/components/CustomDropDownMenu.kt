package com.kausar.messmanagementapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropDownMenu(
    title: String = "",
    editable: Boolean = false,
    selectable: Boolean = true,
    items: List<String>,
    selectedItem: String = "",
    onImeAction: (String) -> Unit = {},
    onChange: (String) -> Unit = {},
    onSelect: (String) -> Unit = {}
) {
    var mExpanded by remember {
        mutableStateOf(false)
    }

    val mSelectedText by rememberUpdatedState(newValue = selectedItem)

    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .wrapContentSize(Alignment.TopStart)
    ) {

        OutlinedTextField(
            enabled = editable,
            value = mSelectedText,
            onValueChange = {
                onChange(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (selectable) {
                        mExpanded = !mExpanded
                    }
                }
                .onGloballyPositioned { coordinates ->
                    mTextFieldSize = coordinates.size.toSize()
                },
            label = { Text(title) },
            trailingIcon = {
                if (selectable) {
                    Icon(icon, "type",
                        Modifier.clickable { mExpanded = !mExpanded })
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Black,
                disabledLabelColor = Color.Black,
                disabledTrailingIconColor = Color.Black
            ),
            keyboardActions = KeyboardActions(onNext = { onImeAction(mSelectedText) })
        )

        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
        ) {
            items.forEach { label ->

                DropdownMenuItem(text = { Text(text = label) }, onClick = {
                    mExpanded = false
                    onSelect(label)
                })
            }
        }
    }

}