package com.neogame.console2d

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.neogame.console2d.utils.Logger

class SettingsActivity : AppCompatActivity() {

    private lateinit var volumeSeekBar: SeekBar
    private lateinit var brightnessSeekBar: SeekBar
    private lateinit var backButton: Button
    private lateinit var versionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Logger.d("SettingsActivity created")

        initViews()
        setupListeners()
        loadSettings()
    }

    private fun initViews() {
        volumeSeekBar = findViewById(R.id.volumeSeekBar)
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar)
        backButton = findViewById(R.id.backButton)
        versionTextView = findViewById(R.id.versionTextView)
    }

    private fun setupListeners() {
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Logger.d("Volume changed: $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Logger.d("Brightness changed: $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadSettings() {
        volumeSeekBar.progress = 50
        brightnessSeekBar.progress = 70
        versionTextView.text = getString(R.string.version_info)
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("SettingsActivity destroyed")
    }
}