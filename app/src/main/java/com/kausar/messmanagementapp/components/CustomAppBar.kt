package com.kausar.messmanagementapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String? = null,
    canNavigateBack: Boolean = true,
    canShowDrawer: Boolean = false,
    showAction: Boolean = false,
    actionIcon: ImageVector? = null,
    onClickAction: (() -> Unit)? = null,
    canLogout: Boolean = true,
    logoutAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    onClickDrawerMenu: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(title = {
        title?.let {
            Text(
                title,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily.Cursive
                )
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
            } else if (canShowDrawer) {
                onClickDrawerMenu?.let {
                    IconButton(onClick = onClickDrawerMenu) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "drawer"
                        )
                    }
                }

            }
        },
        actions = {
            if (showAction) {
                actionIcon?.let {
                    IconButton(onClick = {
                        if (onClickAction != null) {
                            onClickAction()
                        }
                    }, Modifier.padding(end = 16.dp)) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = "action"
                        )
                    }
                }

            }
            if (canLogout) {
                IconButton(onClick = {
                    if (logoutAction != null) {
                        logoutAction()
                    }
                }, Modifier.padding(end = 16.dp)) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "logout"
                    )
                }

            }
        }
    )
}

@Composable
fun WelcomeText() {
    Text(
        text = "Welcome To Meal Management App",
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewAppBar() {
    CustomTopAppBar {}
}