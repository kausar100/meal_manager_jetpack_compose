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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.components.CustomProgressBar
import com.kausar.messmanagementapp.components.CustomTextField
import com.kausar.messmanagementapp.presentation.viewmodels.FirebaseStorageViewModel
import com.kausar.messmanagementapp.presentation.viewmodels.MainViewModel
import com.kausar.messmanagementapp.utils.showToast

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

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(Modifier.fillMaxHeight(.25f), contentAlignment = Alignment.Center) {

                if (isLoading) {
                    CircularProgressIndicator()
                }
                if (selectedImage != null) {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap.value =
                            MediaStore.Images.Media.getBitmap(
                                context.contentResolver,
                                selectedImage
                            )

                    } else {
                        val source =
                            ImageDecoder.createSource(context.contentResolver, selectedImage!!)
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
                } else {
                    if (userData.profilePhoto.isNotEmpty()) {
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
                            contentDescription = stringResource(id = R.string.mess_picture),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )

                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "profile photo",
                            modifier = Modifier
                                .size(100.dp)
                                .border(
                                    1.dp,
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = CircleShape
                                )
                                .background(color = Color.Transparent, shape = CircleShape)
                                .clip(CircleShape)
                        )
                    }
                }
                Row(
                    Modifier
                        .width(100.dp)
                        .fillMaxHeight(.7f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "update profile",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                                CircleShape
                            )
                            .padding(4.dp)
                            .clickable {
                                imagePicker.launch("image/*")
                            }
                    )
                }
            }
            Text(
                text = userData.userName,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
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

        }

        if (profilePic.error.isNotEmpty()) {
            context.showToast(profilePic.error)
        } else if (profilePic.isLoading) {
            CustomProgressBar(msg = "Uploading pic...")
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

    CustomTextField(
        readOnly = !editable,
        input = info,
        onInputChange = onInputChange,
        header = {
            Text(text = title)
        },
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth()
    ) {
        focusManager.clearFocus()
        keyboardController?.hide()
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMealListScreen() {
    ProfileScreen()
}