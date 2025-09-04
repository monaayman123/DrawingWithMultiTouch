package com.example.drawingwithmultitouch.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.example.drawingwithmultitouch.ui.viewModel.DrawingsInteractionListener
import com.example.drawingwithmultitouch.ui.viewModel.PathData
import kotlin.math.abs

@Composable
fun DrawingCanvas(
    activePaths: Map<Int, PathData>,
    finishedPaths: List<PathData>,
    onAction: (DrawingsInteractionListener) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .clipToBounds()
            .background(shape = RoundedCornerShape(16.dp), color = Color.Gray)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        event.changes.forEach { change ->
                            val pointerId = change.id.value
                            val position = change.position

                            when {
                                change.pressed -> {
                                    onAction(
                                        DrawingsInteractionListener.OnDraw(
                                            pointerId.toInt(),
                                            position
                                        )
                                    )
                                    change.consume()
                                }

                                change.changedToUp() -> {
                                    onAction(DrawingsInteractionListener.OnPathEnd(pointerId.toInt()))
                                }
                            }
                        }
                    }
                }
            }
    ) {
        finishedPaths.fastForEach { pathData ->
            drawPath(
                path = pathData.path,
                color = pathData.color,
                thickness = pathData.thickness
            )
        }

        activePaths.values.forEach { pathData ->
            drawPath(
                path = pathData.path,
                color = pathData.color,
                thickness = pathData.thickness
            )
        }
    }
}

private fun DrawScope.drawPath(
    path: List<Offset>,
    color: Color,
    thickness: Float = 10f
) {
    val smoothedPath = Path().apply {
        if (path.isNotEmpty()) {
            moveTo(path.first().x, path.first().y)

            val smoothness = 5
            for (i in 1..path.lastIndex) {
                val from = path[i - 1]
                val to = path[i]
                val dx = abs(from.x - to.x)
                val dy = abs(from.y - to.y)
                if (dx >= smoothness || dy >= smoothness) {
                    quadraticTo(
                        x1 = (from.x + to.x) / 2f,
                        y1 = (from.y + to.y) / 2f,
                        x2 = to.x,
                        y2 = to.y
                    )
                }
            }
        }
    }
    drawPath(
        path = smoothedPath,
        color = color,
        style = Stroke(
            width = thickness,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}