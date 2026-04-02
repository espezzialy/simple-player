package com.espezzialy.simpleplayer.presentation.player.components

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.MultiWindowModeChangedInfo
import androidx.core.util.Consumer

private tailrec fun findComponentActivity(context: Context): ComponentActivity? {
    return when (context) {
        is ComponentActivity -> context
        is ContextWrapper -> findComponentActivity(context.baseContext)
        else -> null
    }
}

@Composable
fun rememberIsInMultiWindowMode(): Boolean {
    val context = LocalContext.current
    val activity = remember(context) { findComponentActivity(context) }
    var inMultiWindow by remember(activity) {
        mutableStateOf(activity?.isInMultiWindowMode == true)
    }
    DisposableEffect(activity) {
        val act = activity ?: return@DisposableEffect onDispose { }
        val listener =
            Consumer<MultiWindowModeChangedInfo> { info ->
                inMultiWindow = info.isInMultiWindowMode
            }
        act.addOnMultiWindowModeChangedListener(listener)
        inMultiWindow = act.isInMultiWindowMode
        onDispose {
            act.removeOnMultiWindowModeChangedListener(listener)
        }
    }
    return inMultiWindow
}
