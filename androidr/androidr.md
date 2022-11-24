# AndroidR

`Android 11` 基于 `Android` 早期版本构建，增加了多种功能和更新，以保障用户安全并提高透明度和可控性。所有开发者都应查看隐私功能并测试他们的应用。具体影响可能会因每个应用的核心功能、目标平台和其他因素而异。

[Android 11中的隐私权变更](https://developer.android.com/about/versions/11/privacy) 

## 重大隐私权变更
下表汇总了 `Android 11` 中与隐私权相关的主要变更。
||隐私权变更|受影响的应用|
|--|:-|--|
|✅|**强制执行分区存储机制**<br>以 Android 11 或更高版本为目标平台的应用始终会受分区存储行为的影响	|以 Android 11 或更高版本为目标平台的应用，以及以 Android 10 为目标平台且未将 requestLegacyExternalStorage 设为 true 以停用分区存储的应用|
|✅|**单次授权**<br>使用单次授权功能，用户可以授予对位置信息、麦克风和摄像头的临时访问权限|在 Android 11 或更高版本上运行且请求位置信息、麦克风或摄像头权限的应用|
|✅|**自动重置权限**<br>如果用户在 Android 11 或更高版本上几个月未与应用互动，系统会自动重置应用的敏感权限|以 Android 11 或更高版本为目标平台且在后台执行大部分工作的应用|
|✅|**后台位置信息访问权限**<br>Android 11 更改了用户向应用授予后台位置信息权限的方式|以 Android 11 或更高版本为目标平台且需要[在后台访问位置信息](https://developer.android.com/training/location/permissions#background)的应用|
|✅|**软件包可见性**<br>Android 11 更改了应用查询同一设备上的其他已安装应用及与之互动的方式|以 Android 11 或更高版本为目标平台且与设备上的其他已安装应用交互的应用|
|✅|**前台服务**<br/>Android 11 更改了前台服务访问位置信息、摄像头和麦克风相关数据的方式|在 Android 11 或更高版本上运行且在前台服务中访问位置信息、摄像头或麦克风的应用|

## Android 11 中的存储机制更新

Android 11（API 级别 30）进一步增强了平台功能，为外部存储设备上的应用和用户数据提供了更好的保护。此版本引入了多项增强功能，例如，可主动选择启用的媒体原始文件路径访问机制、面向媒体的批量编辑操作，以及存储访问框架的界面更新。

 [Android 11 存储常见问题解答](https://medium.com/androiddevelopers/android-11-storage-faq-78cefea52b7c)

### 强制执行分区存储

在 Android 11 上运行但以 Android 10（API 级别 29）为目标平台的应用仍可请求 [`requestLegacyExternalStorage`](https://developer.android.com/reference/android/R.attr#requestLegacyExternalStorage) 属性。应用可以利用此标记[暂时停用与分区存储相关的变更](https://developer.android.com/training/data-storage/use-cases#opt-out-scoped-storage)，例如授予对不同目录和不同类型的媒体文件的访问权限。

当您将应用更新为以 Android 11 为目标平台后，系统会忽略 `requestLegacyExternalStorage` 标记。

如果是覆盖安装呢，可以增加`android:preserveLegacyExternalStorage="true"`，暂时关闭分区存储，好让开发者完成数据迁移的工作。为什么是暂时呢？因为只要**卸载重装**，就会失效了。



### 管理设备存储空间

从 Android 11 开始，使用分区存储模型的应用只能访问自身的应用专用缓存文件。如果您的应用需要管理设备存储空间，请按照关于如何[查询可用空间](https://developer.android.com/training/data-storage/app-specific#query-free-space)的说明操作。

1. 通过调用 [`ACTION_MANAGE_STORAGE`](https://developer.android.com/reference/kotlin/android/os/storage/StorageManager#action_manage_storage) intent 操作检查可用空间。
2. 如果设备上的可用空间不足，请提示用户同意让您的应用清除所有缓存。为此，请调用 [`ACTION_CLEAR_APP_CACHE`](https://developer.android.com/reference/kotlin/android/os/storage/StorageManager#action_clear_app_cache) intent 操作。

> **注意**：`ACTION_CLEAR_APP_CACHE` intent 操作会严重影响设备的电池续航时间，并且可能会从设备上移除大量的文件。

### 外部存储设备上的应用专用目录

从 Android 11 开始，应用无法[在外部存储设备上创建自己的应用专用目录](https://developer.android.com/training/data-storage/app-specific#external)。如需访问系统为您的应用提供的目录，请调用 [`getExternalFilesDirs()`](https://developer.android.com/reference/android/content/Context#getExternalFilesDirs(java.lang.String))。

### 媒体文件访问权限

为了在保证用户隐私的同时可以更轻松地访问媒体，Android 11 增加了以下功能。

#### 执行批量操作

在 Android 11 及更高版本中，您可以要求用户选择一组媒体文件，然后通过一次操作更新这些媒体文件。这些方法可在各种设备上提供更好的一致性，并且可让用户更轻松地管理其媒体集合。

- [`createWriteRequest()`](https://developer.android.com/reference/android/provider/MediaStore#createWriteRequest(android.content.ContentResolver, java.util.Collection))：用户向应用授予对指定媒体文件组的写入访问权限的请求。
- [`createFavoriteRequest()`](https://developer.android.com/reference/android/provider/MediaStore#createFavoriteRequest(android.content.ContentResolver, java.util.Collection, boolean))：用户将设备上指定的媒体文件标记为“收藏”的请求。对该文件具有读取访问权限的任何应用都可以看到用户已将该文件标记为“收藏”。
- [`createTrashRequest()`](https://developer.android.com/reference/android/provider/MediaStore#createTrashRequest(android.content.ContentResolver, java.util.Collection, boolean))：用户将指定的媒体文件放入设备垃圾箱的请求。垃圾箱中的内容会在系统定义的时间段后被永久删除。
- [`createDeleteRequest()`](https://developer.android.com/reference/android/provider/MediaStore#createDeleteRequest(android.content.ContentResolver, java.util.Collection))：用户立即永久删除指定的媒体文件（而不是先将其放入垃圾箱）的请求。

系统在调用以上任何一个方法后，会构建一个 [`PendingIntent`](https://developer.android.com/reference/android/app/PendingIntent) 对象。应用调用此 intent 后，用户会看到一个对话框，请求用户同意应用更新或删除指定的媒体文件。

例如，以下是构建 `createWriteRequest()` 调用的方法：

```kotlin
val urisToModify = /* A collection of content URIs to modify. */
val editPendingIntent = MediaStore.createWriteRequest(contentResolver,
        urisToModify)
// Launch a system prompt requesting user permission for the operation.
startIntentSenderForResult(editPendingIntent.intentSender, EDIT_REQUEST_CODE,
    null, 0, 0, 0)
```

评估用户的响应。如果用户提供了同意声明，请继续执行媒体操作。否则，请向用户说明您的应用为何需要获取相应权限：

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int,
                 data: Intent?) {
    ...
    when (requestCode) {
        EDIT_REQUEST_CODE ->
            if (resultCode == Activity.RESULT_OK) {
                /* Edit request granted; proceed. */
            } else {
                /* Edit request not granted; explain to the user. */
            }
    }
}
```

您可以对 [`createFavoriteRequest()`](https://developer.android.com/reference/android/provider/MediaStore#createFavoriteRequest(android.content.ContentResolver, java.util.Collection, boolean))、[`createTrashRequest()`](https://developer.android.com/reference/android/provider/MediaStore#createTrashRequest(android.content.ContentResolver, java.util.Collection, boolean)) 和 [`createDeleteRequest()`](https://developer.android.com/reference/android/provider/MediaStore#createDeleteRequest(android.content.ContentResolver, java.util.Collection)) 使用相同的通用模式。

### 使用直接文件路径和原生库访问文件

https://developer.android.com/training/data-storage/shared/media#direct-file-paths

为了帮助您的应用更顺畅地使用第三方媒体库，Android 11（API 级别 30）及更高版本允许您使用 [`MediaStore`](https://developer.android.com/reference/android/provider/MediaStore) API 以外的 API 来访问共享存储空间中的媒体文件。不过，您可以改为使用以下任一 API 来直接访问媒体文件：

- [`File`](https://developer.android.com/reference/java/io/File) API。
- 原生库，例如 `fopen()`。

看到这里也许会产生疑惑，在`Android10`中不是进行分区存储了么，这么`Android11`又开始能直接使用文件路径进行访问了。其实这里是Google在分区存储上为开发者做了优化（PS：这里在网上看到有部分开发者反馈分区存储的Uri传到Native层时，这中Uri无法在Native层打开^_^）。

也许有小伙伴问到既然`Android11`可以直接使用`File API`访问媒体文件了，那分区存储这个适配还有必要么？（PS：我知道肯定有必要，但我想知道为什么会有必要？）

```kotlin
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
    //val str = Environment.getExternalStorageDirectory().absolutePath+"/Download/tanzhenxing.txt"//不可访问
    val str = "/sdcard/Download/tanzhenxing02.txt"//不可访问
    //val str = "/sdcard/Download/PXL_20221116_120102566.jpg"//可访问
    //如果您没有任何与存储空间相关的权限，您可以访问应用专属目录中的文件，并可使用 File API 访问归因于您的应用的媒体文件。
    //如果您的应用尝试使用 File API 访问文件但没有必要的权限，就会发生 FileNotFoundException。
    //PS:如果这个文件是自己应用程序创建的，是可以通过FileAPI进行访问的，但卸载重装会丢失访问权限；如果没有访问权限会发生异常；
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
```

我们看上面的一段代码就知道如果适配了`Android11`，那么只能通过`File API` 访问媒体文件和自己有访问权限的文件；除此之外我们如果访问其他文件会造成以下崩溃：

```shell
Caused by: java.io.FileNotFoundException: /sdcard/Download/tanzhenxing.txt: open failed: EACCES (Permission denied)
	at libcore.io.IoBridge.open(IoBridge.java:574)
	at java.io.FileInputStream.<init>(FileInputStream.java:160)
	at com.tzx.androidr.AndroidRMainActivity.getFileContent(AndroidRMainActivity.kt:57)
	at com.tzx.androidr.AndroidRMainActivity.onCreate$lambda-0(AndroidRMainActivity.kt:41)
	at com.tzx.androidr.AndroidRMainActivity.$r8$lambda$MxKbezjb4Znij4KrMlxgA5nFBbM(Unknown Source:0)
	at com.tzx.androidr.AndroidRMainActivity$$ExternalSyntheticLambda1.onActivityResult(Unknown Source:4)
	at androidx.activity.result.ActivityResultRegistry.doDispatch(ActivityResultRegistry.java:392)
	at androidx.activity.result.ActivityResultRegistry.dispatchResult(ActivityResultRegistry.java:351)
	at androidx.activity.ComponentActivity.onRequestPermissionsResult(ComponentActivity.java:667)
	at androidx.fragment.app.FragmentActivity.onRequestPermissionsResult(FragmentActivity.java:636)
	at android.app.Activity.dispatchRequestPermissionsResult(Activity.java:8759)
	at android.app.Activity.dispatchActivityResult(Activity.java:8617)
	at android.app.ActivityThread.deliverResults(ActivityThread.java:5340)
	... 13 more
```

适配`Android11`之后想要访问文件，需要通过什么方式呢？

> 访问应用专属目录

```kotlin
//分区存储空间,/data/data/package/files
val file = File(context.filesDir, filename)
//应用专属外部存储空间,/storage/sdcard0/Android/data/package/files
val appSpecificExternalDir = File(context.getExternalFilesDir(), filename)
```

> 访问公共媒体目录文件

使用**MediaStore**或者**SAF(存储访问框架–Storage Access Framework)**；

### 访问其他应用中的数据

为保护用户的隐私，在搭载 Android 11 或更高版本的设备上，系统会进一步对您的应用访问其他应用的私有目录的行为进行限制。

#### 访问内部存储设备上的数据目录

如果您的应用以 Android 11 为目标平台，则不能访问其他任何应用的数据目录中的文件，即使其他应用以 Android 8.1（API 级别 27）或更低版本为目标平台且已使其数据目录中的文件全局可读也是如此。

#### 访问外部存储设备上的应用专用目录

在 Android 11 上，应用无法再访问外部存储设备中的任何其他应用的[专用于特定应用的目录](https://developer.android.com/training/data-storage/app-specific#external)中的文件。

