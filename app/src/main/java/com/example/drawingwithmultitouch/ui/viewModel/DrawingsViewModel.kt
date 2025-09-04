package com.example.drawingwithmultitouch.ui.viewModel

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DrawingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(DrawingsUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DrawingsUiEffect>()
    val effect = _effect.asSharedFlow()

    fun onAction(action: DrawingsInteractionListener) {
        when (action) {
            is DrawingsInteractionListener.OnDraw -> onDraw(action.pointerId, action.position)
            is DrawingsInteractionListener.OnPathEnd -> onPathEnd(action.pointerId)
            is DrawingsInteractionListener.OnSelectColor -> onSelectColor(action.color)
            is DrawingsInteractionListener.OnClearCanvasClick -> onClearCanvasClick()
            is DrawingsInteractionListener.OnUndoClick -> onUndoClick()
            is DrawingsInteractionListener.OnCancelClick -> onCancelClick()
            is DrawingsInteractionListener.OnSaveClick -> onSaveClick(action.width, action.height)
        }
    }

    private fun onSaveClick(width: Int, height: Int) {
        val captureResult = captureCanvas(width, height)
        _effect.tryEmit(DrawingsUiEffect.SaveImage(captureResult))
    }

    private fun captureCanvas(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.White.toArgb())

        _state.value.finishedPaths.forEach { pathData ->
            drawPathOnCanvas(canvas, pathData)
        }

        _state.value.activePaths.values.forEach { pathData ->
            drawPathOnCanvas(canvas, pathData)
        }
        return bitmap
    }


    private fun drawPathOnCanvas(canvas: Canvas, pathData: PathData) {
        val paint = pathData.color.toPaint()
        if (pathData.path.size == 1) {
            val p = pathData.path.first()
            canvas.drawCircle(p.x, p.y, paint.strokeWidth / 2, paint)
        } else {
            pathData.path.zipWithNext { p1, p2 ->
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
            }
        }
    }

    private fun Color.toPaint(): Paint {
        return Paint().apply {
            color = this@toPaint.toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 8f
            isAntiAlias = true
        }
    }

    private fun onUndoClick() {
        val finished = _state.value.finishedPaths
        if (finished.isNotEmpty()) {
            _state.update { it.copy(finishedPaths = finished.dropLast(1)) }
        }
    }

    private fun onCancelClick() {
        _effect.tryEmit(DrawingsUiEffect.NavigateToHomeScreen)
    }

    private fun onSelectColor(color: Color) {
        _state.update { it.copy(selectedColor = color) }
    }

    private fun onDraw(pointerId: Int, offset: Offset) {
        val currentPaths = _state.value.activePaths.toMutableMap()
        val existing = currentPaths[pointerId]

        if (existing == null) {
            currentPaths[pointerId] = PathData(
                id = pointerId,
                color = _state.value.selectedColor,
                path = listOf(offset)
            )
        } else {
            currentPaths[pointerId] = existing.copy(path = existing.path + offset)
        }

        _state.update { it.copy(activePaths = currentPaths) }
    }

    private fun onPathEnd(pointerId: Int) {
        val currentPaths = _state.value.activePaths.toMutableMap()
        val finished = currentPaths.remove(pointerId)
        if (finished != null) {
            _state.update {
                it.copy(
                    activePaths = currentPaths,
                    finishedPaths = it.finishedPaths + finished
                )
            }
        } else {
            _state.update { it.copy(activePaths = currentPaths) }
        }
    }

    private fun onClearCanvasClick() {
        _state.update {
            it.copy(activePaths = emptyMap(), finishedPaths = emptyList())
        }
    }
}
