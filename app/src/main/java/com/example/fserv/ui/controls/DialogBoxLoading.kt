package com.example.fserv.ui.controls

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.fserv.R
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog



@Composable
fun DialogBoxLoading(
    cornerRadius: Dp= 16.dp,
    paddingStart: Dp = 56.dp,
    paddingEnd: Dp = 56.dp,
    paddingTop: Dp = 32.dp,
    paddingBottom: Dp = 32.dp,
    progressIndicatorColor: Color= colorResource(id=R.color.action_orange),
    progressIndicatorSize: Dp = 80.dp
) {

    Dialog(
        onDismissRequest = {}

    ) {
        Surface(
            modifier =Modifier
                .clip(RoundedCornerShape(dimensionResource(id=R.dimen.corner)))
                .background(Color.Transparent)
        ) {
            Column(
                modifier =Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(dimensionResource(id=R.dimen.corner)))
                    .background(colorResource(id=R.color.action_orange).copy(alpha=1f)),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    ProgressIndicatorLoading(
                        progressIndicatorSize = progressIndicatorSize,
                        progressIndicatorColor = progressIndicatorColor
                    )
                }


                // Gap between progress indicator and text
                Spacer(modifier = Modifier.height(16.dp))

                // Please wait text
                Text(
                    modifier = Modifier
                        .padding(bottom = paddingBottom),
                    color = colorResource(id=R.color.text_light),
                    style = MaterialTheme.typography.body1,
                    text = stringResource(id=R.string.please_wait)
                )
            }
        }
    }
}

@Composable
fun ProgressIndicatorLoading(progressIndicatorSize: Dp, progressIndicatorColor: Color) {

    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
            }
        )
    )

    CircularProgressIndicator(
        progress = 1f,
        modifier =Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                12.dp,
                brush=Brush.sweepGradient(
                    listOf(
                        Color.White,
                        progressIndicatorColor
                    )
                ),
                shape=CircleShape
            ),
        strokeWidth = 1.dp,
        color = Color.White // Set background color
    )
}