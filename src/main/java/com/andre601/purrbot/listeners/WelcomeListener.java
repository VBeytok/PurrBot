package com.andre601.purrbot.listeners;

import com.andre601.purrbot.commands.guild.CmdWelcome;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.DBUtil;
import com.andre601.purrbot.util.ImageUtil;
import com.andre601.purrbot.util.messagehandling.MessageUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.InputStream;

public class WelcomeListener extends ListenerAdapter {

    /**
     * Listens for when a member joins a guild.
     *
     * @param event
     *        A {@link net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent GuildMemberJoinEvent}.
     */
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        long bots = event.getGuild().getMembers().stream().filter(user -> user.getUser().isBot()).count();
        long members = event.getGuild().getMembers().stream().filter(user -> !user.getUser().isBot()).count();

        if(bots > (members + 2)){
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(pm -> {
                //  Try to send a PM with the reason to the guild-owner.
                pm.sendMessage(String.format(
                        "Your Server `%s` (`%s`) has more bots than members!",
                        event.getGuild().getName(),
                        event.getGuild().getId()
                )).queue();
            });
            event.getGuild().leave().queue();
            return;
        }

        if(event.getUser().isBot())
            return;

        Guild guild = event.getGuild();

        if(!DBUtil.getWelcomeChannel(guild).equals("none")){
            TextChannel tc = CmdWelcome.getChannel(guild);

            if(tc != null){
                if(!PermUtil.check(tc, Permission.MESSAGE_WRITE))
                    return;

                String msg = "Welcome {mention}!";

                if(DBUtil.hasMessage(guild))
                    msg = DBUtil.getMessage(guild);

                //  Creating a new message with the MessageBuilder
                Message welcome = new MessageBuilder()
                        .append(MessageUtil.formatPlaceholders(msg, guild, event.getMember())).build();

                if(PermUtil.check(tc, Permission.MESSAGE_ATTACH_FILES)){
                    InputStream is = ImageUtil.getWelcomeImg(
                            event.getUser(),
                            event.getGuild(),
                            DBUtil.getImage(guild),
                            DBUtil.getColor(guild)
                    );

                    if(is == null) {
                        tc.sendMessage(welcome).queue();
                        return;
                    }

                    tc.sendMessage(welcome).addFile(is, String.format(
                            "%s.png",
                            System.currentTimeMillis()
                    )).queue();
                }else{
                    tc.sendMessage(welcome).queue();
                }
            }
        }
    }

    /**
     * Listens for when a channel in a guild gets deleted.
     * <br>This is used for when a saved welcome-channel gets deleted, that the bot won't return errors because of it
     * no longer exists.
     * <br>
     * <br>If a saved welcome channel gets deleted, we run
     * {@link com.andre601.purrbot.commands.guild.CmdWelcome#resetChannel(Guild) CmdWelcome.resetChannel(Guild)} to
     * reset it.
     *
     * @param event
     *        A {@link net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent TextChannelDeleteEvent}.
     */
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        Guild g = event.getGuild();

        if(!DBUtil.getWelcomeChannel(g).equals("none")){
            TextChannel channel = g.getTextChannelById(DBUtil.getWelcomeChannel(g));
            if(event.getChannel() == channel){
                CmdWelcome.resetChannel(g);
            }
        }
    }

}
