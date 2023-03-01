package com.example.fserv.ui.pages

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fserv.BuildConfig
import com.example.fserv.R
import com.example.fserv.view_models.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashPage(navController: NavController) {
    val viewModel: SplashViewModel = viewModel()
    viewModel.checkUser()
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {

        val scale = remember {
            androidx.compose.animation.core.Animatable(0.0f)
        }

        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(800, easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
            )
            while(!viewModel.isFinish){
                delay(1000)
            }

            val nextDestination = if(viewModel.isUserLogged) "main_page" else  "login_page"
            navController.navigate(nextDestination) {
                popUpTo("splash_page") {
                    inclusive = true
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "",
            alignment = Alignment.Center, modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .scale(scale.value)
        )

        Text(
            text = "Version - ${BuildConfig.VERSION_NAME}",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}