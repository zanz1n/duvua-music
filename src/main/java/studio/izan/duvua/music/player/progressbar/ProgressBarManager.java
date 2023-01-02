package studio.izan.duvua.music.player.progressbar;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ProgressBarManager {
    private static ProgressBarManager INSTANCE;

    private final Map<String, Thread> progressDaemons;

    private ProgressBarManager() {
        progressDaemons = new HashMap<>();
    }

    public boolean create(AudioTrack track, TextChannel channel) {
        final String guildId = channel.getGuild().getId();
        final Thread progressbarDaemon = progressDaemons.get(guildId);
        if (progressbarDaemon == null || !progressbarDaemon.isAlive()) {
            final Thread progressbarDaemonNew = new Thread(new ProgressbarDaemon(channel, track));
            progressDaemons.put(guildId, progressbarDaemonNew);
            progressbarDaemonNew.start();
            return true;
        }
        return false;
    }

    public static ProgressBarManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProgressBarManager();
        }
        return INSTANCE;
    }
}