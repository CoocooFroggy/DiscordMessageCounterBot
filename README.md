# DiscordMessageCounterBot
#### Environment Variables
- `COUNTER_BOT_TOKEN`: Discord bot's token
- `COUNTER_BOT_SQL_URL`: URL starting with `jdbc:`. For example, `jdbc:postgresql://XXX.amazonaws.com:0000/XXXXXXXXX`
- `COUNTER_BOT_SQL_USERNAME`: Database username
- `COUNTER_BOT_SQL_PASSWORD`: Database password

#### Database
- Database is Postgresql. Table is called "counter" and has 3 columns. Text `guildid`, text `userid`, int `count`.

### Bot Usage
- `c!count [User mentions]`: Returns how many messages each user has sent. e.g. `c!count @CoocooFroggy @JohnnnnyKlayy`
- `c!join [User mentions]`: Returns how many messages each user has sent. e.g. `c!count @CoocooFroggy @JohnnnnyKlayy`
