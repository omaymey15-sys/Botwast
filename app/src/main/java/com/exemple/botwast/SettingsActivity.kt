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
        // Back Button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Reply Delay Slider
        binding.replyDelaySlider.apply {
            valueFrom = 0f
            valueTo = 5000f
            stepSize = 500f
            setOnChangeListener { _, value, _ ->
                dataManager.setReplyDelay(value.toLong())
                updateDelayLabel(value.toLong())
            }
        }

        // Case Sensitive Toggle
        binding.caseSensitiveSwitch.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setCaseSensitive(isChecked)
        }

        // Random Reply Toggle
        binding.randomReplySwitch.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setRandomReplyEnabled(isChecked)
        }

        // Quiet Mode Toggle
        binding.quietModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            dataManager.setQuietModeEnabled(isChecked)
            binding.quietModeLayout.isEnabled = isChecked
        }

        // Quiet Mode Start Time
        binding.quietStartTimeButton.setOnClickListener {
            showTimePickerDialog { time ->
                dataManager.setQuietModeStart(time)
                binding.quietStartTimeButton.text = time
            }
        }

        // Quiet Mode End Time
        binding.quietEndTimeButton.setOnClickListener {
            showTimePickerDialog { time ->
                dataManager.setQuietModeEnd(time)
                binding.quietEndTimeButton.text = time
            }
        }

        // Export Button
        binding.exportButton.setOnClickListener {
            exportConfiguration()
        }

        // Import Button
        binding.importButton.setOnClickListener {
            importConfiguration()
        }

        // Reset Button
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
        // Simple time picker implementation
        // You can use android.app.TimePickerDialog
        onTimeSelected("12:00")
    }

    private fun exportConfiguration() {
        val json = dataManager.exportConfiguration()
        // Save to file or share
        android.util.Log.d("BotWast", "Export: $json")
    }

    private fun importConfiguration() {
        // Load from file and import
        android.util.Log.d("BotWast", "Import")
    }

    private fun resetSettings() {
        dataManager.resetStatistics()
        loadSettings()
    }
}