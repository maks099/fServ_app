package com.example.fserv.ui.controls

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fserv.R

@Composable
fun HorizontalNumberPicker(
    modifier: Modifier = Modifier ,
    height: Dp = 45.dp ,
    min: Int = 0 ,
    max: Int = 10 ,
    default: MutableState<Int>,
    onValueChange: (Int) -> Unit = {}
) {
    Row {
        PickerButton(
            size = height,
            drawable = R.drawable.baseline_arrow_back_ios_new_24,
            enabled = default.value > 1,
            onClick = {
                if (default.value > 1) default.value--
                onValueChange(default.value)
            }
        )
        Text(
            text = default.value.toString(),
            fontSize = (height.value / 2).sp,
            modifier = Modifier.padding(10.dp)
                .height(IntrinsicSize.Max)
                .align(Alignment.CenterVertically)
        )
        PickerButton(
            size = height,
            drawable = R.drawable.baseline_arrow_forward_ios_24,
            enabled = default.value < max,
            onClick = {
                if (default.value < max) default.value++
                onValueChange(default.value)
            }
        )
    }
}

@Composable
fun PickerButton(
    size: Dp = 45.dp ,
    @DrawableRes drawable: Int = R.drawable.baseline_arrow_back_ios_new_24 ,
    enabled: Boolean = true ,
    onClick: () -> Unit = {}
) {
    val contentDescription = LocalContext.current.resources.getResourceName(drawable)
    val backgroundColor = if (enabled) MaterialTheme.colors.secondary else Color.LightGray

    Image(
        painter = painterResource(id = drawable),
        contentDescription = contentDescription,
        modifier = Modifier.padding(8.dp).background(backgroundColor, CircleShape)
            .clip(CircleShape)
            .width(size).height(size)
            .clickable (
                enabled = enabled,
                onClick = { onClick() }
            )
    )
}