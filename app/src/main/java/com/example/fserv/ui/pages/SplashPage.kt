package com.example.fserv.ui.pages

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fserv.BuildConfig
import com.example.fserv.R
import com.example.fserv.view_models.SplashViewModel
import kotlinx.coroutines.delay


@Composable
fun SplashPage(navController: NavHostController) {
    val viewModel: SplashViewModel = viewModel()


    Scaffold(
        Modifier.fillMaxSize()

    ) {
        paddingValues ->

        val scale = remember {
            androidx.compose.animation.core.Animatable(1.0f)
        }

        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(1200, easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
            )
            var counter = 0;
            while(!viewModel.isFinish && counter <= 5){ //
                delay(1000)
                counter++
            }

            val nextDestination = if(viewModel.isUserLogged) "main_page" else  "login_page"

            navController.navigate(nextDestination) {
                popUpTo("splash_page") {
                    inclusive = true
                }
            }
        }
        Box(

            modifier =Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colorResource(id=R.color.action_orange),
                            colorResource(id=R.color.action_dark)
                        )
                    )
                )
                .padding(paddingValues),
        ) {
            Column(
                horizontalAlignment=Alignment.CenterHorizontally,
                modifier=Modifier
                    .scale(scale.value)
                    .align(Alignment.Center)
                    .padding(bottom=dimensionResource(id=R.dimen.bottom_padding)),
            ) {
                Image(
                    painter=painterResource(id=R.drawable.fireworks_light),
                    contentDescription=stringResource(R.string.logo),
                    modifier=Modifier.padding(
                        start = dimensionResource(id=R.dimen.logo_padding),
                        end = dimensionResource(id=R.dimen.logo_padding)
                    ),
                )
                Text(
                    text = stringResource(id=R.string.app_name),
                    color = colorResource(id=R.color.text_light),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Text(
                text = "${stringResource(id = R.string.version)} — ${BuildConfig.VERSION_NAME}",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = colorResource(id=R.color.text_light),
                modifier =Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }

    }
}