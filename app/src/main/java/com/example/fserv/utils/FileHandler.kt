package com.example.fserv.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.InputStream

suspend fun saveFile(body: ResponseBody?, pathWhereYouWantToSaveFile: String):String{
    if (body==null)
        return ""
    var input: InputStream? = null
    try {
        input = body.byteStream()
   //     val file = File(getCacheDir(), "cacheFileAppeal.srl")
        val fos = withContext(Dispatchers.IO) {
            FileOutputStream(pathWhereYouWantToSaveFile)
        }
        fos.use { output ->
            val buffer = ByteArray(4 * 1024) // or other buffer size
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            output.flush()
        }
        return pathWhereYouWantToSaveFile
    }catch (e:Exception){
        Log.e("saveFile",e.toString())
    }
    finally {
        input?.close()
    }
    return ""
}