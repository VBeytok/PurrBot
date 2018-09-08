package com.andre601.purrbot.core;

import ch.qos.logback.classic.Logger;
import com.andre601.purrbot.listeners.CommandListener;
import com.andre601.purrbot.listeners.GuildListener;
import com.andre601.purrbot.listeners.ReadyListener;
import com.andre601.purrbot.listeners.WelcomeListener;
import com.andre601.purrbot.util.HttpUtil;
import com.andre601.purrbot.util.PermUtil;
import com.andre601.purrbot.util.VoteUtil;
import com.andre601.purrbot.util.command.CommandHandler;
import com.andre601.purrbot.commands.server.*;
import com.andre601.purrbot.commands.fun.*;
import com.andre601.purrbot.commands.info.*;
import com.andre601.purrbot.commands.nsfw.*;
import com.andre601.purrbot.commands.owner.*;
import com.andre601.purrbot.util.messagehandling.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;

import org.discordbots.api.client.DiscordBotListAPI;
import org.discordbots.api.client.entity.Vote;
import org.slf4j.LoggerFactory;
import spark.Spark;

import javax.security.auth.login.LoginException;
import java.util.*;

import static spark.Spark.*;

public class PurrBot {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static DiscordBotListAPI api;

    public static GFile file = new GFile();

    private static Random random = new Random();

    //  All the ArrayLists for Random-Stuff and the blacklist
    private static List<String> RandomShutdownText    = new ArrayList<>();
    private static List<String> RandomNoShutdownText  = new ArrayList<>();
    private static List<String> RandomShutdownImage   = new ArrayList<>();
    private static List<String> RandomNoShutdownImage = new ArrayList<>();
    private static List<String> RandomFact            = new ArrayList<>();
    private static List<String> RandomNoNSWF          = new ArrayList<>();
    private static List<String> RandomDebug           = new ArrayList<>();
    private static List<String> RandomAPIPingMsg      = new ArrayList<>();
    private static List<String> RandomPingMsg         = new ArrayList<>();
    private static List<String> RandomKissImg         = new ArrayList<>();
    private static List<String> RandomAcceptFuckMsg   = new ArrayList<>();
    private static List<String> RandomDenyFuckMsg     = new ArrayList<>();

    private static List<String> BlacklistedGuilds     = new ArrayList<>();

    public static JDABuilder builder;
    public static JDA jda;

    private static void setup(){
        file.make("config", "./config.json", "/config.json");

        System.setProperty("WEBHOOK_URL", file.getItem("config", "errorWebhook"));
    }

    private static Logger logger = (Logger) LoggerFactory.getLogger(PurrBot.class);

    public static EventWaiter waiter = new EventWaiter();

    public static void main(String[] args){

        setup();

        builder = new JDABuilder(AccountType.BOT);

        //  Adding the Bot-Token from the config.json
        builder.setToken(file.getItem("config", "token"));

        //  Setting the API-token, if the bot isn't beta.
        if(!PermUtil.isBeta())
            api = new DiscordBotListAPI.Builder()
                    .token(file.getItem("config", "api-token"))
                    .botId(file.getItem("config", "id"))
                    .build();

        //  Let JDA try to reconnect, when disconnecting
        builder.setAutoReconnect(true);

        //  Set the status to "Do not disturb" with Game "Starting bot..." This changes in the ReadyListener.java
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setGame(Game.playing("Starting Bot..."));

        //  Executing the voids, to register listeners, commands and the random-stuff
        addListeners();
        addCommands();
        loadRandom();

        if(!PermUtil.isBeta()) {
            Spark.port(1000);

            Gson gsonVote = new Gson();
            post("/vote", (req, res) -> {

                Vote vote = gsonVote.fromJson(req.body(), Vote.class);
                VoteUtil.voteAction(vote.getBotId(), vote.getUserId(), vote.isWeekend());
                //  I have to return something for some reason... :shrug:
                return null;
            });
        }

        try {
            jda = builder.build().awaitReady();
        } catch (LoginException | InterruptedException ex) {
            LogUtil.ERROR("Couldn't load bot! Is the bot-token valid?");
            System.exit(0);
        }
    }

    private static void addListeners(){

        //  Adding listeners
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new CommandListener());
        builder.addEventListener(new GuildListener());
        builder.addEventListener(new WelcomeListener());
        builder.addEventListener(waiter);

    }

    private static void addCommands(){

        //  Adding commands
        CommandHandler.commands.put("help", new CmdHelp());
        CommandHandler.commands.put("info", new CmdInfo());
        CommandHandler.commands.put("shutdown", new CmdShutdown());
        CommandHandler.commands.put("sleep", new CmdShutdown());
        CommandHandler.commands.put("neko", new CmdNeko());
        CommandHandler.commands.put("catgirl", new CmdNeko());
        CommandHandler.commands.put("lewd", new CmdLewd());
        CommandHandler.commands.put("hug", new CmdHug());
        CommandHandler.commands.put("pat", new CmdPat());
        CommandHandler.commands.put("user", new CmdUser());
        CommandHandler.commands.put("server", new CmdServer());
        CommandHandler.commands.put("refresh", new CmdRefresh());
        CommandHandler.commands.put("slap", new CmdSlap());
        CommandHandler.commands.put("invite", new CmdInvite());
        CommandHandler.commands.put("prefix", new CmdPrefix());
        CommandHandler.commands.put("cuddle", new CmdCuddle());
        CommandHandler.commands.put("tickle", new CmdTickle());
        CommandHandler.commands.put("msg", new CmdMsg());
        CommandHandler.commands.put("welcome", new CmdWelcome());
        CommandHandler.commands.put("eval", new CmdEval());
        CommandHandler.commands.put("stats", new CmdStats());
        CommandHandler.commands.put("stat", new CmdStats());
        CommandHandler.commands.put("kiss", new CmdKiss());
        CommandHandler.commands.put("quote", new CmdQuote());
        CommandHandler.commands.put("debug", new CmdDebug());
        CommandHandler.commands.put("ping", new CmdPing());
        CommandHandler.commands.put("poke", new CmdPoke());
        CommandHandler.commands.put("gecg", new CmdGecg());
        CommandHandler.commands.put("lesbian", new CmdLesbian());
        CommandHandler.commands.put("les", new CmdLesbian());
        CommandHandler.commands.put("emote", new CmdEmote());
        CommandHandler.commands.put("kitsune", new CmdKitsune());
        CommandHandler.commands.put("foxgirl", new CmdKitsune());
        CommandHandler.commands.put("fakegit", new CmdFakegit());
        CommandHandler.commands.put("git", new CmdFakegit());
        CommandHandler.commands.put("leave", new CmdLeave());
        CommandHandler.commands.put("pm", new CmdPM());
        CommandHandler.commands.put("fuck", new CmdFuck());
        CommandHandler.commands.put("sex", new CmdFuck());
    }

    public static void loadRandom(){

        //  Getting all the content for the random-stuff
        Collections.addAll(RandomShutdownText, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomShutdownText")
                .split("\n")
        );
        Collections.addAll(RandomNoShutdownText, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomNoShutdownText")
                .split("\n")
        );
        Collections.addAll(RandomShutdownImage, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomShutdownImage")
                .split("\n")
        );
        Collections.addAll(RandomNoShutdownImage, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomNoShutdownImage")
                .split("\n")
        );
        Collections.addAll(RandomFact, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomFact")
                .split("\n")
        );
        Collections.addAll(RandomNoNSWF, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomNoNSFWMsg")
                .split("\n")
        );
        Collections.addAll(RandomDebug, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomDebugMsg")
                .split("\n")
        );
        Collections.addAll(RandomAPIPingMsg, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomAPIPingMsg")
                .split("\n")
        );
        Collections.addAll(RandomPingMsg, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomPingMsg")
                .split("\n")
        );
        Collections.addAll(RandomKissImg, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomKissImage")
                .split("\n")
        );
        Collections.addAll(RandomAcceptFuckMsg, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomAcceptFuckMsg")
                .split("\n")
        );
        Collections.addAll(RandomDenyFuckMsg, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/RandomDenyFuckMsg")
                .split("\n")
        );

        //  Getting the blacklisted Guild-IDs
        Collections.addAll(BlacklistedGuilds, HttpUtil.requestHttp(
                "https://raw.githubusercontent.com/andre601/purrbot-files/master/files/BlacklistedGuilds")
                .split("\n")
        );

    }

    //  Just public gets.
    public static List<String> getRandomShutdownText(){
        return RandomShutdownText;
    }
    public static List<String> getRandomNoShutdownText(){
        return RandomNoShutdownText;
    }
    public static List<String> getRandomShutdownImage(){
        return RandomShutdownImage;
    }
    public static List<String> getRandomNoShutdownImage(){
        return RandomNoShutdownImage;
    }
    public static List<String> getRandomFact(){
        return RandomFact;
    }
    public static List<String> getRandomNoNSWF(){
        return RandomNoNSWF;
    }
    public static List<String> getRandomDebug() {
        return RandomDebug;
    }
    public static List<String> getRandomAPIPingMsg(){
        return RandomAPIPingMsg;
    }
    public static List<String> getRandomPingMsg() {
        return RandomPingMsg;
    }
    public static List<String> getRandomKissImg(){
        return RandomKissImg;
    }
    public static List<String> getRandomAcceptFuckMsg(){
        return RandomAcceptFuckMsg;
    }
    public static List<String> getRandomDenyFuckMsg(){
        return RandomDenyFuckMsg;
    }

    public static List<String> getBlacklistedGuilds(){
        return BlacklistedGuilds;
    }

    public static Random getRandom(){
        return random;
    }

    //  Void to clear all the ArrayLists
    public static void clear(){
        RandomShutdownText.clear();
        RandomShutdownImage.clear();
        RandomNoShutdownText.clear();
        RandomNoShutdownImage.clear();
        RandomFact.clear();
        RandomNoNSWF.clear();
        RandomDebug.clear();
        RandomAPIPingMsg.clear();
        RandomPingMsg.clear();
        RandomKissImg.clear();
        RandomAcceptFuckMsg.clear();
        RandomDenyFuckMsg.clear();

        BlacklistedGuilds.clear();
    }

    public static WebhookClient webhookClient(String url){
        return new WebhookClientBuilder(url).build();
    }

    //  Get-method for the Discordbots-API
    public static DiscordBotListAPI getAPI(){
        return api;
    }

    //  Check for if it is *Purr*'s Birthday (19th of march)
    public static boolean isBDay(){
        final Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) == Calendar.MARCH && cal.get(Calendar.DAY_OF_MONTH) == 19;
    }

    public static Gson getGson(){
        return gson;
    }

    public static Logger getLogger(){
        return logger;
    }
}