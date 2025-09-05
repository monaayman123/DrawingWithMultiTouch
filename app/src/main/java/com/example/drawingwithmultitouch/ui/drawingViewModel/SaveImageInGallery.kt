package com.example.drawingwithmultitouch.ui.drawingViewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun saveImage(bitmap: Bitmap, context: Context, imageName: String): Uri {
    val imagesDir = File(context.filesDir, "images")
    if (!imagesDir.exists()) imagesDir.mkdir()

    val imageFile = File(imagesDir, "$imageName.png")
    FileOutputStream(imageFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return Uri.fromFile(imageFile)
}