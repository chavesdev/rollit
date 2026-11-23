package com.chavesdev.rollit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class MainActivity : ComponentActivity() {

    private val openAppUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if(result.resultCode != RESULT_OK) {
                Log.e("Error on update app:", "${result.resultCode}")
            }
        }

    private var feedbackManager: FeedbackManager? = null

    private var appUpdateManager: AppUpdateManager? = null
    private lateinit var appUpdateInfo: AppUpdateInfo
    private var rollViewModel: RollViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        com.google.android.gms.ads.MobileAds.initialize(this)

        feedbackManager = FeedbackManager(applicationContext)

        setContent {
            RollitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: RollViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return RollViewModel(feedbackManager = feedbackManager) as T
                            }
                        }
                    )
                    rollViewModel = viewModel
                    MainRollScreen(
                        viewModel = viewModel,
                        onUpdateClick = { updateApp() }
                    )
                }
            }
        }

        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        feedbackManager?.release()
        feedbackManager = null
    }

    private fun checkUpdates() {
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask?.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateInfo = info
                rollViewModel?.setUpdateAvailable(true)
            }
        }
    }

    private fun updateApp() {
        if (::appUpdateInfo.isInitialized) {
            appUpdateManager?.startUpdateFlowForResult(
                appUpdateInfo,
                openAppUpdateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            )
        }
    }
}
