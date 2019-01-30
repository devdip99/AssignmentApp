package com.example.android.assignmentapp

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.android.assignmentapp.DataModel.ItemDataModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_save_item.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class SaveItemActivity : AppCompatActivity() {

    private val REQUEST_EXTERNAL_STORAGE_PERMISSION = 100
    private val PICK_IMAGE_GALLERY = 101

    private var imagePath: String? = null

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_item)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        hideImageLayout()

        upload_image.setOnClickListener { checkGalleryPermission() }

        add_item.setOnClickListener {
            addItemToFirebase()
        }


    }

    private fun addItemToFirebase() {
        if (title_text.text.isNotEmpty() && description_text.text.isNotEmpty() && category_text.text.isNotEmpty() && imagePath != null) {
            displayDialog("Loading...")
            val mStorageRef = FirebaseStorage.getInstance().reference.child(createFileName())
            val file = Uri.fromFile(File(imagePath))
            mStorageRef.putFile(file)
                    .addOnSuccessListener {
                        mStorageRef.downloadUrl.addOnSuccessListener {uri->
                            saveItemToDatabase(uri)
                        }
                    }
                    .addOnCanceledListener {
                        hideDialog()
                        Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
                    }


        } else {
            Toast.makeText(this, "All Fields are mandatory", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveItemToDatabase(downloadUrl: Uri) {
        val item = ItemDataModel(getRandomString(), title_text.text.toString(), description_text.text.toString(), category_text.text.toString(), downloadUrl.toString())
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val myref: DatabaseReference = database.getReference("Items")
        myref.push().setValue(item)
        hideDialog()
        Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
        clearAllFields()

    }

    private fun createFileName(): String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "Image_$timeStamp.jpg"
    }


    private fun clearAllFields() {
        title_text.text.clear()
        description_text.text.clear()
        category_text.text.clear()
        hideImageLayout()
    }

    private fun checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE_PERMISSION)
        } else {
            pickImage()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_GALLERY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data

            if (imageUri != null) {
                setImageLayoutVisible(imageUri)
                imagePath = getPath(imageUri)
            }
        }
    }

    private fun setImageLayoutVisible(imageUri: Uri) {
        image_layout.visibility = View.VISIBLE
        remove_image.visibility = View.VISIBLE

        image_layout.setImageURI(imageUri)
        remove_image.setOnClickListener {
            hideImageLayout()
        }
    }


    private fun hideImageLayout() {
        image_layout.visibility = View.GONE
        remove_image.visibility = View.GONE
        imagePath = null
    }


    private fun getPath(uri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(uri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } catch (e: Exception) {
            Log.e("getpathexception", "getRealPathFromURI Exception : " + e.toString())
            null
        } finally {
            cursor?.close()
        }


    }

    private fun displayDialog(message: String) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, "", message)
        }
    }

    private fun hideDialog() {
        val mDialog = progressDialog
        if (mDialog != null && mDialog.isShowing) {
            progressDialog?.hide()
            progressDialog = null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else {
            false
        }
    }

    private fun getRandomString(): String {
        val letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < 41) {
            val index = (rnd.nextFloat() * letters.length).toInt()
            salt.append(letters[index])
        }
        return salt.toString()

    }
}
