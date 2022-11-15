package com.tzx.androidq

import android.Manifest
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.IntentSender
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tzx.androidq.databinding.AndroidqActivityScopedStorageLayoutBinding
import java.io.*


/**
 * Created by Tanzhenxing
 * Date: 2022/10/11 19:04
 * Description:
 */
class ScopedStorageActivity : AppCompatActivity() {
    private val TAG = "ScopedStorage"
    private var queryUri: Uri? = null
    private lateinit var binding: AndroidqActivityScopedStorageLayoutBinding
    private lateinit var createBitmapForActivityResult: ActivityResultLauncher<String>
    private lateinit var queryPictureForActivityResult: ActivityResultLauncher<String>
    private lateinit var readPictureForActivityResult: ActivityResultLauncher<String>
    private lateinit var registerForActivityResult: ActivityResultLauncher<Array<out String?>?>
    private lateinit var updatePictureForActivityResult: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var deletePictureRequestPermissionActivityResult: ActivityResultLauncher<String>
    private lateinit var deletePictureSenderRequestActivityResult: ActivityResultLauncher<IntentSenderRequest>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AndroidqActivityScopedStorageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createAppSpecificFile()
        createAppSpecificFolder()
        createFileByMediaStore()
        queryFileByMediaStore()
        readFileByMediaStore()
        loadThumbnail()
        updateFileByMediaStore()
        deleteFileByMediaStore()
    }


    /**
     * 在App-Specific目录下创建文件
     */
    private fun createAppSpecificFile() {
        binding.createAppSpecificFileBtn.setOnClickListener {
            val documents = getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)
            if (documents.isNotEmpty()) {
                val dir = documents[0]
                var os: FileOutputStream? = null
                try {
                    val newFile = File(dir.absolutePath, "MyDocument")
                    os = FileOutputStream(newFile)
                    os.write("create a file".toByteArray(Charsets.UTF_8))
                    os.flush()
                    Log.d(TAG, "创建成功")
                    dir.listFiles()?.forEach { file: File? ->
                        if (file != null) {
                            Log.d(TAG, "Documents 目录下的文件名：" + file.name)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d(TAG, "创建失败")

                } finally {
                    closeIO(os)
                }

            }
        }
    }

    /**
     * 在App-Specific目录下创建文件夹
     */
    private fun createAppSpecificFolder() {
        binding.createAppSpecificFolderBtn.setOnClickListener {
            getExternalFilesDir("apk")?.let {
                if (it.exists()) {
                    Log.d(TAG, "创建成功")
                } else {
                    Log.d(TAG, "创建失败")
                }
            }
        }
    }

    /**
     * 使用MediaStore创建文件
     */
    private fun createFileByMediaStore() {
        createBitmapForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                createBitmap()
            }
        binding.createFileByMediaStoreBtn.setOnClickListener {
            createBitmapForActivityResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun createBitmap() {
        val values = ContentValues()
        val displayName = "NewImage.png"
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.TITLE, "Image.png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/sl")
        } else {
            values.put(
                MediaStore.MediaColumns.DATA,
                "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
            )
        }
        //requires android.permission.WRITE_EXTERNAL_STORAGE, or grantUriPermission()
        val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        //java.lang.UnsupportedOperationException: Writing to internal storage is not supported.
        //val external = MediaStore.Images.Media.INTERNAL_CONTENT_URI
        val insertUri = contentResolver.insert(external, values)
        var os: OutputStream? = null
        try {
            if (insertUri != null) {
                os = contentResolver.openOutputStream(insertUri)
            }
            if (os != null) {
                val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
                //创建了一个红色的图片
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.RED)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, os)
                Log.d(TAG, "创建Bitmap成功")
                if (insertUri != null) {
                    values.clear()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/sl2")
                    } else {
                        values.put(
                            MediaStore.MediaColumns.DATA,
                            "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
                        )
                    }
                    contentResolver.update(insertUri, values, null, null)
                }
            }
        } catch (e: IOException) {
            Log.d(TAG, "创建失败：${e.message}")
        } finally {
            closeIO(os)
        }
    }

    /**
     * 通过MediaStore查询文件
     */
    private fun queryFileByMediaStore() {
        queryPictureForActivityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            queryUri = queryImageUri("yellow.jpg")
        }
        binding.queryFileByMediaStoreBtn.setOnClickListener {
            queryPictureForActivityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /**
     * @param displayName 查询的图片文件名称
     * @return 第一个遍历到的该文件名的uri
     */
    private fun queryImageUri(displayName: String): Uri? {
        val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME}=?"
        val args = arrayOf(displayName)
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = contentResolver.query(external, projection, selection, args, null)
        var queryUri: Uri? = null
        if (cursor != null) {
            while (cursor.moveToNext()) {
                queryUri = ContentUris.withAppendedId(external, cursor.getLong(0))
                Log.d(TAG, "$displayName:查询成功，Uri路径$queryUri")
                queryUri.let {
                    cursor.close()
                    return it
                }
            }
            cursor.close()
        }
        Log.d(TAG, "$displayName:查询失败，Uri路径$queryUri")
        return queryUri
    }

    /**
     * 根据查询到的uri，获取bitmap
     */
    private fun readFileByMediaStore() {
        readPictureForActivityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                readBitmapNotException()
            }
        }
        binding.readFileByMediaStoreBtn.setOnClickListener {
            readPictureForActivityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun readBitmapNotException() {
        val queryUri = queryImageUri("yellow.jpg")
        if (queryUri != null) {
            var pfd: ParcelFileDescriptor? = null
            try {
                pfd = contentResolver.openFileDescriptor(queryUri, "r")
                if (pfd != null) {
                    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor, null, options)
                    // 调用上面定义的方法计算inSampleSize值
                    options.inSampleSize = calculateInSampleSize(options, 500, 500)
                    // 使用获取到的inSampleSize值再次解析图片
                    options.inJustDecodeBounds = false
                    val bitmap =
                        BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor, null, options)
                    binding.imageIv.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                closeIO(pfd)
            }

        } else {
            Log.d(TAG, "还未查询到Uri")
        }
    }


    /**
     * 根据查询到的Uri，获取Thumbnail
     */
    private fun loadThumbnail() {
        binding.loadThumbnailBtn.setOnClickListener {
            queryUri?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val bitmap = contentResolver.loadThumbnail(it, Size(100, 200), null)
                    binding.imageIv.setImageBitmap(bitmap)
                } else {
                    MediaStore.Images.Thumbnails.getThumbnail(
                        contentResolver,
                        ContentUris.parseId(it),
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null
                    )?.let { bitmap ->
                        binding.imageIv.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    /**
     * 根据查询得到的Uri，修改文件
     */
    private fun updateFileByMediaStore() {
        updatePictureForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    updateFileNameWithException()
                } else {
                    Log.d(TAG, "updateFileByMediaStore: 授权失败")
                }
            }
        registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                var result = true
                it.keys.forEach { permission ->
                    if (it[permission] == false) {
                        result = false
                    }
                }
                if (result) {
                    updateFileNameWithException()
                }
            }
        binding.updateFileByMediaStoreBtn.setOnClickListener {
            registerForActivityResult.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    private fun updateFileNameWithException() {
        //这里的img 是我相册里的，如果运行demo，可以换成你自己的
        val queryUri = queryImageUri("IMG_20221104_161840.jpg")
        var os: OutputStream? = null
        try {
            queryUri?.let { uri ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    os = contentResolver.openOutputStream(uri)
                    os?.let {
                        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
                        //创建了一个红色的图片
                        val canvas = Canvas(bitmap)
                        canvas.drawColor(Color.YELLOW)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, os)
                        val contentValues = ContentValues()
                        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "yellow.jpg")
                        contentResolver.update(uri, contentValues, null, null)
                    }
                } else {
                    val filePathByUri = UriTool.getFilePathByUri(this@ScopedStorageActivity, queryUri)
                    val file = File(filePathByUri)
                    os = FileOutputStream(filePathByUri)
                    os.let {
                        val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
                        //创建了一个红色的图片
                        val canvas = Canvas(bitmap)
                        canvas.drawColor(Color.YELLOW)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, os)
                        val yellowFile = File(file.parent, "yellow.jpg")
                        file.renameTo(yellowFile)
                        Log.d(TAG, "updateFileByMediaStore: 文件修改成功")
                    }
                }
            }

        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (e is RecoverableSecurityException) {
                    try {
                        updatePictureForActivityResult.launch(
                            IntentSenderRequest.Builder(e.userAction.actionIntent.intentSender)
                                .build()
                        )
                    } catch (e2: IntentSender.SendIntentException) {
                        e2.printStackTrace()
                    }
                    return
                }
            }
            e.printStackTrace()
        } finally {
            closeIO(os)
        }
    }

    /**
     * 删除MediaStore文件
     */
    private fun deleteFileByMediaStore() {
        deletePictureRequestPermissionActivityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                deleteFile()
            } else {
                Log.d(TAG, "deleteFileByMediaStore: 授权失败")
            }
        }
        deletePictureSenderRequestActivityResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                deleteFile()
            } else {
                Log.d(TAG, "updateFileByMediaStore: 授权失败")
            }
        }
        binding.deleteFileByMediaStoreBtn.setOnClickListener {
            deletePictureRequestPermissionActivityResult.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun deleteFile() {
        //这里的img 是我相册里的，如果运行demo，可以换成你自己的
        val queryUri = queryImageUri("2021-10-14_11.19.18.882.png")
        try {
            if (queryUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val delete = contentResolver.delete(queryUri, null, null)
                    //delete=0删除失败，delete=1也不一定删除成功，必须要授予文件的写权限
                    Log.d(TAG, "contentResolver.delete:$delete")
                } else {
                    val filePathByUri = UriTool.getFilePathByUri(this@ScopedStorageActivity, queryUri)
                    File(filePathByUri).delete()
                }
            }
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (e is RecoverableSecurityException) {
                    try {
                        deletePictureSenderRequestActivityResult.launch(
                            IntentSenderRequest.Builder(e.userAction.actionIntent.intentSender)
                                .build()
                        )
                    } catch (e2: IntentSender.SendIntentException) {
                        e2.printStackTrace()
                    }
                    return
                }
            }
            e.printStackTrace()
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int
    ): Int {
        // 源图片的高度和宽度
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    private fun closeIO(io: Closeable?) {
        try {
            io?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}