# AndroidR

`Android 11` 基于 `Android` 早期版本构建，增加了多种功能和更新，以保障用户安全并提高透明度和可控性。所有开发者都应查看隐私功能并测试他们的应用。具体影响可能会因每个应用的核心功能、目标平台和其他因素而异。

[Android 11介绍](https://developer.android.com/about/versions/11)

[Android 11中的隐私权变更](https://developer.android.com/about/versions/11/privacy) 

[Android 11行为变更](https://developer.android.com/about/versions/11/behavior-changes-all)

[以 Android 11 为目标平台的应用的行为变更](https://developer.android.com/about/versions/11/behavior-changes-11)

## 重大隐私权变更
下表汇总了 `Android 11` 中与隐私权相关的主要变更。
||隐私权变更|受影响的应用|缓存策略|
|--|:-|--|--|
|✅|**强制执行分区存储机制**<br>以 Android 11 或更高版本为目标平台的应用始终会受分区存储行为的影响	|以 Android 11 或更高版本为目标平台的应用，以及以 Android 10 为目标平台且未将 `requestLegacyExternalStorage` 设为 `true` 以停用分区存储的应用|更新您的应用以使用分区存储<br/>[详细了解分区存储变更](https://developer.android.com/about/versions/11/privacy/storage)|
|✅|**单次授权**<br>使用单次授权功能，用户可以授予对位置信息、麦克风和摄像头的临时访问权限|在 Android 11 或更高版本上运行且请求位置信息、麦克风或摄像头权限的应用|在尝试访问受某项权限保护的数据之前，检查您的应用是否具有该权限<br/>[遵循请求权限方面的最佳做法](https://developer.android.com/training/permissions/requesting)|
|✅|**自动重置权限**<br>如果用户在 Android 11 或更高版本上几个月未与应用互动，系统会自动重置应用的敏感权限|以 Android 11 或更高版本为目标平台且在后台执行大部分工作的应用|要求用户阻止系统重置应用的权限<br/>[详细了解自动重置权限](https://developer.android.com/about/versions/11/privacy/permissions#auto-reset)|
|✅|**后台位置信息访问权限**<br>Android 11 更改了用户向应用授予后台位置信息权限的方式|以 Android 11 或更高版本为目标平台且需要[在后台访问位置信息](https://developer.android.com/training/location/permissions#background)的应用|通过对权限请求方法的多次单独调用，逐步请求在前台（粗略或精确）和后台访问位置信息的权限。必要时，说明用户授予该权限所能得到的益处<br/>[详细了解 Android 11 中的在后台访问位置信息的权限](https://developer.android.com/about/versions/11/privacy/location#background-location)|
|✅|**软件包可见性**<br>Android 11 更改了应用查询同一设备上的其他已安装应用及与之互动的方式|以 Android 11 或更高版本为目标平台且与设备上的其他已安装应用交互的应用|将 `<queries>` 元素添加到应用的清单<br/>[详细了解软件包可见性](https://developer.android.com/about/versions/11/privacy/package-visibility)|
|✅|**前台服务**<br/>Android 11 更改了前台服务访问位置信息、摄像头和麦克风相关数据的方式|在 Android 11 或更高版本上运行且在前台服务中访问位置信息、摄像头或麦克风的应用|分别针对需要访问摄像头和麦克风的前台服务，声明 `camera` 和 `microphone` 前台服务类型。但请注意，应用在后台运行时启动的前台服务通常无法访问位置信息、摄像头或麦克风。<br/>[详细了解前台服务的变更](https://developer.android.com/about/versions/11/privacy/foreground-services)|

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

#### 所有文件访问权限

应用可以通过执行以下操作向用户请求“所有文件访问权限”：

1. 在清单中声明 [`MANAGE_EXTERNAL_STORAGE`](https://developer.android.com/reference/android/Manifest.permission#MANAGE_EXTERNAL_STORAGE) 权限。
2. 使用 [`ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION`](https://developer.android.com/reference/android/provider/Settings#ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION) intent 操作将用户引导至一个系统设置页面，在该页面上，用户可以为您的应用启用以下选项：**授予所有文件的管理权限**。

如需确定您的应用是否已获得 `MANAGE_EXTERNAL_STORAGE` 权限，

请调用 [`Environment.isExternalStorageManager()`](https://developer.android.com/reference/android/os/Environment#isExternalStorageManager())。

```kotlin
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
val intent = Intent()
intent.action= Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
startActivity(intent)
val isHasStoragePermission= Environment.isExternalStorageManager()
```

### 文档访问限制

为让开发者有时间进行测试，以下与存储访问框架 (SAF) 相关的变更只有在应用以 Android 11 或更高版本为目标平台时才会生效。

#### 访问目录

您无法再使用 [`ACTION_OPEN_DOCUMENT_TREE`](https://developer.android.com/training/data-storage/shared/documents-files#grant-access-directory) intent 操作请求访问以下目录：

- 内部存储卷的根目录。
- 设备制造商认为可靠的各个 SD 卡卷的根目录，无论该卡是模拟卡还是可移除的卡。可靠的卷是指应用在大多数情况下可以成功访问的卷。
- `Download` 目录。

#### 访问文件

您无法再使用 [`ACTION_OPEN_DOCUMENT_TREE`](https://developer.android.com/training/data-storage/shared/documents-files#grant-access-directory) 或 [`ACTION_OPEN_DOCUMENT`](https://developer.android.com/training/data-storage/shared/documents-files#open-file) intent 操作请求用户从以下目录中选择单独的文件：

- `Android/data/` 目录及其所有子目录。
- `Android/obb/` 目录及其所有子目录。

## 权限申请相关变更

### 单次授权

从 Android 11 开始，每当应用请求与位置信息、麦克风或摄像头相关的权限时，面向用户的权限对话框会包含**仅限这一次**选项。如果用户在对话框中选择此选项，系统会向应用授予临时的单次授权。

然后，应用可以在一段时间内访问相关数据，具体时间取决于应用的行为和用户的操作：

- 当应用的 activity 可见时，应用可以访问相关数据。
- 如果用户将应用转为后台运行，应用可以在短时间内继续访问相关数据。
- 如果您在 activity 可见时启动了一项前台服务，并且用户随后将您的应用转到后台，那么您的应用可以继续访问相关数据，直到该前台服务停止。

<img src="/Users/tanzx/AndroidStudioWorkSpace/GitHub/AndroidSystemVersionAdapter/androidr/img/one-time-prompt.svg" style="zoom:50%;" />

### 应用进程在权限被撤消时终止

如果用户撤消单次授权（例如在系统设置中撤消），无论您是否启动了前台服务，应用都无法访问相关数据。与任何权限一样，如果用户撤消了应用的单次授权，应用进程就会终止。

当用户下次打开应用并且应用中的某项功能请求访问位置信息、麦克风或摄像头时，系统会再次提示用户授予权限。

### 自动重置未使用的应用的权限

如果应用以 Android 11 或更高版本为目标平台并且数月未使用，系统会通过自动重置用户已授予应用的运行时敏感权限来保护用户数据。此操作与用户在系统设置中查看权限并将应用的访问权限级别更改为**拒绝**的做法效果一样。如果应用遵循了有关[在运行时请求权限](https://developer.android.com/training/permissions/requesting)的最佳做法，那么您不必对应用进行任何更改。这是因为，当用户与应用中的功能互动时，您应该会验证相关功能是否具有所需权限。

### 权限对话框的可见性

从 Android 11 开始，在应用安装到设备上后，如果用户在使用过程中多次针对某项特定的权限点按**拒绝**，那么在您的应用再次请求该权限时，用户将不会看到系统权限对话框。该操作表示用户希望“不再询问”。在之前的版本中，除非用户先前已选中“不再询问”对话框或选项，否则每当您的应用请求权限时，用户都会看到系统权限对话框。Android 11 中的这一行为变更旨在避免重复请求用户已选择拒绝的权限。

### 系统提醒窗口变更

在 Android 11 中，向应用授予 [`SYSTEM_ALERT_WINDOW`](https://developer.android.com/reference/android/Manifest.permission#SYSTEM_ALERT_WINDOW) 权限的方式发生了一些变更。这些变更可以让权限的授予更有目的性，从而达到保护用户的目的。

### 根据请求自动向某些应用授予 SYSTEM_ALERT_WINDOW 权限

系统会根据请求自动向某些类型的应用授予 `SYSTEM_ALERT_WINDOW` 权限：

- 系统会自动向具有 [`ROLE_CALL_SCREENING`](https://developer.android.com/reference/android/app/role/RoleManager#ROLE_CALL_SCREENING) 且请求 `SYSTEM_ALERT_WINDOW` 的所有应用授予该权限。如果应用失去 `ROLE_CALL_SCREENING`，就会失去该权限。
- 系统会自动向通过 [`MediaProjection`](https://developer.android.com/reference/android/media/projection/MediaProjection) 截取屏幕且请求 `SYSTEM_ALERT_WINDOW` 的所有应用授予该权限，除非用户已明确拒绝向应用授予该权限。当应用停止截取屏幕时，就会失去该权限。此用例主要用于游戏直播应用。

这些应用无需发送 [`ACTION_MANAGE_OVERLAY_PERMISSION`](https://developer.android.com/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION) 以获取 `SYSTEM_ALERT_WINDOW` 权限，它们只需直接请求 `SYSTEM_ALERT_WINDOW` 即可。

### MANAGE_OVERLAY_PERMISSION intent 始终会将用户转至系统权限屏幕

从 Android 11 开始，[`ACTION_MANAGE_OVERLAY_PERMISSION`](https://developer.android.com/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION) intent 始终会将用户转至顶级**设置**屏幕，用户可在其中授予或撤消应用的 [`SYSTEM_ALERT_WINDOW`](https://developer.android.com/reference/android/Manifest.permission#SYSTEM_ALERT_WINDOW) 权限。intent 中的任何 `package:` 数据都会被忽略。

在更低版本的 Android 中，`ACTION_MANAGE_OVERLAY_PERMISSION` intent 可以指定一个软件包，它会将用户转至应用专用屏幕以管理权限。从 Android 11 开始将不再支持此功能，而是必须由用户先选择要授予或撤消哪些应用的权限。此变更可以让权限的授予更有目的性，从而达到保护用户的目的。

## 电话号码

Android 11 更改了您的应用在读取电话号码时使用的与电话相关的权限。

如果您的应用以 Android 11 或更高版本为目标平台，并且需要访问以下列表中显示的电话号码 API，则必须请求 [`READ_PHONE_NUMBERS`](https://developer.android.com/reference/kotlin/android/Manifest.permission#read_phone_numbers) 权限，而不是 `READ_PHONE_STATE` 权限。

- [`TelephonyManager`](https://developer.android.com/reference/kotlin/android/telephony/TelephonyManager#getline1number) 类和 [`TelecomManager`](https://developer.android.com/reference/kotlin/android/telecom/TelecomManager#getline1number) 类中的 `getLine1Number()` 方法。
- [`TelephonyManager`](https://developer.android.com/reference/kotlin/android/telephony/TelephonyManager) 类中不受支持的 `getMsisdn()` 方法。

如果您的应用声明 `READ_PHONE_STATE` 以调用前面列表中的方法以外的方法，您可以继续在所有 Android 版本中请求 `READ_PHONE_STATE`。不过，如果您仅对前面列表中的方法使用 `READ_PHONE_STATE` 权限，请按以下方式更新您的清单文件：

1. 更改 `READ_PHONE_STATE` 的声明，以使您的应用仅在 Android 10（API 级别 29）及更低版本中使用该权限。
2. 添加 `READ_PHONE_NUMBERS` 权限。

以下清单声明代码段演示了此过程：

```xml
<manifest>
    <!-- Grants the READ_PHONE_STATE permission only on devices that run
         Android 10 (API level 29) and lower. -->
    <uses-permission android:name="READ_PHONE_STATE"
                     android:maxSdkVersion="29" />
    <uses-permission android:name="READ_PHONE_NUMBERS" />
</manifest>
```

## 消息框的更新

### 来自后台的自定义消息框被屏蔽

出于安全方面的考虑，同时也为了保持良好的用户体验，如果包含自定义视图的消息框是以 Android 11 或更高版本为目标平台的应用从后台发送的，系统会屏蔽这些消息框。请注意，仍允许使用文本消息框；此类消息框是使用 [`Toast.makeText()`](https://developer.android.com/reference/kotlin/android/widget/Toast#maketext) 创建的，并不调用 [`setView()`](https://developer.android.com/reference/kotlin/android/widget/Toast#setview)。

如果您的应用仍尝试从后台发布包含自定义视图的消息框，系统不会向用户显示相应的消息，而是会在 logcat 中记录以下消息：

```shell
W/NotificationService: Blocking custom toast from package \
  <package> due to package not in the foreground
```

**需要关注的是如果应用处于后台，且需要展示吐司。那么使用原生的Toast~！**

### 消息框回调

如果您希望在消息框（文本消息框或自定义消息框）出现或消失时收到通知，请使用 Android 11 中添加的 [`addCallback()`](https://developer.android.com/reference/android/widget/Toast#addCallback(android.widget.Toast.Callback)) 方法。

## 相机

### 媒体 intent 操作需要系统默认相机

从 Android 11 开始，只有预装的系统相机应用可以响应以下 intent 操作：

- [`android.media.action.VIDEO_CAPTURE`](https://developer.android.com/reference/android/provider/MediaStore#ACTION_VIDEO_CAPTURE)
- [`android.media.action.IMAGE_CAPTURE`](https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE)
- [`android.media.action.IMAGE_CAPTURE_SECURE`](https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE_SECURE)

如果有多个预装的系统相机应用可用，系统会显示一个对话框，供用户选择应用。如果您希望自己的应用使用特定的第三方相机应用来代表其捕获图片或视频，可以通过为 intent 设置软件包名称或组件来使这些 intent 变得明确。

## 应用打包和安装

### 压缩的资源文件

如果以 Android 11（API 级别 30）或更高版本为目标平台的应用包含压缩的 `resources.arsc` 文件或者如果此文件未按 4 字节边界对齐，应用将无法安装。如果存在其中任意一种情况，系统将无法对此文件进行内存映射。无法进行内存映射的资源表必须读入 RAM 中的缓冲区，从而给系统造成不必要的内存压力，并大大增加设备的 RAM 使用量。

### 现在需要 APK 签名方案 v2

对于以 Android 11（API 级别 30）为目标平台，且目前仅使用 APK 签名方案 v1 签名的应用，现在还必须使用 [APK 签名方案 v2](https://source.android.com/security/apksigning/v2) 或更高版本进行签名。用户无法在搭载 Android 11 的设备上安装或更新仅通过 APK 签名方案 v1 签名的应用。

如需验证您的应用是否已使用 APK 签名方案 v2 或更高版本进行签名，您可以在命令行中使用 [Android Studio](https://developer.android.com/studio/publish/app-signing#sign_release) 或 [`apksigner`](https://developer.android.com/studio/command-line/apksigner) 工具。

**注意：为支持运行旧版 Android 的设备，除了使用 APK 签名方案 v2 或更高版本为您的 APK 签名之外，您还应继续使用 APK 签名方案 v1 进行签名。**

## 后台位置信息访问权限

Android 11 更改了应用中的功能获取[后台位置信息](https://developer.android.com/training/location/permissions#background)访问权限的方式。本部分介绍了上述各项变更。

如果应用中的某项功能从后台访问位置信息，请验证此类访问是否有必要，并考虑以其他方式获取该功能所需的信息。如需详细了解在后台访问位置信息的权限，请参阅[在后台访问位置信息](https://developer.android.com/training/location/background)页面。

### 单独请求在后台访问位置信息

正如有关如何[在运行时请求位置信息访问权限](https://developer.android.com/training/location/permissions#request-location-access-runtime)的指南中所述，您应该执行递增位置信息请求。如果您的应用以 Android 11 或更高版本为目标平台，系统会强制执行此最佳做法。如果您**同时请求**在**前台**访问位置信息的权限和在**后台**访问位置信息的权限，**系统会忽略该请求**，且不会向您的应用授予其中的任一权限。

### 权限对话框的变更

在搭载 Android 11 或更高版本的设备上，您的应用中的某项功能请求在后台访问位置信息时，系统对话框**不会包含**用于启用在**后台访问位置**信息权限的按钮。如需启用在后台访问位置信息的权限，用户必须在设置页面上针对应用的位置权限设置**一律允许**选项，如介绍如何[请求在后台访问位置信息](https://developer.android.com/training/location/permissions#request-background-location)的指南中所述。

### 后台位置信息访问权限使用总结

上面三个部分是摘自[Android 11 中的位置信息更新](https://developer.android.com/about/versions/11/privacy/location#background-location)，看完之后是不是感觉云里雾里的。下面举几个例子大家就会明白。

```kotlin
private fun initTestLocationFunc() {
    binding.testLocation.setOnClickListener {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION), 100)
        }
    }
}
```

> `android11以下的设备`，申请前台和后台位置权限（任意targetSdkVersion）：

<img src="/Users/tanzx/AndroidStudioWorkSpace/GitHub/AndroidSystemVersionAdapter/androidr/img/android910-requestlocation.png" style="zoom:50%;" />

> `Android11及以上的设备`，targetSdkVersion<=29(Android 10),申请前台和后台位置权限：

<img src="/Users/tanzx/AndroidStudioWorkSpace/GitHub/AndroidSystemVersionAdapter/androidr/img/android11-requestloction-device11.png" style="zoom:50%;" />

> `Android11及以上的设备`，targetSdkVersion=30(Android 11),申请前台和后台位置权限：

**无反应**，（PS:Android 11 或更高版本为目标平台，系统会强制执行此最佳做法。如果您**同时请求**在**前台**访问位置信息的权限和在**后台**访问位置信息的权限，**系统会忽略该请求**，且不会向您的应用授予其中的任一权限。[单独请求在后台访问位置信息](#单独请求在后台访问位置信息)）

```kotlin
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
```

> `Android11及以上设备`，先申请前台位置权限，后申请后台位置权限：

- 先执行申请前台权限（targetSdkVersion不区分）；

<img src="/Users/tanzx/AndroidStudioWorkSpace/GitHub/AndroidSystemVersionAdapter/androidr/img/android11-requestforebackground-device11.png" style="zoom:50%;" />

- 后执行申请后台权限（targetSdkVersion=29）；

<img src="/Users/tanzx/AndroidStudioWorkSpace/GitHub/AndroidSystemVersionAdapter/androidr/img/android10-requestbackground-device11.png" style="zoom:50%;" />

- 后执行申请后台权限（targetSdkVersion=30）；

<img src="/Users/tanzx/AndroidStudioWorkSpace/GitHub/AndroidSystemVersionAdapter/androidr/img/android11-requestbackground-device11.png" style="zoom:50%;" />

看完以上的实现效果，我们很清楚知道了该怎么进行后台定位权限的申请说明了吧。

- `targetSdkVersion<30`情况下，如果你之前就有判断过前台和后台位置权限，那就无需担心，没有什么需要适配。

- `targetSdkVersion>30`情况下,需要分开申请前后台位置权限，并且对后台位置权限申请做好说明和引导，当然也是为了更好的服务用户。

## 软件包可见性

[Android 11 中的软件包可见性](https://developer.android.com/about/versions/11/privacy/package-visibility)

Android 11 更改了应用查询用户已在设备上安装的其他应用以及与之交互的方式。使用 `<queries>` 元素，应用可以定义一组自身可访问的其他软件包。通过告知系统应向您的应用显示哪些其他软件包，此元素有助于鼓励最小权限原则。此外，此元素还可帮助 Google Play 等应用商店评估应用为用户提供的隐私权和安全性。

### 声明您的应用与一组特定的其他应用交互

如果您的应用以 Android 11 或更高版本为目标平台，您可能需要在应用的清单文件中添加 `<queries>` 元素。在 `<queries>` 元素中，[按软件包名称](https://developer.android.com/training/basics/intents/package-visibility#package-name)、[按 intent 签名](https://developer.android.com/training/basics/intents/package-visibility#intent-signature)或[按提供程序授权](https://developer.android.com/training/basics/intents/package-visibility#provider-authority)指定其他应用，如以下部分所述。

### 查询特定软件包及与之交互

如果您知道要查询或与之交互的一组特定应用（例如，与您的应用集成的应用或您使用其服务的应用），请将其软件包名称添加到 `<queries>` 元素内的一组 `<package>` 元素中：

```xml
<manifest package="com.example.game">
    <queries>
        <package android:name="com.example.store" />
        <package android:name="com.example.services" />
    </queries>
    ...
</manifest>
```

### 在给定 intent 过滤器的情况下查询应用及与之交互

您的应用可能需要查询一组具有特定用途的应用或与之交互，但您可能不知道要添加的具体软件包名称。在这种情况下，您可以在 `<queries>` 元素中列出 [intent 过滤器签名](https://developer.android.com/training/basics/intents/filters)。然后，您的应用就可以发现具有匹配的 `<intent-filter>` 元素的应用。

以下示例允许您的应用看到支持 JPEG 图片共享功能的已安装应用：

```xml
<manifest package="com.example.game">
    <queries>
        <intent>
            <action android:name="android.intent.action.SEND" />
            <data android:mimeType="image/jpeg" />
        </intent>
    </queries>
    ...
</manifest>
```

`<intent>` 元素有一些限制，具体可以查看 https://developer.android.com/training/basics/intents/package-visibility#intent-signature。

### 所有应用（不推荐）

在极少数情况下，您的应用可能需要查询设备上的所有已安装应用或与之交互，不管这些应用包含哪些组件。为了允许您的应用看到其他所有已安装应用，系统会提供 [`QUERY_ALL_PACKAGES`](https://developer.android.com/reference/android/Manifest.permission?hl=zh-cn#QUERY_ALL_PACKAGES) 权限。

下面列举了一些适合添加 `QUERY_ALL_PACKAGES` 权限的用例：

- 无障碍应用
- 浏览器
- 设备管理应用
- 安全应用
- 防病毒应用

不过，通常可以通过以下方式实现您应用的用例：与一组[自动可见](https://developer.android.com/training/package-visibility/automatic?hl=zh-cn)的应用交互，并在您的清单文件中声明您的应用需要访问的其他应用。为了尊重用户隐私，您的应用应请求应用正常工作所需的最小软件包可见性。

这项[来自 Google Play 的政策更新](https://support.google.com/googleplay/android-developer/answer/10158779?hl=zh-cn)为需要 `QUERY_ALL_PACKAGES` 权限的应用提供了相关准则。

## API更新

### 5G

[向您的应用添加 5G 功能](https://developer.android.com/about/versions/11/features/5g?hl=zh-cn)

在决定如何与 5G 互动时，思考一下您试图打造什么样的体验。5G 可通过一些方法增强您的应用，其中包括：

- 由于 5G 在速度和延迟方面的改进，自动使当前的体验更快更好。
- 提升用户体验，如通过显示 4k 视频或下载分辨率更高的游戏资产。
- 在确认增加的流量消耗不会让用户付费后，添加通常仅通过 WLAN 提供的体验，如主动下载一般为不按流量计费的 WLAN 保留的内容。
- 提供 5G 独有的体验，这种体验只能在高速度且低延迟的网络上实现。

Android 11 添加了 [5G API](https://developer.android.com/about/versions/11/features/5g)，使您的应用能够添加各种先进的功能。

- [按流量计费性](https://developer.android.com/about/versions/11/features/5g?hl=zh-cn#meteredness)
- [5G 检测](https://developer.android.com/about/versions/11/features/5g?hl=zh-cn#detection)
- [带宽估测](https://developer.android.com/about/versions/11/features/5g?hl=zh-cn#estimator)

## 前台服务

在 Android 11 中，前台服务何时可以访问设备的位置信息、摄像头和麦克风发生了一些变化。这有助于保护敏感的用户数据。

>  前台服务类型 camera 和 microphone

如果您的应用以 Android 11 或更高版本为目标平台，且在前台服务中访问摄像头或麦克风，则必须添加[前台服务类型](https://developer.android.com/guide/components/foreground-services#types) `camera` 和 `microphone`。

上一篇文章讲述适配Android10的时候，对于前台定位服务就必须加上`android:foregroundServiceType="location"`。

现在Android11上又增加了两个权限限制（摄像头和麦克风），如果前台服务需要访问位置、摄像头和麦克风，请按以下代码段所示声明服务：

```xml
//AndroidManifest.xml
<manifest>
    ...
    <service ... android:foregroundServiceType="location|camera" />
</manifest>
```

> 对在使用时访问的限制

如果您的应用[在后台运行时启动了某项前台服务](https://developer.android.com/guide/components/foreground-services#while-in-use-restrictions)，则该前台服务无法访问麦克风或摄像头。此外，除非您的应用具有[在后台访问位置信息](https://developer.android.com/training/location/permissions#background)的权限，否则该服务无法访问位置信息。

有些部分**豁免限制**，可以参考https://developer.android.com/guide/components/foreground-services#bg-access-restriction-exemptions。

## 数据访问审核

为了让应用及其依赖项访问用户私密数据的过程更加透明，Android 11 引入了数据访问审核功能。借助此流程得出的见解，您可以更好地识别可能出现的意外数据访问。您的应用可以注册 [`AppOpsManager.OnOpNotedCallback`](https://developer.android.com/reference/android/app/AppOpsManager.OnOpNotedCallback) 实例，该实例可在每次发生以下任一事件时执行相应操作：

- 应用的代码访问私密数据。为了帮助您确定应用的哪个逻辑部分调用了事件，您可以按归因标记审核数据访问。
- 依赖库或 SDK 中的代码访问私密数据。

如需了解详情，请参阅有关如何[审核对数据的访问权限](https://developer.android.com/guide/topics/data/audit-access)的指南。

> 简单描述一下该功能的使用，创建一个带标记的Context。然后用这个Context访问一些私密数据的时候就能在注册的回调里面获取相关的调用信息。

其实感觉这个并不能太大的作用，而且对于代码的入侵成本过高。相同的功能我们通过切面很容易搞定的。

# FAQ

Android 11 存储常见问题解答 [Android 11 storage FAQ](https://medium.com/androiddevelopers/android-11-storage-faq-78cefea52b7c)，我从中找几条记录一下。

> Scoped Storage 是否允许应用程序使用文件路径访问文件，例如使用文件 API？

我们认识到某些应用依赖于直接访问媒体文件路径的代码或库。因此在 Android 11 上，具有读取外部存储权限的应用程序能够访问范围存储环境中具有文件路径的文件。在 Android 10 设备上，这对范围存储环境中的应用程序不可用，除非它们通过设置 `android:requestLegacyExternalStorage` 清单属性[选择退出。](https://developer.android.com/training/data-storage/use-cases#opt-out-scoped-storage)为确保跨 Android 版本的连续性，如果您的应用以 Android 10 或更高版本为目标，您也应该选择退出。有关详细信息，请参阅[分区存储最佳实践](https://developer.android.com/training/data-storage/use-cases#access-file-paths)。

> 与 Media Store API 相比，文件路径访问的性能如何？

性能实际上取决于确切的用例。对于视频播放等顺序读取，文件路径访问提供与媒体存储相当的性能。但是对于随机读写，使用文件路径可能会慢两倍。为了实现最快和最一致的读写，我们推荐使用 `Media Store API`。

> 与 Android 10 相比，在 Android 11 中使用存储访问框架是否有任何进一步的限制？

针对 Android 11（API 级别 30）并使用存储访问框架的应用程序将无法再授予对目录的访问权限，例如 SD 卡的根目录和下载目录。无论目标 SDK 是什么，Android 11 上的存储访问框架都无法用于获取对` Android/data` 和 `Android/obb` 目录的访问权限。[详细](https://developer.android.com/preview/privacy/storage#file-directory-restrictions)了解这些限制和测试行为的方法。

> 分区存储中的应用程序是否仅限于将文件写入其特定于应用程序的数据目录？

在分区存储中，应用程序可以[将媒体文件贡献](https://developer.android.com/training/data-storage/shared/media#add-item)给媒体商店收藏。`Media Store` 会根据文件类型将文件放入组织良好的文件夹中，例如 DCIM、电影、下载等。对于所有此类文件，应用程序也可以继续通过文件 API 进行访问。操作系统维护一个系统，将应用程序归因于每个媒体存储文件，因此应用程序可以读取/写入它们最初贡献给媒体存储的文件，而无需存储权限。

> Media Store [DATA 列](https://developer.android.com/reference/android/provider/MediaStore.MediaColumns#DATA)已被弃用，使用指南是什么？

在 Android 10 上，作用域存储环境中的应用无法使用文件路径访问文件。为了与此设计保持一致，我们当时弃用了 DATA 列。根据您对使用现有本机代码或库的需求的反馈，Android 11 现在支持分区存储中应用的文件路径访问。因此，DATA 列实际上对某些情况很有用。对于媒体商店的插入和更新，`Scoped Storage` 中的应用程序应使用 `DISPLAY_NAME` 和 `RELATIVE_PATH` 列。他们不能再为此使用 DATA 列。当读取磁盘上存在的文件的媒体存储条目时，DATA 列将具有有效的文件路径，可与文件 API 或 NDK 文件库一起使用。

# 总结

Android11的适配和Android10适配最好一起进行，因为这两个版本相关隐私变更对开发者的影响主要是分区存储相关的适配导致的业务逻辑修改。

> 分区存储的相关适配

1. `Android10`上可以使用`android:requestLegacyExternalStorage`先进行过渡，但过渡的时候我们需要将应用内的一些数据进行相关分区存储的迁移；完成了这个步骤，我们在进行`Android11`适配的时候会更加容易；
2. 在进行`Android11`相关分区存储适配时，应用内的相关媒体操作我们授权存储权限之后还可以是用`File API`。这个基本上就解决了大部分的分区存储的适配问题。而我们在`Android10`适配的时候又把相关的数据文件迁移到了应用的私有空间也可以通过`File API`进行文件访问。
3. 最后呢！我们将一些拍照、适配录制和图片保存，以及一些对外分享的业务进行相关修改就行。

另外国外经过多次的app合规整改之后，我们大部分开发者只需要侧重的是**来自后台的自定义消息框被屏蔽**、**APK签名方案V2**、**后台位置信息访问权限**以及**电话号码**这些修改。其他的**无线调试**、**设备到设备文件传输**、**限制对 APN 数据库的读取访问**、**在元数据文件中声明“无障碍”按钮使用情况**等其他的Android11的修改，大家可以参考[以 Android 11 为目标平台的应用的行为变更](https://developer.android.com/about/versions/11/behavior-changes-11)。

参考文章：[拖不得了，Android11真的要来了，最全适配实践指南奉上](https://juejin.cn/post/6860370635664261128)
