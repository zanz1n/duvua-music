package studio.izan.duvua.music.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.izan.duvua.music.types.SEmbedBuilder;
import studio.izan.duvua.music.utils.DefaultButtons;
import studio.izan.duvua.music.utils.TimeParsersKt;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    private String getThumbnailByUri(String uri) {
        final String[] splited = uri.split("/");
        return "https://img.youtube.com/vi/" +
                splited[splited.length -1].replace("watch?v=", "") +
                "/default.jpg"
                ;
    }

    public void loadAndPlay(TextChannel textChannel, String trackUrl, Member member) {
        final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());
        musicManager.setMessageChannel(textChannel);
        musicManager.getScheduler().setMessageChannel(textChannel);

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.getScheduler().enqueue(audioTrack);
                final AudioTrackInfo info = audioTrack.getInfo();
                final String description = "**Música** [" + info.title + "]("+
                        info.uri + ") **de** " + info.author + " **adicionada à playlist**\n\n" +
                        "**Duração: [" + TimeParsersKt.parseMsIntoStringForm(audioTrack.getDuration()) + "]**";
                String userName = member.getNickname();
                if (userName == null) {
                    userName = member.getUser().getName();
                }

                String avatarUrl = member.getAvatarUrl();
                if (avatarUrl == null) {
                    avatarUrl = member.getUser().getAvatarUrl();
                }

                MessageEmbed embed = new SEmbedBuilder()
                        .setDescription(description)
                        .setThumbnail(getThumbnailByUri(audioTrack.getInfo().uri))
                        .setFooter("Requisitado por " + userName, avatarUrl)
                        .build();

                Message finalMessage = new MessageBuilder()
                        .setEmbeds(embed)
                        .setActionRows(DefaultButtons.defaultActionRow, DefaultButtons.defaultActionRow2)
                        .build();

                textChannel.sendMessage(finalMessage).queue();
            }

            private void resolveFailedLoad() {
                textChannel.sendMessageEmbeds(
                        SEmbedBuilder.createDefault("Não foi possível achar uma música" +
                                "buscando por`"+
                                trackUrl.replace("ytsearch:", "")
                                +"`")
                ).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack track = audioPlaylist.getSelectedTrack();
                if (track == null) {
                    track = audioPlaylist.getTracks().get(0);
                }
                if (track == null) {
                    resolveFailedLoad();
                    return;
                }
                trackLoaded(track);
            }

            @Override
            public void noMatches() {
                resolveFailedLoad();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                logger.error(e.getMessage());
                resolveFailedLoad();
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}