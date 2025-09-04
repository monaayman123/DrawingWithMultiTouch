package com.example.drawingwithmultitouch.ui.viewModel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

interface DrawingsInteractionListener {
    data class OnDraw(val pointerId: Int, val position: Offset) : DrawingsInteractionListener
    data class OnPathEnd(val pointerId: Int) : DrawingsInteractionListener
    data class OnSelectColor(val color: Color) : DrawingsInteractionListener
    data object OnClearCanvasClick : DrawingsInteractionListener
    data object OnUndoClick : DrawingsInteractionListener
    data object OnCancelClick : DrawingsInteractionListener
    data class OnSaveClick(val width: Int, val height: Int) : DrawingsInteractionListener

}