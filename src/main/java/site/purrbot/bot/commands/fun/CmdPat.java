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

package site.purrbot.bot.commands.fun;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;
import site.purrbot.bot.constants.API;

import java.util.List;
import java.util.stream.Collectors;

@CommandDescription(
        name = "Pat",
        description = "Lets you pat someone.",
        triggers = {"pat", "patting", "pet"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "{p}pat <@user> [@user ...]")
        }
)
public class CmdPat implements Command{

    private PurrBot bot;

    public CmdPat(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();

        if(msg.getMentionedMembers().isEmpty()){
            bot.getEmbedUtil().sendError(tc, msg.getAuthor(), "purr.fun.pat.no_mention");
            return;
        }

        Member member = msg.getMember();
        if(member == null)
            return;

        Guild guild = msg.getGuild();
        List<Member> members = msg.getMentionedMembers();

        if(members.contains(guild.getSelfMember())){
            if(bot.isBeta()){
                tc.sendMessage(
                        bot.getMsg(guild.getId(), "snuggle.fun.pat.mention_snuggle")
                ).queue();
            }else{
                tc.sendMessage(bot.getMsg(guild.getId(), "purr.fun.pat.mention_purr")).queue();
            }
            msg.addReaction("\u2764").queue();
        }

        if(members.contains(msg.getMember())){
            tc.sendMessage(
                    bot.getMsg(guild.getId(), "purr.fun.pat.mention_self", member.getAsMention())
            ).queue();
        }

        String targets = members.stream()
                .filter(mem -> !mem.equals(guild.getSelfMember()))
                .filter(mem -> !mem.equals(msg.getMember()))
                .map(Member::getEffectiveName)
                .collect(Collectors.joining(", "));

        if(targets.isEmpty())
            return;
    
        String link = bot.getHttpUtil().getImage(API.GIF_PAT);

        tc.sendMessage(
                bot.getMsg(guild.getId(), "purr.fun.pat.loading")
        ).queue(message -> {
            if(link == null){
                message.editMessage(MarkdownSanitizer.escape(
                        bot.getMsg(guild.getId(), "purr.fun.pat.message", member.getEffectiveName(), targets)
                )).queue();
            }else{
                message.editMessage(EmbedBuilder.ZERO_WIDTH_SPACE)
                        .embed(bot.getEmbedUtil().getEmbed().setDescription(MarkdownSanitizer.escape(
                                bot.getMsg(guild.getId(), "purr.fun.pat.message", member.getEffectiveName(), targets)
                        )).setImage(link).build()).queue();
            }
        });
    }
}
