package com.neogame.psp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.neogame.psp.storage.GameManager
import com.neogame.psp.utils.Constants
import com.neogame.psp.utils.FileUtils
import com.neogame.psp.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * AddGameActivity - Ajouter un jeu
 * Permet d'importer des fichiers ZIP de jeux
 */
class AddGameActivity : AppCompatActivity() {

    private lateinit var btnSelectFile: Button
    private lateinit var btnCancel: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_game)

        gameManager = GameManager(this)
        initViews()

        Logger.i("AddGameActivity créée")
    }

    private fun initViews() {
        btnSelectFile = findViewById(R.id.btn_select_file)
        btnCancel = findViewById(R.id.btn_cancel)
        progressBar = findViewById(R.id.progress_import)
        statusText = findViewById(R.id.text_status)

        btnSelectFile.apply {
            text = "📂 Sélectionner ZIP"
            background = ContextCompat.getDrawable(this@AddGameActivity, R.drawable.button_x)
            setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = Constants.MIME_ZIP
                }
                startActivityForResult(intent, 1001)
            }
        }

        btnCancel.apply {
            text = "Annuler"
            background = ContextCompat.getDrawable(this@AddGameActivity, R.drawable.button_b)
            setOnClickListener {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                importGame(uri)
            }
        }
    }

    private fun importGame(uri: Uri) {
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                progressBar.visibility = View.VISIBLE
                statusText.text = "⏳ Importation en cours..."

                val tempFile = File(cacheDir, "temp_game.zip")
                copyUriToFile(uri, tempFile)

                if (!gameManager.isValidGameZip(tempFile.absolutePath)) {
                    withContext(Dispatchers.Main) {
                        statusText.text = "❌ Fichier de jeu invalide"
                        progressBar.visibility = View.GONE
                    }
                    return@launch
                }

                val gameName = tempFile.nameWithoutExtension
                val destFile = File(FileUtils.getGamesDirectory(this@AddGameActivity), "$gameName.zip")

                if (destFile.exists()) destFile.delete()
                tempFile.copyTo(destFile, overwrite = true)
                tempFile.delete()

                withContext(Dispatchers.Main) {
                    statusText.text = "✅ Jeu importé avec succès!"
                    progressBar.visibility = View.GONE
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                Logger.i("Jeu importé: $gameName")
            } catch (e: Exception) {
                Logger.e("Erreur import: ${e.message}")
                withContext(Dispatchers.Main) {
                    statusText.text = "❌ Erreur: ${e.message}"
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun copyUriToFile(uri: Uri, destFile: File) {
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
    }
}