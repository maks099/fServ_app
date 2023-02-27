package com.example.fserv.model.app

enum class DownloadType (
    var message: String = ""
        ){

    PREVIEW(),
    FAIL(),
    SUCCESS()

}