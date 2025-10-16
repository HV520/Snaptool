package com.example.lightshotremade

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    companion object { const val REQ_CODE = 1337 }
    private lateinit var mpMgr: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mpMgr = getSystemService(MediaProjectionManager::class.java)
        startActivityForResult(mpMgr.createScreenCaptureIntent(), REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val svc = Intent(this, CaptureService::class.java)
            svc.putExtra("resultCode", resultCode)
            svc.putExtra("resultData", data)
            startForegroundService(svc)
            finish()
        } else finish()
    }
}
