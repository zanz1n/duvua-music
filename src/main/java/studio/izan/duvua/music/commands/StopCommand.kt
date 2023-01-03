package studio.izan.duvua.music.commands

import net.dv8tion.jda.api.interactions.Interaction
import org.slf4j.Logger
import studio.izan.duvua.music.DuvuaMusic
import studio.izan.duvua.music.player.PlayerManager
import studio.izan.duvua.music.types.IButtonIntegrableCommandBase
import studio.izan.duvua.music.types.SEmbedBuilder
import studio.izan.duvua.music.utils.mention

class StopCommand(private val logger: Logger): IButtonIntegrableCommandBase {
    override val name: String
        get() = "stop"

    override fun run(interaction: Interaction, client: DuvuaMusic) {
        if (interaction.guild == null) return
        if (interaction.member == null) return

        val memberProvider = client.dba.getMember(interaction.member!!)

        if (!memberProvider.isDj()) {
            val embed = SEmbedBuilder.createDefault("Você não tem permissão para usar" +
                    " esse comando, ${mention(interaction.user)}")
            interaction.replyEmbeds(embed).queue()
            return
        }

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

        val embed = SEmbedBuilder.createDefault("A playlist foi limpa por ${mention(interaction.user)}")

        val reply = interaction.replyEmbeds(embed)

        musicManager.scheduler.audioPlayer.stopTrack()
        musicManager.scheduler.queue.clear()

        reply.queue()
    }
}