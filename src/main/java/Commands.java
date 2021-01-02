import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

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
}
