package com.kausar.messmanagementapp.utils

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kausar.messmanagementapp.R
import com.kausar.messmanagementapp.data.AppDrawerItemInfo
import com.kausar.messmanagementapp.data.DrawerParams
import com.kausar.messmanagementapp.navigation.Screen

@Composable
fun AppIcon() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "app logo",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(30))
        )
    }

}

@Composable
fun AppDrawerContent(
    menuItems: ArrayList<AppDrawerItemInfo>,
    currentScreenName: String,
    toggleDrawer: () -> Unit,
    onUserPickedOption: (Screen) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .fillMaxWidth(.6f)
            .fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AppIcon()
            Spacer(modifier = Modifier.height(4.dp))
            Divider(modifier = Modifier.background(color = Color.Gray), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))
            repeat(menuItems.size) {
                NavigationOptions(
                    modifier = Modifier.fillMaxWidth(),
                    selectionOptionName = currentScreenName,
                    item = menuItems[it]
                ) {
                    toggleDrawer()
                    onUserPickedOption(menuItems[it].drawerOption)
                }
            }
        }

    }
}

@Composable
fun NavigationOptions(
    modifier: Modifier = Modifier,
    selectionOptionName: String,
    item: AppDrawerItemInfo,
    onOptionSelect: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = if (item.drawerOption.title == selectionOptionName) Color.Gray else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable {
                onOptionSelect()
            }
            .padding(8.dp)

    ) {
        Icon(
            imageVector = item.drawableId,
            contentDescription = stringResource(id = item.descriptionId),
            modifier = Modifier
                .size(24.dp),
            tint = if (item.drawerOption.title == selectionOptionName) Color.White else Color.Black
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = item.title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (item.drawerOption.title == selectionOptionName) Color.White else Color.Black
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 400)
@Composable
fun PreviewAppDrawerContent() {
    AppDrawerContent(
        menuItems = DrawerParams.drawerButtons,
        currentScreenName = "",
        toggleDrawer = { /*TODO*/ },
        onUserPickedOption = {

        }
    )

}