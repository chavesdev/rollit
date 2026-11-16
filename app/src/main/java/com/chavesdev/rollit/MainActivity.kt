package com.chavesdev.rollit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chavesdev.rollit.hardware.FeedbackManager
import com.chavesdev.rollit.ui.screens.MainRollScreen
import com.chavesdev.rollit.ui.theme.RollitTheme
import com.chavesdev.rollit.viewmodel.RollViewModel

class MainActivity : ComponentActivity() {

    private var feedbackManager: FeedbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        feedbackManager = FeedbackManager(applicationContext)

        setContent {
            RollitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rollViewModel: RollViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return RollViewModel(feedbackManager = feedbackManager) as T
                            }
                        }
                    )
                    MainRollScreen(viewModel = rollViewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        feedbackManager?.release()
        feedbackManager = null
    }
}
