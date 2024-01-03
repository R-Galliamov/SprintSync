package com.developers.spryntsync.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.developers.spryntsync.databinding.ActivityMainBinding

const val TAG = "My log"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}