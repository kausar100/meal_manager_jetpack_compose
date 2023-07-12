package com.kausar.messmanagementapp.utils

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String? = null,
    canNavigateBack: Boolean = true,
    showAction: Boolean = false,
    actionIcon: ImageVector? = null,
    onClickAction: (()->Unit)? = null,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(title = {
        title?.let {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                letterSpacing = 2.sp
            )
        }

    },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "up_button"
                    )
                }
            }
        },
        actions = {
            if(showAction){
                actionIcon?.let {
                    IconButton(onClick = onClickAction!!,Modifier.padding(end = 16.dp)) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = "action"
                        )
                    }
                }

            }
        }
    )
}

@Composable
fun WelcomeText(name: String) {
    Text(
        text = "Welcome To Meal Management App\n${name.uppercase()}",
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
    )
}