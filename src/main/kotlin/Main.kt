import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.lang.management.ManagementFactory
import java.util.*

fun main(args: Array<String>) {

    if (args.size != 2) {
        throw IllegalArgumentException("parameter count must be 2")
    }

    // 是否是 Windows
    val isWindows = System.getProperty("os.name").contains("Windows")
    println("isWindows = $isWindows")

    val pathSeparator = if(isWindows) {
        "\\"
    } else {
        "/"
    }

    val targetFolderPath = if (isWindows) {
        System.getProperty("user.dir")
    } else {
        InputStreamReader(Runtime.getRuntime().exec("pwd").inputStream).readText().trim()
    }
    println("targetFolderPath = $targetFolderPath")

    val targetFolder = File(targetFolderPath)
    val tempKey = "codeTemplate${pathSeparator}lib${pathSeparator}codeTemplate"

    val classPath = ManagementFactory
        .getRuntimeMXBean()
        .classPath

    val classPathArray = classPath
        .split(if (isWindows) ";" else ":")
    val tempClassPath = classPathArray
        .first { it.contains(other = tempKey) }

    // 获取命令的目录
    val commandFolderPath = tempClassPath.substring(
        startIndex = 0,
        endIndex = tempClassPath.indexOf(string = tempKey),
    )

    val commandFolder = File(commandFolderPath)
    val templateFolder = File(commandFolder, "codeTemplates")
    if (!templateFolder.exists()) {
        templateFolder.mkdirs()
    }

    // 获取模板的名称
    val template = args[0]
    val name = args[1]
    val javaName = toJavaName(name = name)

    val targetCreateFolder = File(targetFolder, name)
    val targetTemplateFolder = File(templateFolder, template)

    println("targetFolder = ${targetFolder.path}")
    println("commandFolderPath = $commandFolderPath")
    println("templateFolderPath = ${templateFolder.path}")
    println("template = $template")
    println("targetCreateFolder = $targetCreateFolder")
    println("targetTemplateFolder = $targetTemplateFolder")
    println()
    println("name = $name")
    println("javaName = $javaName")

    if (!targetTemplateFolder.exists()) {
        throw RuntimeException("template '$template' is not exist!!!")
    }

    if (targetCreateFolder.exists()) {
        throw RuntimeException("folder '${targetCreateFolder.path}' is exist!!!")
    }
    val isCopySuccess = targetTemplateFolder.copyRecursively(target = targetCreateFolder, overwrite = true)

    if (!isCopySuccess) {
        throw RuntimeException("fail to copy template to folder '${targetCreateFolder.path}'")
    }

    var rootFile = File(targetFolderPath)
    while (rootFile.name != "java" && rootFile.name != "kotlin") {
        // println("name = ${tempFile.name}")
        rootFile = rootFile.parentFile
    }

    val rootPackageName = targetFolderPath
        .removeRange(startIndex = 0, rootFile.path.length + 1)
        .replace(oldValue = pathSeparator, newValue = ".")

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
        val parentFile = file.parentFile
        val originalName = file.name
        val outputFile = File(parentFile, "${originalName}_${UUID.randomUUID()}")
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
        outputFile.renameTo(
            File(
                parentFile,
                originalName
                    .replace(oldValue = "{NAME}", newValue = name)
                    .replace(oldValue = "{JAVA_NAME}", newValue = javaName)
                    .replace(oldValue = "{ROOT_PACKAGE_NAME}", newValue = rootPackageName)
            )
        )
    } else {
        file.listFiles()?.forEach {
            replaceContent(
                file = it, name = name, javaName = javaName, rootPackageName = rootPackageName,
            )
        }
    }
}