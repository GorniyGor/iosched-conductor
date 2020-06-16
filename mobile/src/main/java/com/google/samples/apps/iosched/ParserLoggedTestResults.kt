import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths

object ParserLoggedTestResults {
    val SOURCE_PATHs = listOf(
        "/Users/e.mikhalchenko/Desktop/Thesis/Device/Meizu M5/UiAutomator",
        "/Users/e.mikhalchenko/Desktop/Thesis/Device/Meizu M5/Espresso"
    )

    @JvmStatic fun main(args: Array<String>) {
        SOURCE_PATHs.forEach { SOURCE_PATH ->
            val directory = File(SOURCE_PATH)
            val files =
                directory.list { dir, name -> !name.startsWith("Results") && name.endsWith(".txt") }

            PrintWriter("$SOURCE_PATH/Results.txt", "UTF-8").use { writer ->
                files?.forEach {
                    val fileStrings = Files.readAllLines(Paths.get("$SOURCE_PATH/$it"))
                    val list = fileStrings.map {
                        it.split(" ").find {
                            it.contains("ms")
                        }?.replace("ms","")
                    }.filterNotNull()
                    writer.println(it.split("/").last() + "; size: ${list.size}")
                    writer.println(list)
                    writer.println()
                }
            }
        }
    }
}