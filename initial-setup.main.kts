@file:Suppress("ComplexCondition")

import java.io.File
import java.nio.file.Paths

val argsMap: Map<String, String> =
    args.toList().associate { arg ->
        arg.split("=").run {
            first().filterNot(Char::isWhitespace) to
                last().dropWhile(Char::isWhitespace).dropLastWhile(Char::isWhitespace)
        }
    }

val String.placeholder
    get() = """"{{ $this }}""""

println("ARGS: \n${argsMap.toList().joinToString("\n"){ (key, value) -> "ARG: $key = $value"}}")

val currentDir: File = Paths.get("").toAbsolutePath().toFile()

println("FILE: ${currentDir.path}")

currentDir.walkTopDown().forEach { file ->
    if (file.isFile &&
            file.name != "empty.file" &&
            file.name != "initial-setup.main.kts" &&
            file.name != "initial-setup.yaml" &&
            file.name != "gradlew" &&
            file.name != "gradlew.bat" &&
            file.path.contains("${File.separator}.git${File.separator}").not() &&
            file.path.contains("${File.separator}.gradle${File.separator}").not() &&
            file.path.contains("${File.separator}build${File.separator}").not() &&
            file.path.contains("${File.separator}gradle${File.separator}wrapper${File.separator}").not() &&
            file.path.contains("${File.separator}.idea${File.separator}").not()
    ) {
        println("CHECKING FILE: $file...")
        val newContent =
            file.readLines().joinToString("\n") { line ->
                val key = argsMap.keys.firstOrNull { key -> line.contains(key.placeholder) }
                when {
                    key != null -> {
                        println("KEY FOUND: $key")
                        line.replace(key.placeholder, argsMap[key]!!)
                            .replace("#TODO: ", "")
                            .replace("# TODO: ", "")
                            .replace("//TODO: ", "")
                            .replace("// TODO: ", "")
                    }
                    listOf(
                        """#TODO: Uncomment"{{""",
                        """# TODO: Uncomment"{{""",
                        """//TODO: Uncomment"{{""",
                        """// TODO: Uncomment"{{""",
                    )
                        .any { line.contains(it) } -> {
                        println("UNCOMMENT FOUND: $line")
                        line.replace("""#TODO: Uncomment"{{ """, "")
                            .replace("""# TODO: Uncomment"{{ """, "")
                            .replace("""//TODO: Uncomment"{{ """, "")
                            .replace("""// TODO: Uncomment"{{ """, "")
                            .replace(""" }}"""", "")
                    }
                    else -> line
                }
            }
        file.writeText(newContent + "\n")
    }
}

File(".github/workflows/initial-setup.yaml").delete()

File(".github/workflows/build.yaml").delete()

File(".github/workflows/publish.yaml").delete()

File("initial-setup.main.kts").delete()

File("README.md").writeText("# ${argsMap["name"]}\n")
