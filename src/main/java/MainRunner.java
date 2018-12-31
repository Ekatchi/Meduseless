import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.MessageBuilder;

import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by declan on 03/04/2017.
 * Used by Ka, last updated 11/02/2017.
 */
public class MainRunner {

    public static void main(String[] args) {
		Scanner diddleboard = new Scanner(System.in);
		String token  = System.getenv("bot_token");
		try {
			IDiscordClient cli = BotUtils.getBuiltDiscordClient(token);
			// Register a listener via the EventSubscriber annotation which allows for organisation and delegation of events
			cli.getDispatcher().registerListener(new CommandHandler());
			// Only login after all events are registered otherwise some may be missed.
			cli.login();
			Random random = new Random();
			Timer timer = new Timer();
			TimerTask hourlyTask = new TimerTask() {
				@Override
				public void run() {
					try {
						updateBotPresence(cli);
						System.out.println("------------Updated--------------");
					} catch (Exception e) {
						IDiscordClient thisclient = BotUtils.getBuiltDiscordClient(token);
						thisclient.logout();
						thisclient.login();
						System.out.println("-------------restarted-------------");
					}
				}
			};

			while (true) {
	/*
            498171887080439838 < General chat for clownfiesta
            162014083409772545 < Code_talk for clownfiesta
            510860374132654080 < bot_playground for clownfiesta
            471508288794787862 < #gaming-gacha on #gaming-gacha
            471509973185200148 < general on #gaming-gacha
            471514042826096640 < #bot_spam on #gaming-gacha
            <@205395140527783957> < bot's user ID for pinging purposes

*/
				Long channelID = Long.valueOf(diddleboard.nextLine());
				String message = diddleboard.nextLine();
				try {
					new MessageBuilder(cli).withChannel(channelID).withContent(message).build();
					updateBotPresence(cli);
				} catch (Exception e) {
					System.err.println("Message could not be sent with error: ");
					e.printStackTrace();
				}


				timer.schedule(hourlyTask, 0l, 1000 * 60 * 60);
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}



    public static void updateBotPresence(IDiscordClient client)
    {
        Random random = new Random();
        String[] status = {"With 0's and 1's", "With Literal Cancer","Circle Simulator 2018", "100% Salt Juice", "League of Legends: Game of the Year Edition", "with her sisters", "Degenerate Dredge", "Infernities in 2018", "with Prinz and Pals", "ＦＵＣＣ: The Game", "With Cardboard Stocks", "with the Bloodghast Brigade", "with the Amalgam-nation", "with the Nar-crew-moeba", "with actual stocks", "Waifu Simulator: Gun Edition", "Vengevine Turbo"};
        client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, status[random.nextInt(16)]); }
    }

