import java.io.*
import java.util.UUID

fun main(args: Array<String>) {

    /*if (args.size != 2) {
        throw IllegalArgumentException("parameter count must be 2")
    }*/

    // 获取命令被调用的目录
    // val targetFolderPath = InputStreamReader(Runtime.getRuntime().exec("pwd").inputStream).readText().trim()
    val targetFolderPath = "/Users/hhkj/Documents/code/java/github/mvvmAndroid/src/main/kotlin/module/test"
    val targetFolder = File(targetFolderPath)
    // 获取命令的目录
    val commandFolderPath = System.getProperty("user.dir")
    val templateFolder = File(commandFolderPath, "template")
    val cacheFolder = File(commandFolderPath, "cache")

    // 获取模板的名称
    val template = "test"
    val name = "user_detail"
    val javaName = toJavaName(name = name)

    val targetCreateFolder = File(targetFolder, name)
    val targetTemplateFolder = File(templateFolder, template)

    println("targetFolderPath = $targetFolderPath")
    println("targetFolder = ${targetFolder.path}")
    println("user.dir = $commandFolderPath")
    println("templateFolderPath = ${templateFolder.path}")
    println("cacheFolder = ${cacheFolder.path}")
    println("template = $template")
    println("targetCreateFolder = $targetCreateFolder")
    println("targetTemplateFolder = $targetTemplateFolder")
    println("name = $name")
    println("javaName = $javaName")

    if (targetCreateFolder.exists()) {
        throw RuntimeException("folder '${targetCreateFolder.path}' is exist!!!")
    }
    val isCopySuccess = targetTemplateFolder.copyRecursively(target = targetCreateFolder, overwrite = true)

    if (!isCopySuccess) {
        throw RuntimeException("fail to copy template to folder '${targetCreateFolder.path}'")
    }

    println("isCopySuccess = $isCopySuccess")

    var rootFile = File(targetFolderPath)
    while (rootFile.name != "java" && rootFile.name != "kotlin") {
        // println("name = ${tempFile.name}")
        rootFile = rootFile.parentFile
    }

    val rootPackageName = targetFolderPath
        .removeRange(startIndex = 0, rootFile.path.length + 1)
        .replace(oldChar = '/', newChar = '.')

    println("rootFile = $rootFile")
    println("rootPackageName = $rootPackageName")

    // 进行文件的内容替换
    replaceContent(
        file = targetCreateFolder,
        name = name,
        javaName = javaName,
        rootPackageName = rootPackageName,
    )

}

private fun toJavaName(name: String): String {
    return name
        .split("_")
        .joinToString(separator = "") {
            it.mapIndexed { index, c ->
                if (index == 0) {
                    c.uppercaseChar()
                } else {
                    c
                }
            }.joinToString(separator = "")
        }
}

private fun replaceContent(file: File, name: String, javaName: String, rootPackageName: String) {
    if (file.isFile) {
        val originalName = file.name
        val outputFile = File(file.parentFile, "${originalName}_${UUID.randomUUID()}")
        file.inputStream().use {
            val fileOut = BufferedWriter(FileWriter(outputFile))
            InputStreamReader(file.inputStream()).use { fileIn ->
                val result = fileIn
                    .readText()
                    .replace(oldValue = "{NAME}", newValue = name)
                    .replace(oldValue = "{JAVA_NAME}", newValue = javaName)
                    .replace(oldValue = "{ROOT_PACKAGE_NAME}", newValue = rootPackageName)
                fileOut.use { fileOut ->
                    fileOut.write(result)
                }
            }
        }
        file.delete()
        outputFile.renameTo(file)
    } else {
        file.listFiles().forEach {
            replaceContent(
                file = it, name = name, javaName = javaName, rootPackageName = rootPackageName,
            )
        }
    }
}