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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

import java.time.temporal.ChronoUnit;

@CommandDescription(
        name = "Ping",
        description = "purr.info.ping.description",
        triggers = {"ping"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "{p}ping"),
                @CommandAttribute(key = "help", value = "{p}ping")
        }
)
public class CmdPing implements Command{

    private final PurrBot bot;
    public CmdPing(PurrBot bot){
        this.bot = bot;
    }

    @Override
    public void run(Guild guild, TextChannel tc, Message msg, Member member, String... args){
        tc.sendMessage(
                bot.getRandomMsg(guild.getId(), "purr.info.ping.loading")
        ).queue(message -> msg.getJDA().getRestPing().queue((time) -> message.editMessage(
                bot.getMsg(guild.getId(), "purr.info.ping.info_full")
                        .replace("{edit_message}", String.valueOf(
                                msg.getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS)
                        ))
                        .replace("{discord}", String.valueOf(msg.getJDA().getGatewayPing()))
                        .replace("{rest_action}", String.valueOf(time))
        ).queue(), throwable -> message.editMessage(
                bot.getMsg(guild.getId(), "purr.info.ping.info")
                        .replace("{edit_message}", String.valueOf(
                                msg.getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS)
                        ))
                        .replace("{discord}", String.valueOf(msg.getJDA().getGatewayPing()))
        ).queue()));
    }
}
