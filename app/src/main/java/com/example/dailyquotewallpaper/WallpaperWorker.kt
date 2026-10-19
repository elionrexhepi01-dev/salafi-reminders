package com.example.dailyquotewallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.math.max

class WallpaperWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            val assetManager = applicationContext.assets
            val images = assetManager.list("wallpapers") ?: return Result.failure()
            if (images.isEmpty()) return Result.failure()

            val randomImage = images.random()

            val originalBitmap = assetManager.open("wallpapers/$randomImage").use {
                BitmapFactory.decodeStream(it)
            }

            val metrics = applicationContext.resources.displayMetrics
            val screenWidth = metrics.widthPixels
            val screenHeight = metrics.heightPixels

            // 🔑 Proper center-crop scaling
            val scaleX = screenWidth.toFloat() / originalBitmap.width
            val scaleY = screenHeight.toFloat() / originalBitmap.height
            val scale = max(scaleX, scaleY)

            val scaledWidth = (originalBitmap.width * scale).toInt()
            val scaledHeight = (originalBitmap.height * scale).toInt()

            val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(
                originalBitmap,
                scaledWidth,
                scaledHeight,
                true
            )

            val outputBitmap = android.graphics.Bitmap.createBitmap(
                screenWidth,
                screenHeight,
                android.graphics.Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(outputBitmap)

            val left = (screenWidth - scaledWidth) / 2f
            val top = (screenHeight - scaledHeight) / 2f

            canvas.drawBitmap(scaledBitmap, left, top, null)

            val wallpaperManager = WallpaperManager.getInstance(applicationContext)

            wallpaperManager.setBitmap(
                outputBitmap,
                null,
                true,
                WallpaperManager.FLAG_LOCK
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}