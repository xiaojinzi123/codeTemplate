import java.io.*

val FILE_USECASE: InputStream
    get() = Thread.currentThread().contextClassLoader.getResourceAsStream("UseCase")!!
val FILE_VIEWMODEL: InputStream
    get() = Thread.currentThread().contextClassLoader.getResourceAsStream("ViewModel")!!
val FILE_ACTIVITY: InputStream
    get() = Thread.currentThread().contextClassLoader.getResourceAsStream("Activity")!!
val FILE_VIEWS: InputStream
    get() = Thread.currentThread().contextClassLoader.getResourceAsStream("Views")!!
val FILE_VOS: InputStream
    get() = Thread.currentThread().contextClassLoader.getResourceAsStream("VOS")!!

fun main(args: Array<String>) {

    if (args.size != 1) {
        throw IllegalArgumentException("name is invalid")
    }

    if (args[0].isEmpty()) {
        throw IllegalArgumentException("name is invalid")
    }

    val targetName = args[0]
    // 获取命令调用的目录
    val targetFolderPath = InputStreamReader(Runtime.getRuntime().exec("pwd").inputStream).readText()

    val folderFile = File(targetFolderPath)
    if (!folderFile.exists()) {
        throw IllegalArgumentException("folder is not exist")
    }
    // 寻找最后出现 java 或者 kotlin 的目录
    var tempFile = folderFile
    while (tempFile.name != "java" && tempFile.name != "kotlin") {
        // println("name = ${tempFile.name}")
        tempFile = tempFile.parentFile
    }
    val targetSubPath = folderFile.path.removeRange(startIndex = 0, tempFile.path.length + 1)
    // println("targetSubPath = $targetSubPath")
    val rootPackageName = targetSubPath.replace(oldChar = '/', newChar = '.')
    println("rootPackageName = $rootPackageName")

    createUseCase(
        rootPackageName = rootPackageName,
        name = targetName,
        rootFolderFile = File(targetFolderPath)
    )
    createViewModel(
        rootPackageName = rootPackageName,
        name = targetName,
        rootFolderFile = File(targetFolderPath)
    )
    createActivity(
        rootPackageName = rootPackageName,
        name = targetName,
        rootFolderFile = File(targetFolderPath)
    )
    createViews(
        rootPackageName = rootPackageName,
        name = targetName,
        rootFolderFile = File(targetFolderPath)
    )
    createVOS(
        rootPackageName = rootPackageName,
        name = targetName,
        rootFolderFile = File(targetFolderPath)
    )
    println("工作完成了")

}

/**
 * @param name 名字必须是首字母大写的
 */
fun createUseCase(rootPackageName: String, name: String, rootFolderFile: File) {
    val targetName = toJavaName(name = name)
    val targetFolder = File(rootFolderFile, File(name, "domain").path)
    if (!targetFolder.exists()) {
        targetFolder.mkdirs().run {
            if (!this) {
                throw IllegalArgumentException("create folder fail: ${targetFolder.path}")
            }
        }
    }
    val targetFile = File(targetFolder, "${targetName}UseCase.kt")
    createFile(targetFile, FILE_USECASE, name, targetName, rootPackageName)
}

fun createViewModel(rootPackageName: String, name: String, rootFolderFile: File) {
    val targetName = toJavaName(name = name)
    val targetFolder = File(rootFolderFile, File(name, "view").path)
    if (!targetFolder.exists()) {
        targetFolder.mkdirs().run {
            if (!this) {
                throw IllegalArgumentException("create folder fail: ${targetFolder.path}")
            }
        }
    }
    val targetFile = File(targetFolder, "${targetName}ViewModel.kt")
    createFile(targetFile, FILE_VIEWMODEL, name, targetName, rootPackageName)
}

fun createActivity(rootPackageName: String, name: String, rootFolderFile: File) {
    val targetName = toJavaName(name = name)
    val targetFolder = File(rootFolderFile, File(name, "view").path)
    if (!targetFolder.exists()) {
        targetFolder.mkdirs().run {
            if (!this) {
                throw IllegalArgumentException("create folder fail: ${targetFolder.path}")
            }
        }
    }
    val targetFile = File(targetFolder, "${targetName}Act.kt")
    createFile(targetFile, FILE_ACTIVITY, name, targetName, rootPackageName)
}

fun createViews(rootPackageName: String, name: String, rootFolderFile: File) {
    val targetName = toJavaName(name = name)
    val targetFolder = File(rootFolderFile, File(name, "view").path)
    if (!targetFolder.exists()) {
        targetFolder.mkdirs().run {
            if (!this) {
                throw IllegalArgumentException("create folder fail: ${targetFolder.path}")
            }
        }
    }
    val targetFile = File(targetFolder, "${targetName}Views.kt")
    createFile(targetFile, FILE_VIEWS, name, targetName, rootPackageName)
}

fun createVOS(rootPackageName: String, name: String, rootFolderFile: File) {
    val targetName = toJavaName(name = name)
    val targetFolder = File(rootFolderFile, File(name, "view").path)
    if (!targetFolder.exists()) {
        targetFolder.mkdirs().run {
            if (!this) {
                throw IllegalArgumentException("create folder fail: ${targetFolder.path}")
            }
        }
    }
    InputStreamReader(FILE_VOS)
    val targetFile = File(targetFolder, "${targetName}VOS.kt")
    createFile(targetFile, FILE_VOS, name, targetName, rootPackageName)
}

private fun createFile(
    targetFile: File,
    targetSourceFileIns: InputStream,
    name: String,
    targetName: String,
    rootPackageName: String
) {
    val fileOut = BufferedWriter(FileWriter(targetFile))
    InputStreamReader(targetSourceFileIns).use { fileIn ->
        val result = fileIn
            .readText()
            .replace(oldValue = "{NAME}", newValue = name)
            .replace(oldValue = "{JAVA_NAME}", newValue = targetName)
            .replace(oldValue = "{ROOT_PACKAGE_NAME}", newValue = rootPackageName)
        fileOut.use { fileOut ->
            fileOut.write(result)
        }
    }
}

fun toJavaName(name: String): String {
    return name
        .split("_")
        .joinToString(separator = "") {
            it
                .mapIndexed { index, c ->
                    if (index == 0) {
                        c.uppercaseChar()
                    } else {
                        c
                    }
                }
                .joinToString(separator = "")
        }
}