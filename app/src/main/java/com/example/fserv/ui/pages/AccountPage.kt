package com.example.fserv.ui.pages

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fserv.R
import com.example.fserv.model.app.*
import com.example.fserv.model.server.UserActivity
import com.example.fserv.ui.controls.SimpleTags
import com.example.fserv.utils.findActivity
import com.example.fserv.view_models.ActivitiesListViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.wallet.*
import com.stripe.android.paymentsheet.PaymentSheetContract
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private lateinit var coroutineScope: CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
private lateinit var modalSheetState: ModalBottomSheetState


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountPage(
    activityListState: LazyListState,
    activities: LazyPagingItems<UserActivity>,
    onUserActivityClick: (UserActivity) -> Unit
) {
    val viewModel=ActivitiesListViewModel.get()
    var payPanelIsVisible by rememberSaveable { mutableStateOf(false) }
    coroutineScope=rememberCoroutineScope()
    val scaffoldState: ScaffoldState=rememberScaffoldState()
    modalSheetState=rememberModalBottomSheetState(
        initialValue=ModalBottomSheetValue.Hidden,
        confirmStateChange={ it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded=true,
    )
    val activity=LocalContext.current.findActivity()


    fun showSnackBar(
        title: Int,
        message: String,
    ) {
        if (!viewModel.snackIsShowing) {
            viewModel.snackIsShowing=true
            coroutineScope.launch {
                val snackbarResult=
                    scaffoldState.snackbarHostState.showSnackbar(
                        message="${activity.getText(title)} $message",
                    )
                when (snackbarResult) {
                    SnackbarResult.Dismissed,SnackbarResult.ActionPerformed -> {
                        viewModel.snackIsShowing=false
                    }
                }
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState=modalSheetState,
        sheetShape=RoundedCornerShape(
            topStart=12.dp,
            topEnd=12.dp
        ),

        sheetContent={
            Column(
                modifier =Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                colorResource(id=R.color.action_orange),
                                colorResource(id=R.color.action_dark)
                            )
                        )
                    ),
            ) {
                GooglePayWay(
                    activity=activity,
                    onGooglePayIsReady={ viewModel.googlePayIsReady=true },
                    googlePayIsReady=viewModel.googlePayIsReady,
                    showWallet={ func ->
                        coroutineScope.launch { modalSheetState.hide() }
                        viewModel.startGooglePayAction(
                            activity=activity,
                            onSuccess={ pendingIntent ->
                                func(pendingIntent)
                            },
                            onError={
                                showSnackBar(
                                    R.string.error,
                                    it
                                )
                            }
                        )
                    },
                    onSuccess={ viewModel.updateAccount() }
                )

                StripeWay(
                    onPaymentResult={
                        viewModel.handleStripeResult(
                            it
                        ) {
                            showSnackBar(
                                R.string.error,
                                ""
                            )
                        }
                    },
                    onClick={ func ->
                        coroutineScope.launch { modalSheetState.hide() }
                        viewModel.getStripePaymentSheet(
                            activity=activity,
                            onSuccess={ args ->
                                func(args)
                            },
                            onError={
                                showSnackBar(
                                    R.string.error,
                                    it
                                )
                            }
                        )
                    }
                )

            }
        }
    ) {
        Scaffold(scaffoldState=scaffoldState) { paddingValues ->
            Box(
                modifier=Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                colorResource(id=R.color.action_orange),
                                colorResource(id=R.color.action_dark)
                            )
                        )
                    )
            ) {
                Column(
                    horizontalAlignment=CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(
                        modifier=Modifier
                            .height(16.dp)
                    )
                    Text(
                        text=stringResource(id=R.string.on_your_account),
                        fontSize=28.sp,
                        color = colorResource(id=R.color.text_light),
                    )
                    AutoSizeText(
                        text="${viewModel.account}₴",
                        textStyle=TextStyle(
                            fontSize=64.sp
                        ),

                    )
                    if (payPanelIsVisible) {
                        PayPanel(
                            viewModel.topUpSum,
                            { viewModel.topUpSum=it },
                            { payPanelIsVisible=false })
                    } else {
                        RechargeButton {
                            payPanelIsVisible=true
                        }
                    }
                    Spacer(
                        modifier=Modifier
                            .height(30.dp)
                    )


                        if(activities.itemCount > 0) {
                            val isRefreshing by viewModel.isRefreshing.collectAsState()
                            Text(
                                text = stringResource(id=R.string.your_activities),
                                color = colorResource(id=R.color.text_light),
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                            SwipeRefresh(
                                state=rememberSwipeRefreshState(isRefreshing=isRefreshing),
                                onRefresh={ activities.refresh() },
                                modifier=Modifier
                                    .fillMaxWidth()
                            ) {
                                LazyColumn(
                                    state=activityListState,
                                    modifier=Modifier
                                        .fillMaxWidth()
                                ) {
                                    items(activities) { activity ->
                                        if (activity != null) {
                                            InfoCard(
                                                activity,
                                                onUserActivityClick
                                            )
                                        }

                                    }
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(id=R.string.you_have_not_activities),
                                color = colorResource(id=R.color.text_light),
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                }

            }
        }
    }
}

@Composable
fun StripeWay(
    onPaymentResult: (PaymentSheetResult) -> Unit,
    onClick: ((PaymentSheetContract.Args) -> Unit) -> Unit
) {
    val stripeLauncher=rememberLauncherForActivityResult(
        contract=PaymentSheetContract(),
        onResult={
            onPaymentResult(it)
        }
    )

    PaymentMethodRow(
        drawable=com.stripe.android.R.drawable.stripe_3ds2_ic_mastercard,
        contentDescription=R.string.debit_card
    ) {
        onClick { stripeLauncher.launch(it) }
    }
}

@Composable
fun GooglePayWay(
    activity: Activity,
    onGooglePayIsReady: () -> Unit,
    googlePayIsReady: Boolean,
    showWallet: ((PendingIntent) -> Unit) -> Unit,
    onSuccess: () -> Unit
) {
    possiblyShowGooglePayButton(
        activity=activity
    ) {
        onGooglePayIsReady()
    }
    if (googlePayIsReady) {
        val googlePaymentLauncher=
            rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
                when (result.resultCode) {
                    RESULT_OK ->
                        result.data?.let { intent ->
                            PaymentData.getFromIntent(intent)?.let {
                                onSuccess()
                            }
                        }
                    RESULT_CANCELED -> {}
                }
            }
        PaymentMethodRow(
            drawable=com.stripe.android.R.drawable.stripe_google_pay_mark,
            contentDescription=R.string.google_pay
        ) {
            showWallet {
                googlePaymentLauncher.launch(
                    IntentSenderRequest.Builder(it).build()
                )
            }
        }
    }
}


@Composable
fun PaymentMethodRow(drawable: Int,contentDescription: Int,onClick: () -> Unit) {
    Row(
        verticalAlignment=Alignment.CenterVertically,
        modifier=Modifier
            .padding(horizontal=12.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Box(
            modifier=Modifier
                .width(82.dp)
        ) {
            Icon(
                painter=painterResource(id=drawable),
                contentDescription=stringResource(id=contentDescription),
                tint=Color.Unspecified,
                modifier=
                if (drawable == com.stripe.android.R.drawable.stripe_google_pay_mark)
                    Modifier
                        .size(82.dp)
                        .align(Alignment.Center)
                else
                    Modifier
                        .size(58.dp)
                        .align(Alignment.Center)


            )
        }
        Spacer(
            modifier=Modifier
                .width(5.dp)
        )
        Text(
            text=stringResource(id=contentDescription),
            style = MaterialTheme.typography.body1,
            fontSize = 18.sp,
            color = colorResource(id=R.color.text_light)
        )
    }
}

@Composable
private fun RechargeButton(
    enabled: Boolean=true,
    onClick: () -> Unit,
) {
    Button(
        enabled=enabled,
        onClick=onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id=R.color.action_dark),
            contentColor = colorResource(id=R.color.text_light)
        ),
        modifier =Modifier
            .padding(
                vertical=8.dp,
                horizontal=4.dp
            )

            .fillMaxWidth(0.6f)
            .height(50.dp)

    ) {
        Text(
            text = stringResource(id=R.string.recharge),
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview(showSystemUi=true)
@Composable
fun PayPanelPreview() {
    PayPanel(
        "",
        {},
        {})
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PayPanel(amount: String,onChange: (String) -> Unit,onClose: () -> Unit) {
        Column(
            modifier=Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp),)
                .background(colorResource(id=R.color.text_light).copy(0.925f))
                .blur(
                    radiusX=250.dp,
                    radiusY=500.dp,
                    edgeTreatment=BlurredEdgeTreatment(RoundedCornerShape(dimensionResource(id=R.dimen.corner)))
                )
                .padding(4.dp)
            ,

            horizontalAlignment = CenterHorizontally
        ) {
            IconButton(
                onClick=onClose,
                modifier=Modifier
                    .align(Alignment.End)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription="Close",
                )
            }

                val localFocusManager=LocalFocusManager.current
                OutlinedTextField(
                    value=amount,
                    maxLines=1,
                    keyboardOptions=KeyboardOptions(
                        capitalization=KeyboardCapitalization.None,
                        keyboardType=KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions=KeyboardActions(
                        onNext={ localFocusManager.clearFocus() },
                        onDone={ localFocusManager.clearFocus() }
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = colorResource(id=R.color.action_dark),
                        focusedBorderColor = colorResource(id=R.color.action_dark),
                        focusedLabelColor = colorResource(id=R.color.action_dark),
                        cursorColor = colorResource(id=R.color.action_dark)
                    ),
                    modifier =Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal=8.dp,
                            vertical=4.dp
                        ),
                    onValueChange={
                        if ((it.isEmpty())) {
                            onChange(it)

                        } else {
                            if(it.matches(Regex("^\\d+\$")) && it.toInt() > 0 && it.toInt() <= 10000){
                                    onChange(it)

                            }
                        }


                    },
                    label={ Text(stringResource(id=R.string.choose_amount)) }
                )
                Row(
                    modifier=Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical=4.dp,
                            horizontal=8.dp
                        )

                ) {
                    val amounts=listOf<String>(
                        "100",
                        "250",
                        "400",
                        "750"
                    )
                    amounts.forEach { amount ->
                        SimpleTags(
                            isActive=false,
                            text=amount
                        ) {
                            onChange(amount)
                        }
                    }
                }
                RechargeButton(
                    enabled=amount != ""
                ) {
                    coroutineScope.launch {
                        if (modalSheetState.isVisible)
                            modalSheetState.hide()
                        else
                            modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }

    }


}

@Composable
fun AutoSizeText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier=Modifier
) {
    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style=scaledTextStyle,
        softWrap=false,
        color = colorResource(id=R.color.text_light),
        onTextLayout={ textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle=
                    scaledTextStyle.copy(fontSize=scaledTextStyle.fontSize * 0.9)
            } else {
                readyToDraw=true
            }
        }
    )
}

@Composable
private fun InfoCard(
    activityObj: UserActivity,
    onUserActivityClick: (UserActivity) -> Unit
) {
    Card(
        modifier=Modifier
            .padding(
                horizontal=12.dp,
                vertical=8.dp
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onUserActivityClick(activityObj)
            }

    ) {
        Row(
            modifier =Modifier
                .background(colorResource(id=R.color.text_light).copy(alpha=0.9f))

                .padding(12.dp)
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            val imagePath="https://fserv.onrender.com/photo/" + activityObj.gallery.first()
            Column{
                Text(
                    activityObj.name,
                    style = MaterialTheme.typography.h3,
                    color = colorResource(id=R.color.action_dark),
                )
                val currentLocale=ConfigurationCompat.getLocales(LocalConfiguration.current).get(0)

                Text(
                    "" + currentLocale?.let { activityObj.getParsedDate(it) },
                    style = MaterialTheme.typography.body1,
                    color = colorResource(id=R.color.action_dark),
                )
            }

            AsyncImage(
                model=ImageRequest.Builder(LocalContext.current)
                    .data(imagePath)
                    .error(R.drawable.placeholder)
                    .crossfade(true)
                    .placeholder(R.drawable.placeholder)
                    .build(),

                contentDescription=stringResource(R.string.event_image),
                contentScale=ContentScale.FillWidth,
                modifier=Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

        }
    }

}

