import net.dv8tion.jda.api.entities.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commands {
    public static void countCommand(User user, Message message, TextChannel channel) {
        //Make sure a user is mentioned
        if (message.getMentionedUsers().isEmpty()) {
            channel.sendMessage("Please mention at least one user.").queue();
            return;
        }

        Message messageToEdit = channel.sendMessage("Retrieving message count(s)...").complete();

        StringBuilder messageToSend = new StringBuilder();
        for (User mentionedUser : message.getMentionedUsers()) {
            try {
                //Get user's row
                ResultSet resultSet = Main.statement.executeQuery("SELECT * FROM counter " +
                        "WHERE guildid = '" + message.getGuild().getId() + "' " +
                        "AND userid = '" + mentionedUser.getId() + "'");
                if (!resultSet.next()) {
                    //No results
                    messageToSend.append("No results for " + mentionedUser.getAsMention() + ". They probably haven't sent any messages.\n");
                    continue;
                }
                //There are results
                //Get user's count
                int countIndex = resultSet.findColumn("count");
                int count = resultSet.getInt(countIndex);

                //Add user to message
                messageToSend.append(mentionedUser.getAsMention() + ": " + count + " messages.\n");

            } catch (SQLException throwables) {
                System.out.println("Caught an exception: ");
                throwables.printStackTrace();
            }
        }
        //Send the message
        messageToEdit.editMessage(messageToSend).queue();
    }

    public static void joinCommand(Guild guild, Message message, TextChannel channel) {
        //Make sure a user is mentioned
        if (message.getMentionedUsers().isEmpty()) {
            channel.sendMessage("Please mention at least one user.").queue();
            return;
        }

        //Vars
        List<User> mentionedUsers = message.getMentionedUsers();
        OffsetDateTime currentDate = OffsetDateTime.now();
        StringBuilder messageToSend = new StringBuilder();

        //Loop through all mentioned users
        for (User mentionedUser : mentionedUsers) {
            Member member = guild.retrieveMember(mentionedUser).complete();
            OffsetDateTime joinedDate = member.getTimeJoined();

            Duration difference = Duration.between(joinedDate, currentDate);
            messageToSend.append(mentionedUser.getName() + " has been in the server for **" + difference.toDays() + "** days.\n");
        }

        channel.sendMessage(messageToSend).queue();
    }
}
