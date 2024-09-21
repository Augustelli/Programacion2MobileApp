package com.augustomancuso

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PruebaP2_02",
    ) {
        App()
    }
}