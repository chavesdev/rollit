package com.chavesdev.rollit.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.chavesdev.rollit.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Composable component rendering a Google AdMob Banner Ad at the bottom of the screen.
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { adView ->
                // AdView update if needed
            }
        )
    }
}
