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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomBasicTextField
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseStorageViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel

@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    storageViewModel: FirebaseStorageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val userData = mainViewModel.userInfo.value


    LaunchedEffect(key1 = true) {
        mainViewModel.getUserInfo()
    }

    val profilePic = storageViewModel.profile.value

    val imagePicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),
            onResult = { image ->
                selectedImage = image
                selectedImage?.let {
                    storageViewModel.uploadProfilePic(it)
                }

            })

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(.1f))
        Box(Modifier.fillMaxHeight(.3f), contentAlignment = Alignment.BottomCenter) {
            selectedImage?.let { uri ->
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

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
            } ?: if (userData.profilePhoto.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userData.profilePhoto)
                        .crossfade(true).build(),
                    onLoading = {
                        isLoading = true
                    },
                    onSuccess = {
                        isLoading = false
                    },
                    onError = {
                        isLoading = false
                    },
                    placeholder = painterResource(id = R.drawable.ic_person),
                    contentDescription = stringResource(id = R.string.profile_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "profile photo",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(100.dp)
                        .border(1.dp, color = Color.Gray, shape = CircleShape)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .clip(CircleShape)
                )
            }

            Box(
                Modifier
                    .fillMaxWidth(.30f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {

                IconButton(
                    onClick = { imagePicker.launch("image/*") }, modifier = Modifier
                        .background(
                            Color.DarkGray, CircleShape
                        )
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "update profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }


            }

            if (isLoading) {
                CircularProgressIndicator()
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userData.userName,
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
            info = userData.contactNo,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        UserInfo(
            title = "Mess Name",
            info = userData.messName,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        UserInfo(
            title = "Type",
            info = userData.userType,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(.9f), contentAlignment = Alignment.Center
        ) {
            selectedImage?.let {
                if (profilePic.error.isNotEmpty()) {
                    Text(profilePic.error, textAlign = TextAlign.Center)
                } else if (profilePic.isLoading) {
                    CustomProgressBar(msg = "Uploading pic...")
                }
            }

        }

    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserInfo(
    title: String,
    info: String,
    editable: Boolean = false,
    onInputChange: (String) -> Unit = {},
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
            text = title, textAlign = TextAlign.Center, fontSize = 20.sp, style = TextStyle(
                fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = FontFamily.Cursive
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomBasicTextField(
            readOnly = !editable,
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
    ProfileScreen()
}