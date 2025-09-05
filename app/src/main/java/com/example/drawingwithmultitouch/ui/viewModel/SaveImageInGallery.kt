package com.example.drawingwithmultitouch.ui.viewModel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun saveImage(bitmap: Bitmap, context: Context, imageName: String): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        saveImageToGalleryAndroid10Plus(bitmap, context, imageName)
    } else {
        saveImageToGalleryLegacy(bitmap, context, imageName)
    }
}

private fun saveImageToGalleryAndroid10Plus(bitmap: Bitmap, context: Context, imageName: String): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$imageName.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val resolver = context.contentResolver
    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    
    imageUri?.let { uri ->
        resolver.openOutputStream(uri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    }
    
    return imageUri ?: throw Exception("Failed to save image to gallery")
}

private fun saveImageToGalleryLegacy(bitmap: Bitmap, context: Context, imageName: String): Uri {
    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    if (!picturesDir.exists()) picturesDir.mkdirs()

    val imageFile = File(picturesDir, "$imageName.png")
    FileOutputStream(imageFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    
    // Notify media scanner about the new file
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    }
    
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    return uri ?: FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
}