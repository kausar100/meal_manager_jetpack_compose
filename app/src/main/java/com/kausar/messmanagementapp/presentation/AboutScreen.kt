package com.kausar.messmanagementapp.presentation

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun AboutScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "This app helps with meal management which is useful for any mess member.",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This app has two types of user :",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Manager\n" +
                            "2. Member",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
                    textAlign = TextAlign.Justify,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Both types of members have access to following feature",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Mess member can set their daily meal information.\n" +
                            "2. They can update their meal information.\n" +
                            "3. They can see list of meal and shopping information for a month.\n"+
                            "4. They can see their meal info such as number of meal, meal rate, cost of meal and balance.",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
                    textAlign = TextAlign.Justify,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Only Manager has access to these feature",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Add member money to account.\n" +
                            "2. Add new shopping entry.\n" +
                            "3. Can see all other member info\n"+
                            "4. Can assign other member as a manager",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
                    textAlign = TextAlign.Justify
                )

            }

            Spacer(modifier = Modifier.fillMaxHeight(.3f))
            Column(
                Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Developed By",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Md. Golam Kausar",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "Software Engineer",
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "email",
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mail Me",
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_EMAIL, arrayOf("kausar.cse16@gmail.com"))
                            }

                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "phone")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+8801315783246",
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:" + "01315783246")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )
                }

            }

        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "close")
            }
        }
    }


}
