package com.tzx.androidr

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tzx.androidr.databinding.ActivityAndroidRmainBinding
import java.io.*
import android.widget.Toast
import android.view.Gravity


class AndroidRMainActivity : AppCompatActivity() {
    private val TAG = "AndroidRMainActivity"
    private lateinit var binding: ActivityAndroidRmainBinding
    private lateinit var testRequestPermissionActivityResults: ActivityResultLauncher<Array<out String?>?>
    private lateinit var testRequestPermissionActivityResult: ActivityResultLauncher<String>
    private val mContext = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAndroidRmainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTestFileApiFunc()
        initTestToastFunc()
        initTestBackGroundLocationFunc()
        initTestForeGroundLocationFunc()
        initTestLocationFunc()
    }

    private fun initTestFileApiFunc() {
        testRequestPermissionActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (!it) {
                    Log.d(TAG, "testRequestPermissionActivityResult:授权失败")
                    return@registerForActivityResult
                }
                //val str = "/sdcard/DCIM/Camera/PXL_20221109_112534451.mp4"//可访问
                //val str = "/sdcard/screen/screen_10_23_17-41-47.png"//可访问
                //val str = "/sdcard/Download/charles-proxy-ssl-proxying-certificate.pem.crt"//不可访问
                //val str = "/sdcard/coverage.ec"//不可访问
                //val str = Environment.getExternalStoragePublicDirectory("").absolutePath + "/test.txt"//不可访问
                //val str = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/tanzhenxing.txt"//不可访问
                //val str = Environment.getExternalStorageDirectory().absolutePath + "/Download/tanzhenxing.txt"//不可访问
                val str = "/sdcard/Download/tanzhenxing02.txt"//不可访问
                //val str = "/sdcard/Download/PXL_20221116_120102566.jpg"//可访问
                //如果您没有任何与存储空间相关的权限，您可以访问应用专属目录中的文件，并可使用 File API 访问归因于您的应用的媒体文件。
                //如果您的应用尝试使用 File API 访问文件但没有必要的权限，就会发生 FileNotFoundException。
                //PS:如果这个文件是自己应用程序创建的，那么是可以通过File API进行访问的，但是卸载重装会丢失访问权限；如果没有访问权限会发生异常；
                val file = File(str)
                if (!file.exists()) {
                    file.createNewFile()
                }
                setFileContent(file, "\nHello World~!")//给文件写入内容
                Log.d(TAG, "fileName：" + file.name)
                Log.d(TAG, "fileName：" + file.totalSpace)
                Log.d(TAG, "fileName：" + getFileContent(file))//输出文件内容
            }
        binding.testFile.setOnClickListener {
            testRequestPermissionActivityResult.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun initTestToastFunc() {
        binding.testToast.setOnClickListener {
            it.postDelayed({ showToast() }, 3000)
        }
    }

    private fun showToast() {
        val textView = TextView(mContext.applicationContext)
        textView.text = "Hello"
        textView.setTextColor(Color.RED)
        val toast = Toast(mContext)
        toast.view = textView
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 70)
        toast.show()
    }

    @Throws(IOException::class)
    private fun getFileContent(file: File?): String? {
        val buf = StringBuffer()
        val fileStream = FileInputStream(file)
        val input = InputStreamReader(fileStream, "utf-8")
        var br: BufferedReader? = null
        var sb: String? = null
        try {
            br = BufferedReader(input)
            var str: String? = null
            while (br.readLine().also { str = it } != null) {
                buf.append(str)
            }
            sb = buf.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.message, e)
        } finally {
            br?.close()
        }
        return sb
    }

    private fun setFileContent(file: File?, str: String?) {
        val fileWrite = FileWriter(file, true)
        fileWrite.write(str)
        fileWrite.close()
    }

    private fun initTestLocationFunc() {
        binding.testLocation.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION), 100)
            }
        }
    }

    private fun initTestForeGroundLocationFunc() {
        binding.testForegroundLocation.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 100)
            }
        }
    }

    private fun initTestBackGroundLocationFunc() {
        binding.testBackgroundLocation.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 100)
            }
        }
    }
}