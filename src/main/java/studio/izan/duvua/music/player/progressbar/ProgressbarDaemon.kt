package studio.izan.duvua.music.player.progressbar

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import studio.izan.duvua.music.types.SEmbedBuilder
import studio.izan.duvua.music.utils.parseMsIntoStringForm

class ProgressbarDaemon(
    public val channel: TextChannel,
    public val track: AudioTrack,
): Runnable {

    private val trackProgressbar: TrackProgressbar = TrackProgressbar(track)

    private val logger: Logger = LoggerFactory.getLogger("ProgressbarDaemon");

    private val interval = 2000L

    private val info = track.info

    private fun makeEmbed(bar: String): MessageEmbed {
        val description = "**Música** [" + info.title + "]("+
                info.uri + ") **de** " + info.author + "\n\n" +
                bar + "\n\n" + "**Duração: [" + parseMsIntoStringForm(track.duration) + "]**"

        return SEmbedBuilder()
            .setDescription(description)
            .build()
    }

    private lateinit var message: Message

    init {
        channel.sendMessageEmbeds(makeEmbed(trackProgressbar.getStringBar()))
            .queue {msg ->
                run {
                    this.message = msg
                }
            }
    }

    override fun run() {
        var mustEnd = false
        while (true) {
            Thread.sleep(interval)
            try {
                val progressbar = trackProgressbar.getStringBar()
                message.editMessageEmbeds(makeEmbed(progressbar)).queue()
            } catch (err: Error) {
                mustEnd = true
                logger.error(err.message)
            }
            if (trackProgressbar.percentage == 100F) mustEnd = true
            if (mustEnd) {
                logger.info("Loop ended")
                return
            }
        }
    }
}