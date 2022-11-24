package com.tzx.androidr

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tzx.androidr.databinding.ActivityAndroidRmainBinding
import java.io.*

class AndroidRMainActivity : AppCompatActivity() {
    private val TAG = "AndroidRMainActivity"
    private lateinit var binding: ActivityAndroidRmainBinding
    private lateinit var testRequestPermissionActivityResults: ActivityResultLauncher<Array<out String?>?>
    private lateinit var testRequestPermissionActivityResult: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAndroidRmainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        testRequestPermissionActivityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
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
        binding.test.setOnClickListener {
            testRequestPermissionActivityResult.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    @Throws(IOException::class)
    fun getFileContent(file: File?): String? {
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

    fun setFileContent(file: File?, str: String?) {
        val fileWrite = FileWriter(file, true)
        fileWrite.write(str)
        fileWrite.close()
    }
}