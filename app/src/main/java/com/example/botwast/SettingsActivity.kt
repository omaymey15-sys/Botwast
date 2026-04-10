package com.example.botwast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.botwast.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = DataManager(this)

        setupUI()
        loadSettings()
    }

    private fun setupUI() {

        // BACK
        binding.backButton.setOnClickListener {
            finish()
        }

        // SLIDER (FIX IMPORTANT)
        binding.replyDelaySlider.apply {
            valueFrom = 0f
            valueTo = 5000f
            stepSize = 500f

            addOnChangeListener { _, value, _ ->
                dataManager.setReplyDelay(value.toLong())
                updateDelayLabel(value.toLong())
            }
        }

        // CASE SENSITIVE
        binding.caseSensitiveSwitch.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setCaseSensitive(isChecked)
        }

        // RANDOM REPLY
        binding.randomReplySwitch.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setRandomReplyEnabled(isChecked)
        }

        // QUIET MODE
        binding.quietModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setQuietModeEnabled(isChecked)
            binding.quietModeLayout.isEnabled = isChecked
        }

        // START TIME
        binding.quietStartTimeButton.setOnClickListener {
            showTimePickerDialog { time ->
                dataManager.setQuietModeStart(time)
                binding.quietStartTimeButton.text = time
            }
        }

        // END TIME
        binding.quietEndTimeButton.setOnClickListener {
            showTimePickerDialog { time ->
                dataManager.setQuietModeEnd(time)
                binding.quietEndTimeButton.text = time
            }
        }

        // EXPORT
        binding.exportButton.setOnClickListener {
            exportConfiguration()
        }

        // IMPORT
        binding.importButton.setOnClickListener {
            importConfiguration()
        }

        // RESET
        binding.resetButton.setOnClickListener {
            resetSettings()
        }
    }

    private fun loadSettings() {
        binding.apply {

            replyDelaySlider.value = dataManager.getReplyDelay().toFloat()
            updateDelayLabel(dataManager.getReplyDelay())

            caseSensitiveSwitch.isChecked = dataManager.isCaseSensitive()
            randomReplySwitch.isChecked = dataManager.isRandomReplyEnabled()
            quietModeSwitch.isChecked = dataManager.isQuietModeEnabled()

            quietStartTimeButton.text = dataManager.getQuietModeStart()
            quietEndTimeButton.text = dataManager.getQuietModeEnd()
        }
    }

    private fun updateDelayLabel(delay: Long) {
        binding.replyDelayLabel.text = "Délai réponse: ${delay}ms"
    }

    private fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {
        // TEMP SIMPLE VERSION
        onTimeSelected("12:00")
    }

    private fun exportConfiguration() {
        val json = dataManager.exportConfiguration()
        android.util.Log.d("BotWast", "Export: $json")
    }

    private fun importConfiguration() {
        android.util.Log.d("BotWast", "Import")
    }

    private fun resetSettings() {
        dataManager.resetStatistics()
        loadSettings()
    }
}