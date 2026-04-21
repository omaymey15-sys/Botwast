package com.neogame.psp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddGameActivity : AppCompatActivity() {

    private lateinit var btnSelectFile: Button
    private lateinit var btnCancel: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_game)

        btnSelectFile = findViewById(R.id.btn_select_file)
        btnCancel = findViewById(R.id.btn_cancel)
        progressBar = findViewById(R.id.progress_import)
        statusText = findViewById(R.id.text_status)

        btnSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/zip"
            }
            startActivityForResult(intent, 1001)
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri -> importGame(uri) }
        }
    }

    private fun importGame(uri: Uri) {
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                progressBar.visibility = View.VISIBLE
                statusText.text = "⏳ Importing..."

                val tempFile = File(cacheDir, "temp_game.zip")
                contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val gamesDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "ConsolePSP"
                )
                gamesDir.mkdirs()
                
                val destFile = File(gamesDir, "game_${System.currentTimeMillis()}.zip")
                tempFile.copyTo(destFile, overwrite = true)
                tempFile.delete()

                statusText.text = "✅ Game imported!"
                setResult(Activity.RESULT_OK)
                Thread.sleep(1000)
                finish()
            } catch (e: Exception) {
                statusText.text = "❌ Error: ${e.message}"
                progressBar.visibility = View.GONE
            }
        }
    }
}