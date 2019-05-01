package space.anity

import com.fizzed.rocker.*
import com.fizzed.rocker.runtime.*
import io.javalin.*
import io.javalin.Handler
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.rendering.*
import io.javalin.rendering.template.TemplateUtil.model
import io.javalin.security.*
import io.javalin.security.SecurityUtil.roles
import java.net.*
import java.util.logging.*

const val fileHome = "files"
val databaseController = DatabaseController()
val userHandler = UserHandler()
val fileController = FileController()
private val log = Logger.getLogger("App.kt")

fun main() {
    val app = Javalin.create().apply {
        port(7000)
        accessManager { handler, ctx, permittedRoles -> roleManager(handler, ctx, permittedRoles) }
    }.start()

    // Set up templating
    RockerRuntime.getInstance().isReloading = false
    JavalinRenderer.register(
        FileRenderer { filepath, model -> Rocker.template(filepath).bind(model).render().toString() }, ".rocker.html"
    )

    databaseController.initDatabase()

    app.routes {
        /**
         * Normalizes and cleans the requested url
         */
        before("/*") { ctx ->
            if (URI(ctx.url()).normalize().toString() != ctx.url()) ctx.redirect(URI(ctx.url()).normalize().toString())
        }

        /**
         * Renders the static resources (important for deployed jar files)
         */
        get(
            "/css/*", { ctx ->
                ctx.contentType("text/css")
                ctx.result(Thread.currentThread().contextClassLoader.getResourceAsStream("css/" + ctx.splat(0)))
            },
            roles(Roles.GUEST)
        )
        get(
            "/js/*", { ctx ->
                ctx.contentType("text/js")
                ctx.result(Thread.currentThread().contextClassLoader.getResourceAsStream("js/" + ctx.splat(0)))
            },
            roles(Roles.GUEST)
        )
        get(
            "/fonts/*", { ctx ->
                ctx.result(Thread.currentThread().contextClassLoader.getResourceAsStream("fonts/" + ctx.splat(0)))
            },
            roles(Roles.GUEST)
        )

        /**
         * Main page
         */
        get(
            "/",
            { ctx ->
                ctx.render(
                    "index.rocker.html",
                    model("username", databaseController.getUsername(userHandler.getVerifiedUserId(ctx)))
                )
            },
            roles(Roles.GUEST)
        )

        /**
         * Renders the login page
         */
        get("/login", { ctx ->
            if (userHandler.getVerifiedUserId(ctx) > 0 || !databaseController.isSetup()) ctx.redirect("/")
            else ctx.render(
                "login.rocker.html",
                model("message", "", "counter", 0)
            )
        }, roles(Roles.GUEST))

        /**
         * Endpoint for user authentication
         */
        post("/login", userHandler::login, roles(Roles.GUEST))

        /**
         * Logs the user out
         */
        get("/logout", userHandler::logout, roles(Roles.USER))

        /**
         * Renders the setup page (only on initial use)
         */
        get("/setup", { ctx ->
            if (databaseController.isSetup()) ctx.redirect("/login")
            else ctx.render(
                "setup.rocker.html",
                model("message", "")
            )
        }, roles(Roles.GUEST))

        /**
         * Endpoint for setup (only on initial use)
         */
        post("/setup", userHandler::setup, roles(Roles.GUEST))

        /**
         * Renders the file list view
         */
        get("/files/*", fileController::crawl, roles(Roles.USER))

        /**
         * Receives and saves multipart media data
         */
        post("/upload/*", fileController::upload, roles(Roles.USER))

        /**
         * Deletes file
         */
        post("/delete/*", fileController::delete, roles(Roles.USER))

        /**
         * Shares file
         */
        post("/share/*", fileController::share, roles(Roles.USER))

        /**
         * Shares file in directory
         */
        post("/share", fileController::handleSharedFile, roles(Roles.GUEST))

        /**
         * Shows the shared file
         */
        get("/shared", fileController::renderShared, roles(Roles.GUEST))
    }
}

/**
 * Sets up the roles with the database and declares the handling of roles
 */
fun roleManager(handler: Handler, ctx: Context, permittedRoles: Set<Role>) {
    when {
        userHandler.getVerifiedUserId(ctx) == ctx.cookieStore("userId") ?: "userId" -> handler.handle(ctx)
        databaseController.getRoles(userHandler.getVerifiedUserId(ctx)).any { it in permittedRoles } -> handler.handle(
            ctx
        )
        //ctx.host()!!.contains("localhost") -> handler.handle(ctx) // DEBUG
        else -> ctx.status(401).redirect("/login")
    }
}

/**
 * Declares the roles in which a user can be in
 */
enum class Roles : Role {
    ADMIN, USER, GUEST
}
