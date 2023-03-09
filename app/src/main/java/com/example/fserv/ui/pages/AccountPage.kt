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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fserv.R
import com.example.fserv.model.app.*
import com.example.fserv.model.server.UserActivityObj
import com.example.fserv.ui.controls.SimpleTags
import com.example.fserv.utils.findActivity
import com.example.fserv.view_models.ActivitiesListViewModel
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.wallet.*
import com.stripe.android.PaymentConfiguration
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.paymentsheet.PaymentSheet
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
    activities: LazyPagingItems<UserActivityObj>,
    onUserActivityClick: (UserActivityObj) -> Unit
){
    val viewModel = ActivitiesListViewModel.get()
    var payPanelIsVisible by rememberSaveable { mutableStateOf(false) }
    coroutineScope=rememberCoroutineScope()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    modalSheetState=rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )
    val activity = LocalContext.current.findActivity()



    fun showSnackBar(
        title: Int,
        message: String,
    ) {
        if(!viewModel.snackIsShowing){
            viewModel.snackIsShowing = true
            coroutineScope.launch {
                val snackbarResult=
                    scaffoldState.snackbarHostState.showSnackbar(
                        message="${activity.getText(title)} $message",
                    )
                when (snackbarResult) {
                    SnackbarResult.Dismissed, SnackbarResult.ActionPerformed -> {
                        viewModel.snackIsShowing = false
                    }
                }
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            Column(
                //...
            ) {
                GooglePayWay(
                    activity = activity,
                    onGooglePayIsReady = {viewModel.googlePayIsReady = true},
                    googlePayIsReady = viewModel.googlePayIsReady,
                    showWallet = {
                        func ->
                        coroutineScope.launch { modalSheetState.hide() }
                        viewModel.startGooglePayAction(
                            activity = activity,
                            onSuccess = {
                                pendingIntent ->
                                func(pendingIntent)
                            },
                            onError = {
                                showSnackBar(R.string.error, it)
                            }
                        )
                    },
                    onSuccess ={ viewModel.updateAccount() }
                )

                StripeWay(
                    onPaymentResult = { viewModel.handleStripeResult(it
                    ) {
                        showSnackBar(
                            R.string.error,
                            ""
                        )
                    }
                    },
                    onClick = {
                        func->
                        coroutineScope.launch { modalSheetState.hide() }
                        viewModel.getStripePaymentSheet(
                            activity = activity,
                            onSuccess = {
                                args->
                                func(args)
                            },
                            onError = {
                                showSnackBar(R.string.error, it)
                            }
                        )
                    }
                )

            }
        }
    ) {
        Scaffold(scaffoldState = scaffoldState) { paddingValues ->
            Column(
                horizontalAlignment = CenterHorizontally,
                modifier =Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                Column(
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier =Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.on_your_account),
                        fontSize = 32.sp
                    )
                    AutoSizeText(
                        text ="${viewModel.account}₴",
                        textStyle = TextStyle(
                            fontSize = 68.sp
                        )
                    )
                    if (payPanelIsVisible){
                        PayPanel(
                            viewModel.topUpSum,
                            { viewModel.topUpSum = it },
                            {payPanelIsVisible = false})
                    } else{
                        RechargeButton {
                            payPanelIsVisible = true

                        }
                    }

                }
                Spacer(modifier = Modifier
                    .height(30.dp))

                val isRefreshing by viewModel.isRefreshing.collectAsState()
                SwipeRefresh(
                    state =  rememberSwipeRefreshState(isRefreshing = isRefreshing),
                    onRefresh = { activities.refresh() },
                    modifier =Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    LazyColumn(
                        state = activityListState,
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        items(activities){
                                activity ->
                            if(activity != null){
                                InfoCard(activity, onUserActivityClick)
                            }

                        }
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
    val stripeLauncher = rememberLauncherForActivityResult(
        contract = PaymentSheetContract(),
        onResult = {
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
        activity = activity
    ){
        onGooglePayIsReady()
    }
    if(googlePayIsReady){
        val googlePaymentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK ->
                    result.data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let{
                           onSuccess()
                        }
                    }
                RESULT_CANCELED -> {  }
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
fun PaymentMethodRow(drawable: Int, contentDescription: Int, onClick: () -> Unit){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =Modifier
            .padding(horizontal=7.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .width(82.dp)
        ){
            Icon(
                painter=painterResource(id=drawable),
                contentDescription = stringResource(id=contentDescription),
                tint= Color.Unspecified,
                modifier =
                if(drawable == com.stripe.android.R.drawable.stripe_google_pay_mark)
                    Modifier
                        .size(82.dp)
                        .align(Alignment.Center)
                else
                    Modifier
                        .size(58.dp)
                        .align(Alignment.Center)


            )
        }
        Spacer(modifier=Modifier
            .width(5.dp))
        Text(
            text=stringResource(id=contentDescription),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun RechargeButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
){
    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
            .width(200.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        )
    ){
        Text(stringResource(id = R.string.recharge))
    }
}

@Preview(showSystemUi = true)
@Composable
fun PayPanelPreview(){
    PayPanel("", {}, {})
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PayPanel(amount: String, onChange: (String) -> Unit, onClose: () -> Unit) {
    Card(
        modifier =Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Column(
            modifier =Modifier
                .fillMaxWidth()
            //     .padding(horizontal = 10.dp)
        ) {
            IconButton(
                onClick= onClose,
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "",
                )
            }
            Column(
                modifier =Modifier
                    .fillMaxWidth()
                    .padding(
                        start=15.dp,
                        top=0.dp,
                        end=15.dp,
                        bottom=15.dp
                    )
            ) {
                val localFocusManager = LocalFocusManager.current
                OutlinedTextField(
                    value = amount,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { localFocusManager.moveFocus(FocusDirection.Down) },
                        onDone = { localFocusManager.clearFocus() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    onValueChange = { onChange(it) },
                    label = { Text(stringResource(id = R.string.choose_amount)) }
                )
                Row(
                    modifier =Modifier
                        .fillMaxWidth()
                        .padding(vertical=10.dp)

                ) {
                    val amounts = listOf<String>("100", "250", "400", "750")
                    amounts.forEach {
                            amount ->
                        SimpleTags(
                            isActive=false,
                            text=amount
                        ) {
                            onChange(amount)
                        }
                    }
                }
                RechargeButton(
                    modifier = Modifier.align(CenterHorizontally),
                    enabled = amount != ""
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
    }


}

@Composable
fun AutoSizeText(
    text: String ,
    textStyle: TextStyle ,
    modifier: Modifier = Modifier
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
        style = scaledTextStyle,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle =
                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
private fun InfoCard(
    activityObj: UserActivityObj,
    onUserActivityClick: (UserActivityObj) -> Unit) {
    Log.d("AccountPage", "${activityObj.gallery} ${activityObj._id}$" )
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp) ,
        modifier =Modifier
            .padding(
                horizontal=16.dp,
                vertical=8.dp
            )
            .fillMaxWidth()
            .clickable {
                onUserActivityClick(activityObj)
            },
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 10.dp)
        ){
            val imagePath = "https://fserv.onrender.com/photo/" + activityObj.gallery.first()
            Log.d("TICKET", imagePath)
            Text(
                activityObj.name,
                modifier = Modifier
                    .weight(1f)
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imagePath)
                    .error(R.drawable.placeholder)
                    .crossfade(true)
                    .placeholder(R.drawable.placeholder)
                    .build(),

                contentDescription = stringResource(R.string.event_image),
                contentScale = ContentScale.Crop,
                modifier =Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

        }
    }

}

