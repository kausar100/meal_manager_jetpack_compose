package com.kausar.messmanagementapp.presentation.profile_screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kausar.messmanagementapp.components.CustomBasicTextField
import com.kausar.messmanagementapp.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit, onNavigateBack: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            selectedImage = it
        })

    Scaffold(topBar = {
        CustomTopAppBar(
            logoutAction = onLogout,
            scrollBehavior = scrollBehavior,
        )
    },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    imagePicker.launch("image/*")
                },
                modifier = Modifier.background(Color.Transparent, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "update profile",
                    tint = Color.Black
                )
            }
        }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            selectedImage?.let { uri ->
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }
                bitmap.value?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = "profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )

                }
            } ?: Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "profile photo",
                tint = Color.Black,
                modifier = Modifier
                    .size(100.dp)
                    .border(1.dp, color = Color.Gray, shape = CircleShape)
                    .background(color = Color.Transparent, shape = CircleShape)
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
    ProfileScreen(onLogout = { /*TODO*/ }) {

    }
}