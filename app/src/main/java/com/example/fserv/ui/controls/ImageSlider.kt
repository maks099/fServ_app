package com.example.fserv.ui.controls

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.fserv.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import java.util.*


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(urlList: List<String>){
    Log.d("IMAGES", urlList.first())
    val state = rememberPagerState()
    val imageUrl =
        remember { mutableStateOf("") }
    HorizontalPager(
        state = state,
        count = urlList.size, modifier = Modifier
            .height(250.dp)
            .fillMaxWidth()
    ) { page ->
        imageUrl.value = "https://fserv.onrender.com/photo/" + urlList[page]

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {

                val painter = rememberImagePainter(data = imageUrl.value, builder = {
                    placeholder(R.drawable.placeholder)
                    scale(Scale.FILL)
                })
                Image(
                    painter = painter, contentDescription = "",
                    Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxSize(), contentScale = ContentScale.Crop
                )
            }

            LaunchedEffect(key1 = state.currentPage) {
                delay(3000)
                var newPosition = state.currentPage + 1
                if (newPosition > urlList.size - 1) newPosition = 0
                // scrolling to the new position.
                state.animateScrollToPage(newPosition)
            }
        }
    }
    DotsIndicator(
        totalDots = urlList.size ,
        selectedIndex = state.currentPage
    )
}


@Composable
private fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), horizontalArrangement = Arrangement.Center
    ) {

        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = Color.DarkGray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = Color.LightGray)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}