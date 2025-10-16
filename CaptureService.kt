package com.example.lightshotremade

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjectionManager
import android.os.*
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class CaptureService : Service() {
    private var mp: android.media.projection.MediaProjection? = null
    private lateinit var reader: ImageReader

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED) ?: Activity.RESULT_CANCELED
        val data = intent?.getParcelableExtra<Intent>("resultData")
        val mpMgr = getSystemService(MediaProjectionManager::class.java)
        mp = mpMgr.getMediaProjection(resultCode, data!!)

        val metrics = resources.displayMetrics
        reader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, android.graphics.PixelFormat.RGBA_8888, 2)
        mp?.createVirtualDisplay("cap", metrics.widthPixels, metrics.heightPixels, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY, reader.surface, null, null)

        reader.setOnImageAvailableListener({ r ->
            val image = r.acquireLatestImage() ?: return@setOnImageAvailableListener
            val plane = image.planes[0]
            val buffer: ByteBuffer = plane.buffer
            val bmp = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(buffer)
            val temp = File(cacheDir, "cap.png")
            FileOutputStream(temp).use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
            val b = Intent(this, OverlayActivity::class.java)
            b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            b.putExtra("imgPath", temp.absolutePath)
            startActivity(b)
            image.close()
            stopSelf()
        }, Handler(Looper.getMainLooper()))

        startForeground(1, NotificationCompat.Builder(this, "screencap")
            .setContentTitle("LightshotRemade").setSmallIcon(android.R.drawable.ic_menu_camera).build())
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?) = null
}
