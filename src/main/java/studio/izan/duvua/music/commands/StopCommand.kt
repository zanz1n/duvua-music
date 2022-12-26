package studio.izan.duvua.music.commands

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.Logger
import studio.izan.duvua.music.DuvuaMusic
import studio.izan.duvua.music.types.ICommandBase

class StopCommand(private val logger: Logger): ICommandBase {
    override val name: String
        get() = "stop"

    override val description: String
        get() = "Para de tocar a música e limpa a fila de reprodução"

    override val options: List<OptionData>?
        get() = null

    override fun run(interaction: SlashCommandEvent, client: DuvuaMusic) {
    }
}