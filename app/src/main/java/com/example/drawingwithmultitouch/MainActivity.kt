package com.example.drawingwithmultitouch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drawingwithmultitouch.ui.CanvasControls
import com.example.drawingwithmultitouch.ui.DrawingCanvas
import com.example.drawingwithmultitouch.ui.theme.DrawingWithMultiTouchTheme
import com.example.drawingwithmultitouch.ui.viewModel.DrawingsInteractionListener
import com.example.drawingwithmultitouch.ui.viewModel.DrawingsUiEffect
import com.example.drawingwithmultitouch.ui.viewModel.DrawingsViewModel
import com.example.drawingwithmultitouch.ui.viewModel.allColors
import com.example.drawingwithmultitouch.ui.viewModel.saveImage


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingWithMultiTouchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<DrawingsViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        viewModel.effect.collect { effect ->
                            when (effect) {
                                is DrawingsUiEffect.SaveImage -> {
                                    val uri = saveImage(
                                        bitmap = effect.bitmap,
                                        context = context,
                                        imageName = "drawing"
                                    )
                                    val intent = Intent().apply {
                                        action = Intent.ACTION_VIEW
                                        setDataAndType(uri, "image/*")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                }

                                is DrawingsUiEffect.NavigateToHomeScreen -> {
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(Color.Black),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.cancel),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.clickable(
                                    onClick = {
                                        viewModel.onAction(DrawingsInteractionListener.OnCancelClick)
                                    }
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                fontSize = 24.sp,
                                text = "Drawing",
                                color = Color.White
                            )

                        }
                        DrawingCanvas(
                            activePaths = state.activePaths,
                            finishedPaths = state.finishedPaths,
                            onAction = viewModel::onAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .onGloballyPositioned(
                                    onGloballyPositioned = { coordinates ->
                                        canvasSize = coordinates.size
                                    }
                                )
                        )
                        CanvasControls(
                            modifier = Modifier
                                .padding(top = 12.dp),
                            selectedColor = state.selectedColor,
                            colors = allColors,
                            onSelectColor = {
                                viewModel.onAction(DrawingsInteractionListener.OnSelectColor(it))
                            },
                            onClearCanvas = {
                                viewModel.onAction(DrawingsInteractionListener.OnClearCanvasClick)
                            },
                            onUndo = {
                                viewModel.onAction(DrawingsInteractionListener.OnUndoClick)
                            },
                            onSave = {
                                viewModel.onAction(
                                    DrawingsInteractionListener.OnSaveClick(
                                        canvasSize.width,
                                        canvasSize.height
                                    )
                                )
                            },
                            isCanvasEmpty = state.activePaths.isEmpty() && state.finishedPaths.isEmpty()
                        )
                    }
                }
            }
        }
    }
}