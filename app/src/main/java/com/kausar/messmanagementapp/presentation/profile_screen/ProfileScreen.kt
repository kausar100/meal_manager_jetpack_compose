package com.kausar.messmanagementapp.presentation.profile_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomBasicTextField
import com.kausar.messmanagementapp.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onSubmit: () -> Unit, onNavigateBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(topBar = {
        CustomTopAppBar(
            title = "Profile",
            canNavigateBack = true,
            scrollBehavior = scrollBehavior,
            navigateUp = onNavigateBack
        )
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Md.Golam Kausar",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily.Cursive
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            UserInfo(
                title = "Contact Number",
                info = "01315783246",
                onInputChange = {

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            UserInfo(
                title = "Member type",
                info = "Manager",
                onInputChange = {

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )


        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserInfo(
    title: String,
    info: String,
    onInputChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = FontFamily.Cursive
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomBasicTextField(
            readOnly = true,
            input = info,
            onInputChange = onInputChange,
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth()
        ) {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMealListScreen() {
    ProfileScreen(onSubmit = { /*TODO*/ }) {

    }
}