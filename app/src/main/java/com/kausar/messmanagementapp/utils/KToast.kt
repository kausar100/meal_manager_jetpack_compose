package com.kausar.messmanagementapp.utils


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/***
 * 1.create a boolean variable
 *
 * 2.change this value with onclick of composable
 *
 * 3.use this function sending above variable with success or error message
 *
 * 4.ontimeout callback set false to above variable
 *
 * for example
 *
 * 1. var showToast by remember {
 *      mutableStateOf(false)
 *      }
 * 2. Button(onclick = { showToast = true }{
 *          Text(text = "show toast")
 *      }
 * 3. CustomToast(
 *      showToast = showToast,
 *      errorMessage = "message text"
 *      ){
 *          showToast = false
 *      }
 */

@Composable
fun CustomToast(
    showToast: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    onTimeout: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = showToast,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Box(
                modifier = Modifier.background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                when {
                    successMessage != null -> {
                        KToast(
                            icon = Icons.Filled.Done,
                            type = ToastType.success,
                            contentDescription = "success",
                            message = successMessage
                        )
                    }

                    errorMessage != null -> {
                        KToast(
                            icon = Icons.Filled.Warning,
                            type = ToastType.error,
                            contentDescription = "error",
                            message = errorMessage
                        )
                    }
                }

                LaunchedEffect(key1 = showToast) {
                    delay(2000)
                    onTimeout()
                }
            }
        }
    }

}

enum class ToastType {
    error, success
}

@Composable
fun KToast(
    type: ToastType,
    icon: ImageVector,
    contentDescription: String? = null,
    message: String = ""
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                color = if (type == ToastType.error) Color.Red.copy(alpha = .8f) else Color.Blue.copy(
                    alpha = .8f
                ), shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
       Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            color = Color.White,
            fontWeight = FontWeight.W400,
            fontSize = MaterialTheme.typography.titleSmall.fontSize
        )

    }
}