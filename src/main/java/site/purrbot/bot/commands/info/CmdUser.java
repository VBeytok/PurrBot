/*
 * Copyright 2018 - 2020 Andre601
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package site.purrbot.bot.commands.info;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.Emotes;

import java.time.LocalDateTime;
import java.util.List;

@CommandDescription(
        name = "User",
        description = "Gives you information about yourself",
        triggers = {"user", "member", "userinfo", "memberinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}user"),
                @CommandAttribute(key = "help", value = "{p}user")
        }
)
public class CmdUser implements Command{

    private final PurrBot bot;

    public CmdUser(PurrBot bot){
        this.bot = bot;
    }

    private String getRoles(Member member){
        List<Role> roles = member.getRoles();

        if(roles.size() <= 1)
            return bot.getMsg(member.getGuild().getId(), "purr.info.user.no_roles_others");

        StringBuilder sb = new StringBuilder("```\n");
        for(int i = 1; i < roles.size(); i++){
            Role role = roles.get(i);
            String name = role.getName();

            if(sb.length() + name.length() + 20 > MessageEmbed.VALUE_MAX_LENGTH){
                int rolesLeft = roles.size() - i;

                sb.append(
                        bot.getMsg(member.getGuild().getId(), "purr.info.user.more_roles")
                                .replace("{remaining}", String.valueOf(rolesLeft))
                );
                break;
            }

            sb.append(name).append("\n");
        }
        
        sb.append("```");

        return sb.toString();
    }

    private String getName(Member member){
        StringBuilder sb = new StringBuilder(member.getUser().getName());

        if(member.isOwner())
            sb.append(Emotes.OWNER.getEmote());

        if(member.getUser().isBot())
            sb.append(Emotes.BOT.getEmote());
        
        return sb.toString();
    }

    private String getUserInfo(Member member){
        StringBuilder sb = new StringBuilder(
                bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.id")
                        .replace("{id}", member.getId())
        );
        
        if(member.getNickname() != null){
            String nick = member.getNickname();
            sb.append("\n")
                    .append(
                            bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.nickname")
                                    .replace("{nickname}", nick.length() > 20 ? nick.substring(0, 19) + "..." : nick)
                    );
        }
        
        return sb.toString();
    }
    
    private String getTimes(Member member){
        StringBuilder sb = new StringBuilder();

        sb.append(bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.created"))
                .append("\n   ")
                .append(bot.getMessageUtil().formatTime(LocalDateTime.from(member.getTimeCreated())))
                .append("\n\n")
                .append(bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.joined"))
                .append("\n   ")
                .append(bot.getMessageUtil().formatTime(LocalDateTime.from(member.getTimeJoined())));

        if(member.getTimeBoosted() != null)
            sb.append("\n\n")
                    .append(bot.getMsg(member.getGuild().getId(), "purr.info.user.embed.booster"))
                    .append("\n   ")
                    .append(bot.getMessageUtil().formatTime(LocalDateTime.from(member.getTimeBoosted())));

        return sb.toString();
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args) {
        EmbedBuilder embed = bot.getEmbedUtil().getEmbed(member.getUser(), tc.getGuild())
                .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                .addField(
                        getName(member),
                        String.format(
                                "```yaml\n" +
                                "%s" +
                                "```",
                                getUserInfo(member)
                        ),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.user.embed.avatar"),
                        bot.getMsg(guild.getId(), "purr.info.user.embed.avatar_url")
                                .replace("{link}", member.getUser().getEffectiveAvatarUrl()),
                        true
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.user.embed.role_highest"),
                        member.getRoles().isEmpty() ? bot.getMsg(guild.getId(), "purr.info.user.no_roles") : 
                                member.getRoles().get(0).getAsMention(),
                        true
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.user.embed.role_total"),
                        getRoles(member),
                        false
                )
                .addField(
                        bot.getMsg(guild.getId(), "purr.info.user.embed.dates"),
                        String.format(
                                "```yaml\n" +
                                "%s\n" +
                                "```",
                                getTimes(member)
                        ),
                        false
                );

        tc.sendMessage(embed.build()).queue();
    }
}
