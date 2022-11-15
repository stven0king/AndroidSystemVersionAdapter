package com.tzx.androidq

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.documentfile.provider.DocumentFile
import com.tzx.androidq.databinding.AndroidqActivityStorageAccessFrameworkBinding
import java.io.*

/**
 * Created by Tanzhenxing
 * Date: 2022/10/11 16:56
 * Description:
 */
class StorageAccessFrameworkActivity : AppCompatActivity() {
    private val TAG = "StorageAccessFramework"
    private lateinit var binding: AndroidqActivityStorageAccessFrameworkBinding
    private lateinit var safSelectSingleFileActivityResult: ActivityResultLauncher<Intent>
    private lateinit var createFileActivityResult: ActivityResultLauncher<Intent>
    private lateinit var editFileActivityResult: ActivityResultLauncher<Intent>
    private lateinit var selectDirActivityResult: ActivityResultLauncher<Intent>
    private var queryUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AndroidqActivityStorageAccessFrameworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectSingleFile()
        createFile("text/plain", "aaa.txt")
        deleteFile()
        renameFileName()
        editDocument()
        getDocumentTree()
    }


    /**
     * 选择一个文件，这里打开一个图片作为演示
     */
    private fun selectSingleFile() {
        safSelectSingleFileActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                //获取文档
                val uri = it?.data?.data
                if (uri != null) {
                    queryUri = uri
                    binding.createFileUriTv.text = queryUri.toString()
                    dumpImageMetaData(uri)
                    getBitmapFromUri(uri)?.let {
                        binding.showIv.setImageBitmap(it)
                    }
                    Log.d(TAG, "图片的line :$uri")
                }
            }
        }
        binding.safSelectSingleFile.setOnClickListener {
            safSelectSingleFileActivityResult.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                addCategory(Intent.CATEGORY_OPENABLE)
                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            })
        }
    }

    private fun createFile(mimeType: String, fileName: String) {
        createFileActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                //创建文档
                val uri = it?.data?.data
                if (uri != null) {
                    Log.d(TAG, "创建文件成功")
                    binding.createFileUriTv.text = uri.toString()
                    binding.createFileUriTv.visibility = View.VISIBLE
                    dumpImageMetaData(uri)
                }
            }
        }
        binding.createFileBtn.setOnClickListener {
            createFileActivityResult.launch(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                // Filter to only show results that can be "opened", such as
                // a file (as opposed to a list of contacts or timezones).
                addCategory(Intent.CATEGORY_OPENABLE)
                // Create a file with the requested MIME type.
                type = mimeType
                putExtra(Intent.EXTRA_TITLE, fileName)
            })
        }
    }

    /**
     * 如果您获得了文档的 URI，并且文档的 Document.COLUMN_FLAGS 包含 FLAG_SUPPORTS_DELETE，则便可删除该文档
     */
    private fun deleteFile() {
        binding.deleteFileBtn.setOnClickListener {
            queryUri?.let { url ->
                if (checkUriFlag(url, DocumentsContract.Document.FLAG_SUPPORTS_DELETE)) {
                    val deleted = DocumentsContract.deleteDocument(contentResolver, url)
                    val s = "删除$url$deleted"
                    Log.d(TAG, "deleteFile:$s")
                    if (deleted) {
                        binding.createFileUriTv.text = ""
                    }
                } else {
                    Log.d(TAG, "deleteFile" + "权限校验失败")
                }
            }
        }
    }

    private fun renameFileName() {
        binding.renameFileBtn.setOnClickListener {
            queryUri?.let {
                val uri = it
                //小米8 Android9 抛出java.lang.UnsupportedOperationException: Rename not supported异常
                //Pixel 6a Android13可以正常重命名
                if (checkUriFlag(uri, DocumentsContract.Document.FLAG_SUPPORTS_RENAME)) {
                    try {
                        //如果文件名已存在，会报错java.lang.IllegalStateException: File already exists:
                        DocumentsContract.renameDocument(contentResolver, uri, "slzs.txt")
                        Log.d(TAG, "renameFileName" + "重命名成功")
                    } catch (e: FileNotFoundException) {
                        Log.d(TAG, "renameFileName" + "重命名失败，文件不存在")
                    }
                } else {
                    Log.d(TAG, "renameFileName" + "重命名失败，权限校验失败")
                    DocumentFile.fromSingleUri(this@StorageAccessFrameworkActivity, uri)?.renameTo("slzs.txt")
                }
            }
        }
    }

    private fun editDocument() {
        editFileActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    //编辑文档
                    val uri = it?.data?.data
                    queryUri = uri
                    if (uri != null) {
                        alterDocument(uri)//更新文档
                    }
                }
            }
        binding.editDocumentBtn.setOnClickListener {
            editFileActivityResult.launch(
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
                // file browser.
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    // Filter to only show results that can be "opened", such as a
                    // file (as opposed to a list of contacts or timezones).
                    addCategory(Intent.CATEGORY_OPENABLE)
                    // Filter to show only text files.
                    type = "text/plain"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                })
        }
    }

    /**
     * 使用saf选择目录
     */
    private fun getDocumentTree() {
        selectDirActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //选择目录
            val treeUri = it?.data?.data
            if (treeUri != null) {
                savePersistablePermission(treeUri)
                val root = DocumentFile.fromTreeUri(this, treeUri)
                root?.listFiles()?.forEach {
                    Log.d(TAG, "目录下文件名称：${it.name}")
                }
            }
        }
        binding.getDocumentTreeBtn.setOnClickListener {
            val sp = getSharedPreferences("DirPermission", Context.MODE_PRIVATE)
            val uriString = sp.getString("uri", "")
            if (!uriString.isNullOrEmpty()) {
                try {
                    val treeUri = Uri.parse(uriString)
                    // Check for the freshest data.
                    contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    Log.d(TAG, "已经获得永久访问权限")
                    val root = DocumentFile.fromTreeUri(this, treeUri)
                    root?.listFiles()?.forEach {
                        Log.d(TAG, "目录下文件名称：${it.name}")
                    }
                } catch (e: SecurityException) {
                    Log.d(TAG, "uri 权限失效，调用目录获取")
                    selectDirActivityResult.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
                }
            } else {
                Log.d(TAG, "没有永久访问权限，调用目录获取")
                selectDirActivityResult.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
            }
        }
    }

    private fun checkUriFlag(uri: Uri, flag: Int): Boolean {
        try {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_FLAGS)
                val columnFlags = cursor.getInt(columnIndex)
                Log.i(TAG,"Column Flags：$columnFlags  Flag：$flag")
                if ((columnFlags and  flag) == flag) {
                    return true
                }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 永久保留权限
     */
    private fun savePersistablePermission(uri: Uri) {
        val sp = getSharedPreferences("DirPermission", Context.MODE_PRIVATE)
        sp.edit {
            this.putString("uri", uri.toString())
            this.commit()
        }
        // Check for the freshest data.
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    /**
     * 获取文档元数据
     */
    private fun dumpImageMetaData(uri: Uri) {
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val displayName = cursor.getString(columnIndex)
            Log.i(TAG, "Display Name：$displayName")
            val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
            val size: String = if (!cursor.isNull(sizeIndex)) {
                cursor.getString(sizeIndex)
            } else {
                "Unknown"
            }
            Log.i(TAG, "Size：$size")
            cursor.close()
        }

    }

    /**
     * 通过Uri 获取Bitmap，耗时操作不应该在主线程
     */
    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(uri, "r")
        if (parcelFileDescriptor != null) {
            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        }
        return null

    }

    /**
     * 通过Uri获取InputStream
     */
    private fun readTextFromUri(uri: Uri): String {
        val stringBuffer = StringBuffer()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuffer.append(line)
                    line = reader.readLine()
                }
                inputStream.close()
            }
        }
        return stringBuffer.toString()
    }


    private fun alterDocument(uri: Uri) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                // use{} lets the document provider know you're done by automatically closing the stream
                FileOutputStream(it.fileDescriptor).use {
                    it.write(
                        ("Overwritten by MyCloud at ${System.currentTimeMillis()}\n").toByteArray()
                    )
                    Log.d(TAG, "编辑成功")
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
