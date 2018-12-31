import javafx.scene.paint.Color;
import org.apache.commons.io.FileUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by declan on 04/04/2017.
 * Used by Ka, last updated 11/02/2017. Commented out prune again because it just isn't working.
 */
public class CommandHandler  {

    //A bunch of things used for meme commands.
    static Random random = new Random();
    static String[] memokou = {"http://i.imgur.com/XfDiX5s.jpg", "http://i.imgur.com/uJPBD1W.jpg", "http://i.imgur.com/Zoxoigd.jpg", "http://i.imgur.com/j6pyLsD.jpg", "http://i.imgur.com/1lL6fmo.jpg"};
    static String[] response = {"Me too.", "Same.", "Me too, dude.", "Me too thanks.", "True."};
    static String[] omemechan = {"http://i.imgur.com/kPnMUhG.png", "http://i.imgur.com/2dlpDHi.jpg", "http://i.imgur.com/ctXmRae.png"};
    static String[] lewdresponse = {"https://i.imgur.com/p5BubWh.png", "https://i.imgur.com/QCBWHjM.png", "http://i.imgur.com/slyS7f2.png", "http://i.imgur.com/ULooYdP.png", "http://i.imgur.com/LQoT1OW.png", "https://cdn.discordapp.com/attachments/351519235995992064/351520002211577867/image.gif", "http://i.imgur.com/doe4nPa.jpg", "http://i.imgur.com/fRpRiqO.png", "http://i.imgur.com/V55fF7m.jpg", "http://i.imgur.com/LBqmJtv.png", "http://i.imgur.com/T0ulzsX.jpg", "http://i.imgur.com/lRYhuLd.png", "http://i.imgur.com/p9gF6TR.png", "http://i.imgur.com/CSq1r3e.jpg", "http://i.imgur.com/1KlGNxi.png"};
    static String[] joeyface = {"http://imgur.com/UOZsti4.jpg", "http://imgur.com/RV1kPff.jpg", "http://imgur.com/UmKzkS5.jpg", "http://imgur.com/Z3IanGI.jpg", "http://imgur.com/pOGfiAr.jpg", "http://imgur.com/JPxdb8q.jpg", "http://imgur.com/c3w3bLH.jpg"};
    static String[] hearthstonehero = {"Warlock", "Rogue", "Paladin", "Warrior", "Mage", "Shaman", "Druid", "Hunter", "Priest"};
    static String[] hearthstoneterm1 = {"Odd", "Even", "Dragon", "C'Thun", "N'Zoth", "Token", "Combo", "Aggro", "Face", "Control", "Reno", "Wallet", "Big Boy", "Miracle", "Patron", "Malygos", "Yogg", "Tempo", "Zoo", "Hand", "Midrange", "OTK", "Monkey", "Mech", "Value", "Quest", "Pirate"};
    static String[] hearthstoneterm2 = {"Odd", "Even", "Dragon", "C'Thun", "N'Zoth", "Token", "Combo", "Aggro", "Face", "Control", "Reno", "Wallet", "Murloc", "Miracle", "Patron", "Malygos", "Yogg", "Tempo", "Zoo", "Hand", "Midrange", "OTK", "Monkey", "Mech", "Value", "Quest", "Pirate"};
    static String[] lennyface = {"( ͡° ͜ʖ ͡°)", "乁(◡‿◡✿)ㄏ", "ヽ༼ຈل͜ຈ༽ﾉ", "ᕙ༼ຈل͜ຈ༽ᕗ", "(つ°ヮ°)つ", "༼つ ◕_◕ ༽つ", "乁( ◔ ౪◔)ㄏ", "乁( ◔_◔)ㄏ", "(◉◞౪◟◉)", "(*'Д')", "(=ﾟДﾟ=)", "(^ ՞ਊ ՞)☛", "(ง ◉ω◉)ง", "(✿╹◡╹)", "ᑴಠωಠᑷ", "ʕ༼◕  ౪  ◕✿༽ʔ", "(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧", "(つ◕౪◕)つ", "٩(◕‿◕｡)۶", "(つ◕‿◕)ﾉ--✿", "https://cdn.discordapp.com/attachments/86919947267104768/215186661020008449/tumblr_nob50s8RB31tgwh9yo1_400.gif"};

    // A static map of commands mapping from command string to the functional impl
    private static Map<String, Command> commandMap = new HashMap<>();
    static {


		//Outputs a pretty Embeded help block. Lists commands, as well as some relevant information about the bot.
		commandMap.put("help", (event, args) -> {
			EmbedBuilder builder = new EmbedBuilder();
			builder.appendField("Garbage Meme Commands: ", "thinksphere\ndonger \nhappyday \nSMorcerer \n justanotherday", true);
			builder.appendField("More Relevant Commands: ", "choose\necho\ncustomcommandadd\ncustomcommandlist\ncustomcommandremove\nroll\naddrole (mod/admin use)\nremoverole (mod/admin use)\nrequestrole (for self use)\nrelinquishrole (for self use) \nmyava \ntheirava \n customcolor", true);
			builder.withAuthorName("Botkatchi");
			builder.withAuthorIcon("http://i.imgur.com/fHSGYZg.png");
			builder.withColor(200, 0, 0);
			builder.withDescription("A multi-purpose Discord bot made by Ekatchi. Mostly for memes. All commands are prompted with `-`.\nFor more information about specific commands and any potential inputs, just call the command.\nTo call a custom command, use `-[customcommandname]`.\n(In all cases [] is used, ignore.");
			builder.withFooterText("Last updated: December 31st 2018");
			RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
		});

        // If the IUser that called this is in a voice channel, join them
        commandMap.put("joinvoice", (event, args) -> {
            IVoiceChannel userVoiceChannel = event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel();
            if(userVoiceChannel == null) {
                BotUtils.sendMessage(event.getChannel(), "Error: User must be in a voice channel.");
                return;
            }
            userVoiceChannel.join();

        });



        //If the user that called this is in the same voice channel, clear the playlist and leave the channel.
        commandMap.put("leavevoice", (event, args) -> {
            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();
            if(botVoiceChannel == null)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Not currently in a voice channel.");
                return;
            }
            if(botVoiceChannel.getConnectedUsers().contains(event.getAuthor())) {
                AudioPlayer audioP = AudioPlayer.getAudioPlayerForGuild(event.getGuild());
                audioP.clear();
                botVoiceChannel.leave();
            }
            else
                BotUtils.sendMessage(event.getChannel(), "Error: Must be in the same voice channel to use this command.");
        });

        commandMap.put("echo", (event, args) -> {
            if(args.size() == 0)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Usage: -echo [message]");
            }
            String message = event.getMessage().getContent().substring(5);
            BotUtils.sendMessage(event.getChannel(), message);
        });
        commandMap.put("requestrole", (event, args) -> {
        	String blah = "";
        	for(int i = 0; i < args.size(); i++)
			{
				blah = blah + " "+ args.get(i).toString();
				blah = blah.trim();
			}
            try{IRole role = (event.getGuild().getRolesByName(blah).get(0));}
            catch (Exception e)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: " + blah + " role could not be found.");
                return;
            }
            if(event.getMessage().getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRolesByName(blah).get(0)))
            {
                BotUtils.sendMessage(event.getChannel(), "Error: you already have the " + blah + " role.");
                return;
            }
            if(event.getGuild().getRoles().contains(event.getGuild().getRolesByName(blah).get(0)))
            {
                IRole role = event.getGuild().getRolesByName(blah).get(0);
                try{event.getMessage().getAuthor().addRole(role);}
                catch (Exception e)
                {
                    BotUtils.sendMessage(event.getChannel(), "Error: Requested role hierarchy is too high.");
                    return;
                }
                BotUtils.sendMessage(event.getChannel(),  "You have successfully been given the " + blah + " role.");
            }
            else
                BotUtils.sendMessage(event.getChannel(), "Error: " + blah + " role could not be found.");
        });

        commandMap.put("relinquishrole", (event, args) -> {
			String blah = "";
			for(int i = 0; i < args.size(); i++)
			{
				blah = blah + " "+ args.get(i).toString();
				blah = blah.trim();
			}
            try{IRole role = event.getGuild().getRolesByName(blah).get(0);}
            catch (Exception E)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Role not found.");
                return;
            }
            IRole role = event.getGuild().getRolesByName(blah).get(0);
            if(event.getAuthor().getRolesForGuild(event.getGuild()).contains(role)) {
                try {
                    event.getMessage().getAuthor().removeRole(role);
                } catch (Exception e) {
                    BotUtils.sendMessage(event.getChannel(), "Error: Role could not be removed.");
                    return;
                }
                BotUtils.sendMessage(event.getChannel(), "Role successfully removed.");
            }
            else
                BotUtils.sendMessage(event.getChannel(), "Error: You do not have the requested role to be removed.");
        });
        commandMap.put("addrole", (event, args) -> {
            if(args.size() > 1)
            {
                try{event.getMessage().getMentions().get(0);}
                catch (Exception e)
                {
                    BotUtils.sendMessage(event.getChannel(), "Error: User not found.");
                    return;
                }
                try{String s = (args.toString().substring(22));}
                catch (Exception e)
                {
                    BotUtils.sendMessage(event.getChannel(), "Error: Usage: -addrole [@user][role].");
                    return;
                }
                if(event.getMessage().getMentions().get(0).equals(event.getAuthor())) {
                    BotUtils.sendMessage(event.getChannel(), "Error: Cannot give self roles using -addrole. Please use -requestrole.");
                    return;
                }
                EnumSet userpermissions = event.getAuthor().getPermissionsForGuild(event.getGuild());
                String userperms = userpermissions.toString();
                EnumSet botpermissions = event.getClient().getOurUser().getPermissionsForGuild(event.getGuild());
                String botperms = botpermissions.toString();
                if((userperms.contains("MANAGE_ROLES")))
                {
                    String s = args.toString().replaceAll(", ", " ").substring(22);
                    String blah = s.substring(0, s.length() - 1).trim();
                    try{IRole role = (event.getGuild().getRolesByName(blah).get(0));}
                    catch (Exception e)
                    {
                        BotUtils.sendMessage(event.getChannel(), "Error: " + blah + " role could not be found.");
                        return;
                    }
                    if(event.getMessage().getMentions().get(0).getRolesForGuild(event.getGuild()).contains(event.getGuild().getRolesByName(blah).get(0)))
                    {
                        BotUtils.sendMessage(event.getChannel(), "Error: " + event.getMessage().getMentions().get(0) + " already has " + blah + " role.");
                        return;
                    }
                    if(event.getGuild().getRoles().contains(event.getGuild().getRolesByName(blah).get(0)))
                    {
                        IRole role = event.getGuild().getRolesByName(blah).get(0);
                        try{event.getMessage().getMentions().get(0).addRole(role);}
                        catch (Exception e)
                        {
                            BotUtils.sendMessage(event.getChannel(), "Error: Requested role hierarchy is too high.");
                            return;
                        }
                        BotUtils.sendMessage(event.getChannel(), event.getMessage().getMentions().get(0) + " has successfully been given the " + blah + " role.");
                    }
                    else
                        BotUtils.sendMessage(event.getChannel(), "Error: " + blah + " role could not be found.");
                }
                else
                    BotUtils.sendMessage(event.getChannel(), "Error: You do not have the permissions to do that.");
            }
            else
                BotUtils.sendMessage(event.getChannel(), "Error: Usage: -addrole [@user] [role]");
        });

        commandMap.put("removerole", (event, args) -> {
            try {
                if (args.size() > 1) {
                    try {
                        event.getMessage().getMentions().get(0);
                    } catch (Exception e) {
                        BotUtils.sendMessage(event.getChannel(), "Error: User not found.");
                        return;
                    }
                    try {
                        String s = (args.toString().substring(22));
                    } catch (Exception e) {
                        BotUtils.sendMessage(event.getChannel(), "Error: Usage: -removerole [@user][role].");
                        return;
                    }
                    EnumSet userpermissions = event.getAuthor().getPermissionsForGuild(event.getGuild());
                    String userperms = userpermissions.toString();
                    EnumSet botpermissions = event.getClient().getOurUser().getPermissionsForGuild(event.getGuild());
                    String botperms = botpermissions.toString();
                    if ((userperms.contains("MANAGE_ROLES"))) {
                        String s = args.toString().replaceAll(", ", " ").substring(22);
                        String blah = s.substring(0, s.length() - 1).trim();
                        try {
                            IRole role = (event.getGuild().getRolesByName(blah).get(0));
                        } catch (Exception e) {
                            BotUtils.sendMessage(event.getChannel(), "Error: " + blah + " role could not be found.");
                            return;
                        }
                        if (!(event.getMessage().getMentions().get(0).getRolesForGuild(event.getGuild()).contains(event.getGuild().getRolesByName(blah).get(0)))) {
                            BotUtils.sendMessage(event.getChannel(), "Error: " + event.getMessage().getMentions().get(0) + " does not have the " + blah + " role.");
                            return;
                        }
                        if (event.getGuild().getRoles().contains(event.getGuild().getRolesByName(blah).get(0))) {
                            IRole role = event.getGuild().getRolesByName(blah).get(0);
                            event.getMessage().getMentions().get(0).removeRole(role);
                            BotUtils.sendMessage(event.getChannel(), event.getMessage().getMentions().get(0) + " has had the " + blah + " role removed.");
                        } else
                            BotUtils.sendMessage(event.getChannel(), "Error: " + blah + " role could not be found.");
                    } else
                        BotUtils.sendMessage(event.getChannel(), "Error: You do not have the permissions to do that.");
                } else
                    BotUtils.sendMessage(event.getChannel(), "Error: Usage: -removerole [@user] [role]");
            }
            catch (MissingPermissionsException e)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Edited roles hierarchy is too high.");
            }
        });

        //returns a URL to the user's avatar.
        commandMap.put("myava", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), event.getAuthor().getAvatarURL());
        });

        //returns a URL of the specified user's avatar. If the username could not be found, says so with an error message.
        commandMap.put("theirava", (event, args) ->{
            try{event.getMessage().getMentions().get(0);}
            catch (Exception e)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Usage: -theirava @username");
                return;
            }
            IUser user = event.getMessage().getMentions().get(0);
            BotUtils.sendMessage(event.getChannel(), user.getAvatarURL());
        });


        /** Doesn't work as intended sometimes. Please revisit. Or don't.
         commandMap.put("prune", (event, args) ->{
         if(args.size() == 0) {
         BotUtils.sendMessage(event.getChannel(), "Error: Usage: -prune [# between 1 and 30]");
         return;
         }
         EnumSet userpermissions = event.getAuthor().getPermissionsForGuild(event.getGuild());
         String userperms = userpermissions.toString();
         EnumSet botpermissions = event.getClient().getOurUser().getPermissionsForGuild(event.getGuild());
         String botperms = botpermissions.toString();
         if(userperms.contains(botperms)) {
         int num = Integer.parseInt(args.toString().substring(1, args.toString().length() - 1));
         if(num > 30) {
         BotUtils.sendMessage(event.getChannel(), "Error: The prune number must be less than 30.");
         return;
         }
         MessageHistory history = event.getChannel().getMessageHistory(num + 1);
         try{event.getChannel().bulkDelete(history);}
         catch (Exception e)
         {
         BotUtils.sendMessage(event.getChannel(), "Error: Could not delete the last " + num + " messages. Try again at your own risk.");
         return;
         }
         BotUtils.sendMessage(event.getChannel(), ":thumbsup: Successfully deleted the last " + num + " message(s).");
         }
         else
         BotUtils.sendMessage(event.getChannel(), "Error: You do not have permission to use that command.");
         }); **/

        commandMap.put("roll", (event, args) ->{
            if(args.size() != 1) {
                BotUtils.sendMessage(event.getChannel(), "Error: Usage: -roll [# of dice to roll]d[# of dice sides]");
                return;
            }
            String input = args.get(0);
            int dlocation = input.indexOf('d');
            int dicenum = Integer.parseInt(input.substring(0, dlocation));
            int dicenum2 = Integer.parseInt(input.substring(dlocation+1, input.length()));
            String dicearray = "";
            if(dicenum > 100 || dicenum2 > 100)
			{
				BotUtils.sendMessage(event.getChannel(), "Please use reasonable numbers thank");
				return;
			}
            if (dicenum2 == 0)
					{
						BotUtils.sendMessage(event.getChannel(), "Please don't try to roll a d0.");
						return;
					}
            int total = 0;
            for(int i = 0; i < dicenum; i++)
            {
                int diceroll = random.nextInt(dicenum2+1);
                while(diceroll == 0)
                    diceroll = random.nextInt(dicenum2+1);
                total += diceroll;
                dicearray = dicearray.concat("`"+ diceroll + "` ");
            }
            BotUtils.sendMessage(event.getChannel(), "You rolled `"+dicenum+"`  `"+dicenum2+"` sided dice for a total of "+ "`" + total + "`.\n Specific dice rolled: "+ dicearray);


        });
        //Best meme command. rember happy day
        commandMap.put("happyday", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), "pls rember wen u feel scare or frigten\n" +
                    "never forget ttimes wen u feeled happy\n" +
                    "wen day is dark alway rember happy day\n" +
                    "https://cdn.discordapp.com/attachments/163382741491122176/256987667479134209/kheEVrN.png");
        });

        //21 different dongers and counting.
        commandMap.put("donger", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), lennyface[random.nextInt(21)]);
        });

        //Probably going to remove this one, too many good Chinese Mokou memes to upload to imgur and put into the list.
        /*commandMap.put("mokoumeme", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), memokou[random.nextInt(5)]);
        });*/
        //Randomly slaps together 1-3 Hearthstone terms and a class. Doesn't give duplicates, and has "Jade" and "Handbuff" for their respective classes.
        commandMap.put("SMorcerer", (event, args) -> {
            String temphero = "";
            if(args.size() == 1)
            {
                if(args.get(0).equalsIgnoreCase("Paladin") || (args.get(0).equalsIgnoreCase("Rogue")) || (args.get(0).equalsIgnoreCase("Warrior")) || (args.get(0).equalsIgnoreCase("Shaman")) || args.get(0).equalsIgnoreCase("Mage") || args.get(0).equalsIgnoreCase("Warlock") || args.get(0).equalsIgnoreCase("Hunter") || args.get(0).equalsIgnoreCase("Priest") || args.get(0).equalsIgnoreCase("Druid")) {
                    temphero = args.get(0);
                    String first = temphero.substring(0, 1).toUpperCase();
                    String second = temphero.substring(1, temphero.length()).toLowerCase();
                    temphero = first + second;
                }
                else
                {
                    BotUtils.sendMessage(event.getChannel(), "Error: usage: -SMorcerer [Class name if desired]");
                    return;
                }
            }
            if(args.size() == 0) {
                temphero = hearthstonehero[random.nextInt(9)];
            }
            int tempint = random.nextInt(26);
            String tempterm = hearthstoneterm1[tempint];
            switch (random.nextInt(4)) {
                case 0:
                    if (temphero.equals("Rogue") || temphero.equals("Druid") || temphero.equals("Shaman"))
                        hearthstoneterm1[tempint] = "Jade";
                    else if (temphero.equals("Paladin") || temphero.equals("Hunter") || temphero.equals("Warrior"))
                        hearthstoneterm1[tempint] = "Handbuff";
                    BotUtils.sendMessage(event.getChannel(), hearthstoneterm1[random.nextInt(27)] + " " + temphero);
                    hearthstoneterm1[tempint] = tempterm;
                    break;
                case 1:
                    if (temphero.equals("Rogue") || temphero.equals("Druid") || temphero.equals("Shaman"))
                        hearthstoneterm1[tempint] = "Jade";
                    else if (temphero.equals("Paladin") || temphero.equals("Hunter") || temphero.equals("Warrior"))
                        hearthstoneterm1[tempint] = "Handbuff";
                    BotUtils.sendMessage(event.getChannel(), hearthstoneterm1[random.nextInt(27)] + " " + temphero);
                    hearthstoneterm1[tempint] = tempterm;
                    break;
                case 2:
                    if (temphero.equals("Rogue") || temphero.equals("Druid") || temphero.equals("Shaman"))
                        hearthstoneterm1[tempint] = "Jade";
                    else if (temphero.equals("Paladin") || temphero.equals("Hunter") || temphero.equals("Warrior"))
                        hearthstoneterm1[tempint] = "Handbuff";
                    String term1 = hearthstoneterm1[random.nextInt(27)];
                    String term2 = hearthstoneterm2[random.nextInt(27)];
                    while (term1.equals(term2)) {
                        term2 = hearthstoneterm2[random.nextInt(25)];
                    }
                    BotUtils.sendMessage(event.getChannel(), term1 + " " + term2 + " " + temphero);
                    hearthstoneterm1[tempint] = tempterm;
                    break;
                case 3:
                    if (temphero.equals("Rogue") || temphero.equals("Druid") || temphero.equals("Shaman"))
                        hearthstoneterm1[tempint] = "Jade";
                    else if (temphero.equals("Paladin") || temphero.equals("Hunter") || temphero.equals("Warrior"))
                        hearthstoneterm1[tempint] = "Handbuff";
                    String term11 = hearthstoneterm1[random.nextInt(27)];
                    String term12 = hearthstoneterm2[random.nextInt(27)];
                    String term13 = hearthstoneterm2[random.nextInt(27)];
                    while (term11.equals(term12) || term11.equals(term13) || term12.equals(term13)) {
                        term11 = hearthstoneterm1[random.nextInt(27)];
                        term12 = hearthstoneterm2[random.nextInt(27)];
                        term13 = hearthstoneterm2[random.nextInt(27)];
                    }
                    BotUtils.sendMessage(event.getChannel(), term11 + " " + term12 + " " + term13 + " " + temphero);
                    hearthstoneterm1[tempint] = tempterm;
                    break;
            }
            if(args.size() == 2)
                BotUtils.sendMessage(event.getChannel(), "Error: usage: -SMorcerer [Class name if desired]");
        });

        //Might get rid of the meme commands. Kind of pointless. But at the same time...
       /*commandMap.put("joeyface", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), joeyface[random.nextInt(7)]);
        });*/

        //Not a listed command, but it's funny. Poor Jay.
        commandMap.put("bestplay", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), "Embrace the Shadow into Reno Jackson is a powerful play that immediately ends the game.");
        });

        /*commandMap.put("ugu", (event, args) ->{
            BotUtils.sendMessage(event.getChannel(), " •́⩊•̀ ");
        });*/

        /*commandMap.put("omemechan", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), omemechan[random.nextInt(3)]);
        });

        commandMap.put("lewd", (event, args) -> {
            BotUtils.sendMessage(event.getChannel(), lewdresponse[random.nextInt(14)]);
        });*/


        commandMap.put("thinksphere", (event, args) ->{
            BotUtils.sendMessage(event.getChannel(), "http://i.imgur.com/aqtwUW4.gifv");
        });

        commandMap.put("customcommandlist", (event, args) ->{
            if(args.size() != 0)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Usage: -customcommandlist");
                return;
            }
            String commandlist = "";
            File file = new File("src/customcommands.txt");
            try(Scanner sc = new Scanner(file))
            {
                while(sc.hasNextLine())
                {
                    String line = sc.nextLine();
                    if (line.equals(""))
                        line = line;
                    else
                    {
                        String linesplit[] = line.split("ð");
                        String commandname = linesplit[2];
                        commandlist += "`" + commandname + "` ";
                    }
                }
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Could not find any commands.");
            }
            catch(IOException e)
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Could not access data. Please do not try again.");
            }
            BotUtils.sendMessage(event.getChannel(), "Current custom commands:" + commandlist);
        });

        //Creates a custom command in the .txt file on the desktop of this computer. If that disappears somehow, disable this immediately.
        commandMap.put("customcommandadd", (event, args) -> {
					//ï is the seperator between the username and the commandname
					//ð is the seperator between the commandname and commandtext
					if (args.size() < 1) {
						BotUtils.sendMessage(event.getChannel(), "Error: Usage: -customcommandadd [commandname] [text] to add command.");
						return;
					}
					if (args.size() == 1) {
						BotUtils.sendMessage(event.getChannel(), "Error: Usage:\n-customcommandadd [commandname] [text]\nto add command.\nCustom commands are called by simply\nusing `-[commandname]`.\nFor a list of custom commands, call `-customcommmandlist`.");
						return;
					}

            File outFile = new File("src/customcommands.txt");
            try(FileWriter fWriter = new FileWriter(outFile, true))
            {
                PrintWriter pWriter = new PrintWriter(fWriter);
                //Command name is args.get(0);
                //Command text is args.get(1);
                String commandtext = "";
                for(int i = 1; i < args.size(); i++)
                {
                    commandtext += " " + args.get(i);
                }
                pWriter.println("Added by ð " + event.getAuthor().getName() + " ð " + args.get(0) +  " ð " + commandtext);
                pWriter.close();
                BotUtils.sendMessage(event.getChannel(), "Custom command " + args.get(0) + " successfully added to the database.");
            }
            catch(IOException e )
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Could not access data. Please do not try again.");
            }

        });

        commandMap.put("customcommandremove", (event, args) ->{
        	Boolean removed = false;
        	if(args.size() != 1)
			{
				BotUtils.sendMessage(event.getChannel(), "Error: Usage: -customcommandremove [commandname]. Command can only be removed by the person who added it.");
			}
        	String searchString = event.getAuthor().getName() + " ð " + args.get(0);
        	File file = new File("src/customcommands.txt");
        	try {
				List<String> lines = FileUtils.readLines(file);
				int size = lines.size();
				for(int i = 0; i < size; i++)
				{
					if(lines.get(i).contains(searchString))
					{
						List<String> updatedLines = lines.stream().filter(s -> !s.contains(searchString)).collect(Collectors.toList());
						FileUtils.writeLines(file, updatedLines, false);
						BotUtils.sendMessage(event.getChannel(), "Custom command " + args.get(0) + " has been successfully removed.");
						removed = true;
					}
					else if (lines.get(i).contains(args.get(0)))
					{
						if(event.getAuthor().getPermissionsForGuild(event.getGuild()).toString().contains("MANAGE_MESSAGES") || event.getAuthor().getPermissionsForGuild(event.getGuild()).toString().contains("MANAGE_ROLES"))
						{
							List<String> updatedLines = lines.stream().filter(s -> !s.contains(args.get(0))).collect(Collectors.toList());
							FileUtils.writeLines(file, updatedLines, false);
							BotUtils.sendMessage(event.getChannel(), "Custom command " + args.get(0) + " has been successfully removed. Moderator overwrite.");
							removed = true;
						}
						else
						{
							BotUtils.sendMessage(event.getChannel(), "Error: Only the person who added the command may remove it.");
						}
					}
				}
				if(!removed)
				{
					BotUtils.sendMessage(event.getChannel(), "Error: command does not exist.\nUse `-customcommandlist` to get a list of current commands.");
				}
			}
			catch (IOException e)
			{
				BotUtils.sendMessage(event.getChannel(), "Error: Could not access data. Please do not try again.");
			}
		});

        commandMap.put("choose", (event, args) ->{
            if (args.size() != 0 && args.size() != 1)
            {
            	String chooseString = args.toString();
            	for(int i = 0; i < args.size(); i++)
				{
					if(!(chooseString.contains("\"")))
					{
						BotUtils.sendMessage(event.getChannel(), "Error: Usage: -choose [option] [option] (can take more than two options, keep options seperate using quotation marks) ");
						return;
					}
				}
            	chooseString = chooseString.replace('[', ' ');
            	chooseString = chooseString.replaceAll("]", "");
            	chooseString = chooseString.replaceAll(",", "");
            	ArrayList<String> list = new ArrayList<String>();
				int lastQuote = 2;
				for (int i = 2; i < chooseString.length(); i++)
				{
					if(chooseString.charAt(i) == '"')
					{
						list.add(chooseString.substring(lastQuote, i));
						i++;
						lastQuote = i;
					}
				}
				for(int i = 0; i < list.size(); i++)
				{
					if(list.get(i).equals (" "))
					{
						list.remove(i);
					}
				}
				if(list.size() <= 1)
				{
					BotUtils.sendMessage(event.getChannel(), "Error: one or less choices given.");
					return;
				}
                int rand = random.nextInt(list.size());
                BotUtils.sendMessage(event.getChannel(), "I choose " + list.get(rand)+ "!");
            }
            else
            {
                BotUtils.sendMessage(event.getChannel(), "Error: Usage: -choose [option] [option] (can take more than two options, keep options seperate using quotation marks) ");
            }
        });
		commandMap.put("justanotherday", (event, args) ->{
			String[] sitcoms = {"https://www.youtube.com/watch?v=Niu9Zmrx0p8", "https://www.youtube.com/watch?v=Ag1o3koTLWM", "https://www.youtube.com/watch?v=ay70lpWGpuQ", "https://www.youtube.com/watch?v=QVwr8lrQ2ww", "https://www.youtube.com/watch?v=_V2sBURgUBI"};
			BotUtils.sendMessage(event.getChannel(), sitcoms[random.nextInt(sitcoms.length)]);
		});
        commandMap.put("customcolor", (event, args) ->{
			if(args.size() >= 2) {
        	boolean pingable = false;
        	if(args.get(args.size() - 1).equals("true"))
			{
				args.remove(args.size() - 1);
				pingable = true;
			}
			try{
				Color color = Color.valueOf(args.get(0));
			}
			catch (IllegalArgumentException A)
			{
				BotUtils.sendMessage(event.getChannel(), "Error: provided color value was not hexidecimal");
			}
			try {
				Color color = Color.valueOf(args.get(0));
				args.remove(0);
				String rolename = args.toString();
				rolename = rolename.substring(1, rolename.length() - 1);
				rolename = rolename.replaceAll(",", "");
				if (event.getGuild().getRolesByName(rolename).isEmpty()) {
					EnumSet<Permissions> perms = event.getGuild().getRolesByName("@everyone").get(0).getPermissions();
					java.awt.Color awtColor = new java.awt.Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity());
					event.getGuild().createRole().edit(awtColor, false, rolename, perms, pingable);
					BotUtils.sendMessage(event.getChannel(), "Role " + rolename + " successfully made.");
					event.getAuthor().addRole(event.getGuild().getRolesByName(rolename).get(0));
					BotUtils.sendMessage(event.getChannel(), "You have successfuly given the " + rolename + " role.");
				} else {
					BotUtils.sendMessage(event.getChannel(), "Error: Role already exists.");
				}
			}
			catch (IllegalArgumentException A)
			{
				BotUtils.sendMessage(event.getChannel(), "Error: role name must be between 1 and 32 characters.");
			}
			}
			else
				BotUtils.sendMessage(event.getChannel(), "Error: Usage: -customcolor [hexidecimalvalue] [rolename] (true if pingable)");

		});


         //An example embed block, use for building other ones.
         /**commandMap.put("exampleembed", (event, args) -> {

         EmbedBuilder builder = new EmbedBuilder();

         builder.appendField("fieldTitleInline", "fieldContentInline", true);
         builder.appendField("fieldTitleInline2", "fieldContentInline2", true);
         builder.appendField("fieldTitleNotInline", "fieldContentNotInline", false);
         builder.appendField(":tada: fieldWithCoolThings :tada:", "[hiddenLink](http://i.imgur.com/Y9utuDe.png)", false);

         builder.withAuthorName("authorName");
         builder.withAuthorIcon("http://i.imgur.com/PB0Soqj.png");
         builder.withAuthorUrl("http://i.imgur.com/oPvYFj3.png");

         builder.withColor(255, 0, 0);
         builder.withDesc("withDesc");
         builder.withDescription("withDescription");
         builder.withTitle("withTitle");
         builder.withTimestamp(100);
         builder.withUrl("http://i.imgur.com/IrEVKQq.png");
         builder.withImage("http://i.imgur.com/agsp5Re.png");

         builder.withFooterIcon("http://i.imgur.com/Ch0wy1e.png");
         builder.withFooterText("footerText");
         builder.withFooterIcon("http://i.imgur.com/TELh8OT.png");
         builder.withThumbnail("http://www.gstatic.com/webp/gallery/1.webp");

         builder.appendDesc(" + appendDesc");
         builder.appendDescription(" + appendDescription");

         RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
         });**/
    }

    //Handles all message received events, and parses them out as needed. Can mess around with, if you know what you're doing.
    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {

        // Note for error handling, you'll probably want to log failed commands with a logger or sout
        // In most cases it's not advised to annoy the user with a reply incase they didn't intend to trigger a
        // command anyway, such as a user typing ?notacommand, the bot should not say "notacommand" doesn't exist in
        // most situations. It's partially good practise and partially developer preference

        /** BLACKLIST LIKE THIS
         * if(event.getAuthor().toString().equals("<@!268531668313178114>"))
         {
         return;
         }**/
        String[] argArray = event.getMessage().getContent().split(" ");

        System.out.println(event.getAuthor().getName() + " (in " + event.getMessage().getChannel().getName() + " at " + event.getMessage().getTimestamp() + "): " + event.getMessage().getContent());
        // First ensure at least the command and prefix is present, the arg length can be handled by your command func
        if(argArray.length == 0)
            return;

        if(argArray[0].toString().equals("<@205395140527783957>"))
        	BotUtils.sendMessage(event.getChannel(), event.getAuthor().mention());

        //No longer same as hell my dude.
        /*if (argArray[0].toString().equals("Same.") || argArray[0].toString().toUpperCase().equals("SAME") || argArray[0].toString().equals("same.") || argArray[0].toString().equals("Me too.")) {
			if(event.getAuthor().getStringID().equals("163971321435389952"))
				return;
        	BotUtils.sendMessage(event.getChannel(), response[random.nextInt(5)]);
		}*/

        // Check if the first arg (the command) starts with the prefix defined in the utils class
        if(!argArray[0].startsWith(BotUtils.BOT_PREFIX))
            return;

        // Extract the "command" part of the first arg out by ditching the amount of characters present in the prefix
        String commandStr = argArray[0].substring(BotUtils.BOT_PREFIX.length());

        // Load the rest of the args in the array into a List for safer access
        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0); // Remove the command

        // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists
        if(commandMap.containsKey(commandStr))
            commandMap.get(commandStr).runCommand(event, argsList);
        else
        {
            File file = new File("src/customcommands.txt");
            try(Scanner sc = new Scanner(file))
            {
                while(sc.hasNextLine())
                {
                    String line = sc.nextLine();
                    String[] array = line.split("ð");
                    if(array[2].trim().equals(commandStr))
                    {
                        String splitline[] = line.split("ð");
                        BotUtils.sendMessage(event.getChannel(), splitline[3].trim());
                        return;
                    }
                }
            }
            catch (IOException e)
            {
            }
        }

    }

    //A bunch of user joins/leaves for servers, no need to touch.
    //When a new user joins a server, this message will be sent to the #general of that server.
    @EventSubscriber

    public void onJoinEvent(UserJoinEvent event) throws Exception
    {
        BotUtils.sendMessage(event.getGuild().getChannelsByName("general").get(0), "Welcome, " + event.getUser().mention() + ", to " + event.getGuild().getName() + "!");
    }

    //When a user leaves a server, this message will be sent to the #general of that server.
    @EventSubscriber
    public void onLeaveEvent(UserLeaveEvent event) throws Exception
    {
        BotUtils.sendMessage(event.getGuild().getChannelsByName("general").get(0), event.getUser().getName() + " has left " + event.getGuild().getName() + ".");
    }

    //When a user is banned from a server, this message will be sent to the #general of that server.
    @EventSubscriber
    public void onBanEvent(UserBanEvent event) throws Exception
    {
        BotUtils.sendMessage(event.getGuild().getChannelsByName("general").get(0), event.getUser().getName() + " has been banned.");
    }

    //When a user joins a voice channel, the bot will send a message to the #voice in that server, saying what channel they joined.
    @EventSubscriber
    public void onVoiceJoinEvent(UserVoiceChannelJoinEvent event) throws Exception
    {
        if(event.getUser().getName().equals("Botkatchi"))
            return;
        try
		{
			BotUtils.sendMessage(event.getGuild().getChannelsByName("voice").get(0), event.getUser().getDisplayName(event.getGuild()) + " has joined **" + event.getVoiceChannel() + "**.");
		}
		catch(DiscordException e)
		{
			return;
		}
    }

    //When a user moves from one voice channel to another, the bot will send a message to the #voice in that server, saying what channels they moved between.
    @EventSubscriber
    public void onVoiceMoveEvent(UserVoiceChannelMoveEvent event) throws Exception
    {
        if(event.getUser().getName().equals("Botkatchi"))
            return;
        try
		{
        	BotUtils.sendMessage(event.getGuild().getChannelsByName("voice").get(0), event.getUser().getDisplayName(event.getGuild()) + " has moved from **" + event.getOldChannel().getName() + "** to **" + event.getNewChannel().getName() + "**.");
		}
		catch(DiscordException e)
		{
			return;
		}
    }

    //When a user leaves a voice channel, the bot will send a message to the #voice of that server, saying what channel they left.
    @EventSubscriber
    public void onVoiceLeaveEvent(UserVoiceChannelLeaveEvent event) throws Exception
    {
        if(event.getUser().getName().equals("Botkatchi"))
            return;
        try
		{
			BotUtils.sendMessage(event.getGuild().getChannelsByName("voice").get(0), event.getUser().getDisplayName(event.getGuild()) + " has left **" + event.getVoiceChannel() + "**.");
		}
		catch(DiscordException e)
		{
			return;
		}
    }



}
