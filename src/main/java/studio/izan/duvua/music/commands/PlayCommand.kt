package studio.izan.duvua.music.commands

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.Logger
import studio.izan.duvua.music.DuvuaMusic
import studio.izan.duvua.music.types.ICommandBase

class PlayCommand(private val logger: Logger): ICommandBase {
    override val name: String
        get() = "play"

    override val description: String
        get() = "Toca uma música do youtube"

    override val options: List<OptionData>
        get() = arrayListOf(
            OptionData(
                OptionType.STRING,
                "song",
                "A url ou o nome do som que deseja tocar",
                true
            )
        )

    override fun run(interaction: SlashCommandEvent, client: DuvuaMusic) {
        if (interaction.guild == null) return
        val statement = client.dba.connection
            .prepareStatement("SELECT \"musicStrictM\" FROM \"Guild\" WHERE \"dcId\" = (?);")
        val song = interaction.options.find { opt -> opt.name == "song" }?.asString

        logger.info(song)
        statement.setString(1, interaction.guild!!.id)
        val result = statement.executeQuery()
        logger.info(result.toString())
    }
}