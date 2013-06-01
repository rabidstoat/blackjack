BLACKJACK PROTOCOL CONFIGURATION FILES
======================================
CS544 Computer Networks Spring 2013
Group 10 Members
o Jennifer Lautenschlager
o Constantine Lazarakis
o Carol Greco
o Duc Anh Nguyen

The contents of this directory are as follows.

1. ReadMe.txt
   This file.
   
2. blackjack.keystore
   A security keystore containing a single self-signed X509 certificate
   used by both the blackjack server and client for implicit TLS
   negotiations. The password for both the certificate and the keystore
   is "password". This was created with Java's toolset.
   
3. commands.txt
   Server configuration file specifying what commands, in the form of Java
   classes that implement a specific command interface, are to be loaded
   into the server (via reflection).