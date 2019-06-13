# NBA-Sim

The purpose of this project was to create a POSTGRESQL database from data scraped from the nba.com API and simulate games based off of that data. 

## Creating the database
Unfortunately, the data I scraped was via the "scores" section of the NBA API rather than the "stats" section so the process was a little more difficult. 
One aspect that was particularly arduous was the teams were not listed on the play-by-play url, therefore I had to use a different url to get the teams as well as the player data. 
After that data was gathered, I began transforming the team data into SQL queries and entering them into my database. The same idea was then used to create the pbp table but with more road blocks.
I wanted as much data as I could get to make my simulation a little more accurate and leave room for improvements in the future. Unfortunately, the API 
I was using only gave us a text description of each play. 

### Mistakes I've made
The first and most obvious mistake I made was using the incorrect API for what I wished to accomplish. Aside from that, I made other technical mistakes that were due to my lack of database intelligence.
Although, I inadvertantly took advantage of some normalization techniques by separating the players table from the pbp table- I certainly didn't follow good SQL development protocol.
In future versions of this project, I plan to add some indexes and keys to make the database querying more efficient.
