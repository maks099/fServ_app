package com.example.fserv.model.app

import androidx.annotation.StringRes
import com.example.fserv.R

enum class StringCoder(
    @StringRes val resource: Int ,
) {
    EmailNotValid(
        resource = R.string.email_is_not_valid
    ),
    ResetLinkIsSend(
        resource = R.string.email_for_reset_is_sended
    ),
    ServerError(
        resource = R.string.server_error
    )
}