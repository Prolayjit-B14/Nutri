package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.navigation.NavigationRoot
import com.example.ui.theme.NutriFitTheme
import com.example.ui.viewmodel.NutritionViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NutritionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriFitTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavigationRoot(viewModel = viewModel)
                }
            }
        }
    }
}
