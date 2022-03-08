import java.io.*

val PARAMETER_NAME_TEMPLATE = "template"
val PARAMETER_NAME_NAME = "name"

fun main(args: Array<String>) {

    val targetArgs = arrayOf("template=xxx", "ghhhh")

    println("targetArgs = ${targetArgs.joinToString()}")

    if (targetArgs[0].isEmpty()) {
        throw IllegalArgumentException("name is invalid")
    }

    var template: String? = null
    var name: String? = null

    targetArgs
        .filter { "=" in it }
        .forEach {
            val index = it.indexOf('=')
            val parameterName = it.substring(startIndex = 0, endIndex = index)
            val parameterValue = it.substring(startIndex = index + 1)
            println("parameterName = $parameterName, parameterValue = $parameterValue")
            when (parameterName) {
                PARAMETER_NAME_TEMPLATE -> template = template ?: parameterValue
                PARAMETER_NAME_NAME -> name = name ?: parameterValue
            }
        }

    targetArgs
        .filter { "=" !in it }
        .forEachIndexed { index, item ->
            println("index = $index, item = $item")
            when (index) {
                0 -> template = template ?: item
                1 -> name = name ?: item
            }
        }

    if (template.isNullOrEmpty() || name.isNullOrEmpty()) {
        throw IllegalArgumentException()
    }

    println("template = $template, name = $name")

    // 获取命令调用的目录
    val targetFolderPath = InputStreamReader(Runtime.getRuntime().exec("pwd").inputStream).readText().trim()

    println("targetFolderPath = $targetFolderPath")

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