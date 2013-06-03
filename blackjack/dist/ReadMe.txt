BLACKJACK PROTOCOL DISTRIBUTION (PRE-COMPILED)
==============================================

CS544 Computer Networks Spring 2013
Group 10 Members
o Jennifer Lautenschlager
o Constantine Lazarakis
o Carol Greco
o Duc Anh Nguyen

This file is where the 'ant jar' task compiles the jar file. It also
contains some starter databases of users and blackjack games, as well
as script files for Unix and Windows to run the system. You will need
a Java 7 interpreter in your path (java.exe on Windows, java on Linux)
to run. Also, your firewall must be configured such that TCP/IP 
connections over port 55555 are allowed.

1. run-server.bat or run-server.sh
   Windows or Unix commands for starting the server on the local
   machine. It requires no parameters. By default, it will show 
   Java logging messages of the INFO level and above.
   
2. run-client.bat or run-client.sh
   Windows or Unix commands for starting the command-line client. If
   no parameters are given, it will attempt to find a suitable server
   on the LAN. An optional single parameter can be used to explicitly
   specify the host (e.g., tux64-12.cs.drexel.edu, 192.168.224.142). 
   Part of the client uses a GUI frame for optionally displaying 
   message traffic, so if running on Linux you must have your X11 
   DISPLAY exported, or else run in headless mode (see below).
   
3. run-client-headless.bat or run-client-headless.sh
   Windows or Unix commands that are essentially the same as above,
   except in headless mode no GUI (used to optionally display message
   traffic for debug purposes) is available. The command-line, menu-
   driven UI for logging into the blackjack server and playing
   games is still fully functionally. Use this is you are running
   on Tux in a putty window, for example, without your X11 DISPLAY
   variable exported and tunneled properly.
   
3. setup-users.bat or setup-users.sh
   Windows or Unix commands for starting a simple command-line utility
   for adding, removing, and viewing users in the local flatfile
   database used by the server. RESTARTING THE SERVER IS REQUIRED FOR
   CHANGES TO TAKE EFFECT. A default configuration is provided with the 
   following accounts defined:
   
   o Username: user1
     Password: password
     Balance: $1000
   o Username: user2
     Password: password
     Balance: $2000
   o Username: user3
     Password: password
     Balance: $3000
   o Username: user4
     Password: password
     Balance: $4000
     
   As clients play, their balance will be adjusted according to the
   bets they place, and whether they win or lose.
   
4. setup-games.bat or setup-games.sh
   Windows or Unix commands for starting a simple command-line utility
   for adding, removing, and viewing hosted games in the local flatfile
   database used by the server. RESTARING THE SERVER IS REQUIRED FOR CHANGES
   TO TAKE EFFECT. A default configuration is provided with the following 
   games defined:
   
   game1) Blackjack [Bets from $100 - $500, 1-4 players, with 2 decks used]
    o House rule #1
    o House rule #2
   game2) Blackjack [Bets from $0 - $0, 1 player, with 3 decks used]
    o Practice game only
   gmae3) Blackjack [Bets from $10 - $250, 1-6 players, with 4 decks used]
    o Example rule number one: text would go here
   game4) Blackjack [Bets from $100 - $1000, 1-8 players, with 10 decks used]
     