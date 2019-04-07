package space.anity

import com.fizzed.rocker.*
import com.fizzed.rocker.runtime.*
import io.javalin.*
import io.javalin.Handler
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.util.*
import io.javalin.rendering.*
import io.javalin.rendering.template.TemplateUtil.model
import io.javalin.security.*
import io.javalin.security.SecurityUtil.roles
import java.io.*
import java.nio.file.*
import java.util.logging.*

const val fileHome = "files"
val databaseController = DatabaseController()
private val log = Logger.getLogger("App.kt")

fun main() {
    val app = Javalin.create()
        .enableStaticFiles("../resources/")
        .accessManager { handler, ctx, permittedRoles -> setupRoles(handler, ctx, permittedRoles) }
        .start(7000)

    // Set up templating
    RockerRuntime.getInstance().isReloading = true
    JavalinRenderer.register(
        FileRenderer { filepath, model -> Rocker.template(filepath).bind(model).render().toString() }, ".rocker.html"
    )

    // Only for testing purposes
    databaseController.createUser("melvin", "supersecure", "ADMIN")

    app.routes {
        /**
         * Sends a json object of filenames in [fileHome]s
         * TODO: Fix possible security issue with "../"
         */
        get("/files/*", { ctx -> crawlFiles(ctx) }, roles(Roles.ADMIN))

        /**
         * Renders the upload rocker template
         */
        get("/upload", { ctx -> ctx.render("upload.rocker.html") }, roles(Roles.USER))

        /**
         * Receives and saves multipart media data
         * TODO: Fix possible security issue with "../"
         */
        post("/upload", { ctx -> upload(ctx) }, roles(Roles.ADMIN))
    }
}

/**
 * Sets up the roles with the database and declares the handling of roles
 */
fun setupRoles(handler: Handler, ctx: Context, permittedRoles: Set<Role>) {
    val userRole = databaseController.getRole("melvin")
    when {
        permittedRoles.contains(userRole) -> handler.handle(ctx)
        ctx.host()!!.contains("localhost") -> handler.handle(ctx)
        else -> ctx.status(401).json("This site isn't available for you.")
    }
}

/**
 * Crawls the requested directory and returns filenames in array
 */
fun crawlFiles(ctx: Context) {
    val files = ArrayList<String>()
    try {
        if (File("$fileHome/${ctx.splats()[0]}").isDirectory) {
            Files.list(Paths.get("$fileHome/${ctx.splats()[0]}/")).forEach {
                val fileName = it.toString()
                    .drop(fileHome.length + (if (ctx.splats()[0].isNotEmpty()) ctx.splats()[0].length + 2 else 1))
                val filePath = "$fileHome${it.toString().drop(fileHome.length)}"
                files.add(if (File(filePath).isDirectory) "$fileName/" else fileName)
                ctx.render("files.rocker.html", model("files", files))
            }
        } else
            ctx.render(
                "fileview.rocker.html", model(
                    "content", Files.readAllLines(
                        Paths.get("$fileHome/${ctx.splats()[0]}"),
                        Charsets.UTF_8
                    ).joinToString(separator = "\n"),
                    "filename", File("$fileHome/${ctx.splats()[0]}").name,
                    "extension", File("$fileHome/${ctx.splats()[0]}").extension
                )
            )
    } catch (_: java.nio.file.NoSuchFileException) {
        throw NotFoundResponse("Error: File or directory does not exist.")
    }
}

/**
 * Saves multipart media data into requested directory
 */
fun upload(ctx: Context) {
    ctx.uploadedFiles("files").forEach { (contentType, content, name, extension) ->
        if (ctx.queryParam("dir") !== null) {
            FileUtil.streamToFile(content, "files/${ctx.queryParam("dir")}/$name")
            ctx.redirect("/views/upload.rocker.html")
        } else
            throw BadRequestResponse("Error: Please enter a filename.")
    }
}

/**
 * Declares the roles in which a user can be in
 */
enum class Roles : Role {
    ADMIN, USER, GUEST
}
