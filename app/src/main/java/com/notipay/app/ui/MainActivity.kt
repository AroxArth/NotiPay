package com.notipay.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.notipay.app.ui.home.HomeScreen
import com.notipay.app.ui.theme.NotiPayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiPayTheme {
                HomeScreen()
            }
        }
    }
}
