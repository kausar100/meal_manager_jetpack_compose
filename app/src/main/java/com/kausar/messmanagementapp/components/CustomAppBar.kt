package com.kausar.messmanagementapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kausar.messmanagementapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String? = null,
    canNavigateBack: Boolean = true,
    canGoProfileScreen: Boolean = false,
    profilePicture: String = "",
    userName: String = "",
    userType: String = "",
    gotoProfileScreen: () -> Unit = { },
    showAction: Boolean = false,
    actionIcon: Int? = null,
    onClickAction: (() -> Unit)? = null,
    canLogout: Boolean = true,
    logoutAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(title = {
        title?.let {
            Text(
                title,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }

    }, modifier = modifier, scrollBehavior = scrollBehavior, navigationIcon = {
        if (canNavigateBack) {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = "up_button"
                )
            }
        }
        if (canGoProfileScreen) {
            Row(
                Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        gotoProfileScreen()
                    }, verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(profilePicture)
                        .crossfade(true).build(),
                    placeholder = painterResource(id = R.drawable.ic_person),
                    contentDescription = stringResource(id = R.string.mess_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = userType, fontSize = 12.sp)
                    Text(text = userName, fontWeight = FontWeight.Bold)
                }

            }


        }
    }, actions = {
        if (showAction) {
            actionIcon?.let {
                IconButton(onClick = {
                    if (onClickAction != null) {
                        onClickAction()
                    }
                }, Modifier.padding(end = 16.dp)) {
                    Icon(
                        painter = painterResource(id = actionIcon),
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
                    imageVector = Icons.Default.ExitToApp, contentDescription = "logout"
                )
            }

        }
    }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .9f),
        actionIconContentColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.surface,
        navigationIconContentColor = MaterialTheme.colorScheme.surface
    )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewAppBar() {
    CustomTopAppBar {}
}