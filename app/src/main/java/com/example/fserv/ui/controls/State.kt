package com.example.fserv.ui.controls

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.fserv.R

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingView(
    modifier: Modifier=Modifier
) {
    Column(
        modifier=modifier,
        verticalArrangement=Arrangement.Center,
        horizontalAlignment=Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadingItem() {
    CircularProgressIndicator(
        modifier=Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}


@Preview
@Composable
fun EmptyListItemPreview() {
    EmptyListItem(modifier=Modifier)
}


@Composable
fun EmptyListItem(modifier: Modifier=Modifier) {
    Row(
        modifier=modifier
            .padding(8.dp),
        verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.Center,

        ) {
        Text(
            stringResource(id=R.string.empty_list),
            style = MaterialTheme.typography.body1,
            fontSize=24.sp,
            fontWeight=FontWeight.Bold,
            textAlign=TextAlign.Center,
            color = colorResource(id=R.color.text_light)
        )
    }
}

@Composable
fun ErrorItem(
    message: String,
    modifier: Modifier=Modifier,
    onClickRetry: () -> Unit
) {
    Row(
        modifier=modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement=Arrangement.SpaceAround,
        verticalAlignment=Alignment.CenterVertically
    ) {
        Text(
            text=message,
            maxLines=1,
            style=MaterialTheme.typography.h6,
            color=Color.Red
        )
        OutlinedButton(
            onClick=onClickRetry,
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = colorResource(id=R.color.action_orange)
            )
        ) {
            Text(text=stringResource(id=R.string.try_again))
        }
    }
}