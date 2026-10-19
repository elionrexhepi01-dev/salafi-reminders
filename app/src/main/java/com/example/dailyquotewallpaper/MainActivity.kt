package com.example.dailyquotewallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

// Color Definitions
val BackgroundColor = Color(0xFFF3EEE7)
val MainTextColor = Color(0xFF332F2B)
val AccentColor = Color(0xFFB47A4E)

// Google Fonts Setup for Manrope
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Manrope")

val ManropeFontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Bold)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReminderScreen(
                onChangeNowClick = {
                    val oneTimeRequest = OneTimeWorkRequestBuilder<WallpaperWorker>().build()
                    WorkManager.getInstance(this@MainActivity).enqueue(oneTimeRequest)
                }
            )
        }

        val wallpaperWorkRequest = PeriodicWorkRequestBuilder<WallpaperWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyWallpaperWork",
            ExistingPeriodicWorkPolicy.KEEP,
            wallpaperWorkRequest
        )
    }
}

@Composable
fun ReminderScreen(onChangeNowClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 28.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = "Salafi Reminders",
            fontFamily = ManropeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = AccentColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main Description with Accent Words
        val subtitleText = buildAnnotatedString {
            append("Authentic sayings, beneficial reminders, and guidance from the ")

            withStyle(style = SpanStyle(color = AccentColor, fontWeight = FontWeight.Bold)) {
                append("Qur’an")
            }
            append(" and ")

            withStyle(style = SpanStyle(color = AccentColor, fontWeight = FontWeight.Bold)) {
                append("Sunnah")
            }
            append(", the Companions, and the scholars upon the methodology of the ")

            withStyle(style = SpanStyle(color = AccentColor, fontWeight = FontWeight.Bold)) {
                append("Salaf")
            }
            append(".")
        }

        Text(
            text = subtitleText,
            fontFamily = ManropeFontFamily,
            fontSize = 16.sp,
            color = MainTextColor,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Wallpaper Instruction Note
        Text(
            text = "Your lock screen wallpaper updates automatically every hour. You can also trigger an update manually below.",
            fontFamily = ManropeFontFamily,
            fontSize = 14.sp,
            color = MainTextColor.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Trigger Button
        Button(
            onClick = onChangeNowClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "Change Wallpaper",
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}