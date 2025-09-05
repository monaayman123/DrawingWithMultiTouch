package com.example.drawingwithmultitouch.ui.drawingViewModel

import android.graphics.Bitmap

sealed interface DrawingsUiEffect {
    data object NavigateToHomeScreen: DrawingsUiEffect
    data class SaveImage(val bitmap: Bitmap) : DrawingsUiEffect
}