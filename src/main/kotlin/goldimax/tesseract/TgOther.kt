package goldimax.tesseract

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.jcabi.manifests.Manifests
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

object TgOther {
    init {
        with(UniBot.tg) {
            onCommand("/rainbow") { msg, _ ->
                val version = try {
                    Manifests.read("Version")
                } catch (e: IllegalArgumentException) {
                    "Unknown"
                }

                sendMessage(
                    msg.chat.id,
                    """
            Copy. I am online.
            You are ${msg.from!!.id}.
            You are${if (SUManager.isSuperuser(TGUser(msg.from!!.id.toLong()))) "" else " not"} superuser.
            Here is ${msg.chat.id}.
            Build: $version.
            """.trimIndent()
                , parseMode = "MarkdownV2")
            }

            onCommand("/connect") { msg, cmd ->
                error(msg) {
                    testSu(msg)

                    Connections.connect.add(Connection(cmd!!.trim().toLong(), msg.chat.id))
                    Connections.save()

                    sendMessage(msg.chat.id, "Done.", replyTo = msg.message_id)
                }
            }

            onCommand("/hitokoto") { msg, _ ->
                val json = Parser.default().parse(
                    URL("https://v1.hitokoto.cn/")
                        .openStream()
                ) as JsonObject
                sendMessage(msg.chat.id, "「${
                    json.string("hitokoto")}」 —— ${
                    json.string("from")}")
            }

            onCommand("/disconnect") { msg, _ ->
                error(msg) {
                    testSu(msg)

                    Connections.connect.removeIf { it.tg == msg.chat.id }
                    Connections.save()

                    sendMessage(msg.chat.id, "Done.", replyTo = msg.message_id)
                }
            }
        }
    }
}
