import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main extends ListenerAdapter {

    static JDA jda;
    static String token = System.getenv("COUNTER_BOT_TOKEN");

    //SQL
    static Connection connection;
    static Statement statement;

    public static boolean startBot() throws InterruptedException {
        JDABuilder preBuild = JDABuilder.createDefault(token);
        preBuild.setActivity(Activity.listening("c!count"));
        try {
            jda = preBuild.build();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        jda.addEventListener(new Main());
        jda.awaitReady();
        return true;
    }

    public static void main(String[] args) {
        try {
            startBot();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String url = System.getenv("COUNTER_BOT_SQL_URL");
        try {
            connection = DriverManager.getConnection(url,System.getenv("COUNTER_BOT_SQL_USERNAME"),System.getenv("COUNTER_BOT_SQL_PASSWORD"));
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        User user = event.getAuthor();

        String prefix = "c!";
        String messageRaw = event.getMessage().getContentRaw();
        String messageNoPrefix = messageRaw.substring(prefix.length());

        countMessage(guild, user);

        //Must start with prefix
        if (!messageRaw.startsWith(prefix))
            return;

        if (messageNoPrefix.startsWith("count")) {
            Commands.countCommand(user, event.getMessage(), event.getChannel());
        }

    }

    public static void countMessage(Guild guild, User user) {
        String guildId = guild.getId();
        String userId = user.getId();

        try {
            //Make sure a counter exists for the user
            statement.executeUpdate("INSERT INTO counter(guildid, userid, count)" +
                    "SELECT '" + guildId + "', '" + userId + "', 0 " +
                    "WHERE NOT EXISTS (SELECT 1 FROM counter WHERE guildid = '" + guildId + "' AND userid = '" + userId + "')");

            //Add one to the counter
            statement.executeUpdate("UPDATE counter " +
                    "SET count = count + 1 " +
                    "WHERE guildid = '" + guildId + "' " +
                    "AND userid = '" + userId + "'");

        } catch (SQLException e) {
            System.out.println("Caught an exception: ");
            e.printStackTrace();
        }

    }
}
