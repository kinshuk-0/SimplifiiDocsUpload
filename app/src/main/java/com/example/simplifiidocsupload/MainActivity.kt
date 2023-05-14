package com.example.simplifiidocsupload

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.simplifiidocsupload.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val storageRef = Firebase.storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUploadDocs.setOnClickListener {
            openFileSelection()
        }
    }
    private val fileSelectionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                // Handle the selected file URI
                handleFileSelection(uri)
            }
        }
    }
    private fun openFileSelection() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "*/*"  // Allow all file types
        }

        fileSelectionLauncher.launch(intent)
    }

    private fun handleFileSelection(uri: Uri)  {
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Please Wait")
        mProgressDialog.show()
        // Here, you can upload the selected file to the server
        val fileName = getFileName(uri)
        // Implement your upload logic

        storageRef.child(fileName).putFile(uri).addOnSuccessListener {
            Toast.makeText(this@MainActivity, "Successfully uploaded image",
                Toast.LENGTH_LONG).show()
            storageRef.child(fileName).downloadUrl.addOnSuccessListener {
                mProgressDialog.dismiss()
                Log.d("LOG_LOG", it.toString());
            }
        }

    }

    private fun getFileName(uri: Uri): String {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        }
        return ""
    }
}