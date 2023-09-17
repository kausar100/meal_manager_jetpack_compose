package com.kausar.messmanagementapp.presentation.shopping_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.kausar.messmanagementapp.data.model.MemberShoppingList
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ShoppingListInformation(info: String) {
    val configuration = LocalConfiguration.current
    val widthInDp = configuration.screenWidthDp.dp

    val shoppingInformation = Gson().fromJson(info, MemberShoppingList::class.java)
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()


    Box(Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 4.dp)) {
        Card(
            modifier = Modifier.fillMaxSize(),
            elevation = CardDefaults.elevatedCardElevation(), shape = RoundedCornerShape(4.dp)
        ) {
            HorizontalPager(
                count = shoppingInformation.info.size, state = pagerState
            ) {
                SingleShoppingInformation(
                    modifier = Modifier.width(widthInDp), shoppingInformation.info[it]
                )

            }
        }
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .clip(CircleShape)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            if(pagerState.currentPage != 0){
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }, modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Prev")
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            if(pagerState.currentPage != shoppingInformation.info.size-1){
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }, modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Next"
                    )
                }
            }


        }
    }


}