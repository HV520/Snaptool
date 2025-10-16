package com.example.lightshotremade

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class OverlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay)
        val iv = findViewById<ImageView>(R.id.overlayImage)
        val path = intent.getStringExtra("imgPath")
        val bm = BitmapFactory.decodeFile(path)
        iv.setImageBitmap(bm)
    }
}
