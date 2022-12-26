package studio.izan.duvua.music.events

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import studio.izan.duvua.music.DuvuaMusic
import studio.izan.duvua.music.commands.PlayCommand
import studio.izan.duvua.music.commands.StopCommand
import studio.izan.duvua.music.types.ICommandBase
import kotlin.concurrent.thread

class SlashCommandListener(private val client: DuvuaMusic): ListenerAdapter() {
    private val logger = LoggerFactory.getLogger("SlashCommandEvent")

    val listeningCommands: List<ICommandBase> = arrayListOf(
        PlayCommand(logger),
        StopCommand(logger)
    )

    override fun onSlashCommand(event: SlashCommandEvent) {
        thread {
            val command = listeningCommands.find { cmd -> cmd.name == event.name }
            command?.run(event, client)
        }
    }
}