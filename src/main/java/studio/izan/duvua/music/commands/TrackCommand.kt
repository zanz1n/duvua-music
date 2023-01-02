package studio.izan.duvua.music.commands

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionType
import org.slf4j.Logger
import studio.izan.duvua.music.DuvuaMusic
import studio.izan.duvua.music.player.PlayerManager
import studio.izan.duvua.music.player.progressbar.ProgressBarManager
import studio.izan.duvua.music.types.IButtonIntegrableCommandBase
import studio.izan.duvua.music.types.SEmbedBuilder
import studio.izan.duvua.music.utils.mention

class TrackCommand(private val logger: Logger): IButtonIntegrableCommandBase {
    override val name: String
        get() = "track"

    override fun run(interaction: Interaction, client: DuvuaMusic) {
        if (interaction.guild == null) return
        if (interaction.member == null) return

        val vc = interaction.member?.voiceState?.channel;

        if (vc == null) {
            val embed = SEmbedBuilder.createDefault("Você precisa estar em um canal de voz para" +
                    "usar esse comando, ${mention(interaction.user)}")
            interaction.replyEmbeds(embed).queue()
            return
        }

        val musicManager = PlayerManager.getInstance().getMusicManager(interaction.guild)

        if (interaction.guild!!.selfMember.voiceState?.inVoiceChannel() != true
            || musicManager.audioPlayer.playingTrack == null) {

            val embed = SEmbedBuilder.createDefault("Não tem nenhum som na playlist, ${mention(interaction.user)}")
            interaction.replyEmbeds(embed).queue()
            return
        }

        ProgressBarManager.getInstance().create(musicManager.audioPlayer.playingTrack, interaction.textChannel)

        interaction.replyEmbeds(SEmbedBuilder.createDefault("OK")).queue()
    }
}