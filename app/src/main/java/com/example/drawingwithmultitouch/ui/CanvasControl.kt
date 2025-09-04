package com.example.drawingwithmultitouch.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach

@Composable
fun ColumnScope.CanvasControls(
    selectedColor: Color,
    colors: List<Color>,
    isCanvasEmpty: Boolean,
    onSelectColor: (Color) -> Unit,
    onClearCanvas: () -> Unit,
    onUndo: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(16.dp),
                color = Color.Gray
            )
            .horizontalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {

        colors.fastForEach { color ->
            val isSelected = selectedColor == color
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val scale = if (isSelected) 1.2f else 1f
                        scaleX = scale
                        scaleY = scale
                    }
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = 2.dp,
                        color = if (selectedColor == color) {
                            Color.Black
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .clickable {
                        onSelectColor(color)
                    }
            )
        }
    }
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = onClearCanvas,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Clear", color = Color.White)
        }
        val colorButton by animateColorAsState(
            targetValue = if (isCanvasEmpty) Color.Gray else Color.Blue,
            label = "colorButton"
        )
        Button(
            onClick = onUndo,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("Undo", color = Color.White)
        }
        Button(
            onClick =onSave,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorButton)
        ) {
            Text("Save", color = Color.White)
        }

    }

}

@Preview
@Composable
private fun CanvasControlPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    }
}