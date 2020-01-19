/*
 * Copyright 2020 Andre601
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

package site.purrbot.bot.commands.guild;

import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import site.purrbot.bot.PurrBot;
import site.purrbot.bot.commands.Command;

@CommandDescription(
        name = "Debug",
        description = "Creates a Debug with useful information.",
        triggers = {"debug"},
        attributes = {
                @CommandAttribute(key = "manage_server"),
                @CommandAttribute(key = "category", value = "guild"),
                @CommandAttribute(key = "usage", value = "{p}debug")
        }
)
public class CmdDebug implements Command{
    
    private PurrBot bot;
    
    public CmdDebug(PurrBot bot){
        this.bot = bot;
    }
    
    @Override
    public void execute(Message msg, String s){
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        
        tc.sendMessage(
                "Creating debug...\n" +
                "This may take a while."
        ).queue(message -> {
            String code = bot.getDebugUtil().getDebugUrl(guild, msg.getAuthor());
            if(code == null)
                message.editMessage("Could not create debug! There was an error. :(").queue();
            else
                message.editMessage(String.format(
                        "**Debug created!**\n" +
                        "You can find it here: <https://bytebin.lucko.me/%s>",
                        code
                )).queue();
        });
    }
}