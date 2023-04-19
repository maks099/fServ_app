package com.example.fserv.ui.controls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fserv.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddressControl(address: String, latitude: Double, longitude: Double){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =Modifier
            .fillMaxSize()
            .padding(12.dp, bottom = 24.dp)
    ) {
        Text(
            text = address,
            textAlign = TextAlign.Center,
            color = colorResource(id=R.color.text_light)

        )
        val point = LatLng(latitude, longitude)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(point, 15f)
        }
        GoogleMap(
            Modifier
                .height(250.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = point) ,
                snippet = address
            )
        }

    }

}