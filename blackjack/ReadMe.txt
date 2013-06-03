BLACKJACK PROTOCOL CS544 GROUP 10 PROJECT
=========================================
CS544 Computer Networks Spring 2013
Group 10 Members
o Jennifer Lautenschlager
o Constantine Lazarakis
o Carol Greco
o Duc Anh Nguyen

This file explains how to run the blackjack client and server that
Group 10 of CS544, Spring 2013 class created for their group project.
The project is implemented entirely in Java 7.

-----------------------------------------------------------------------------
1. Quickstart Instructions
-----------------------------------------------------------------------------

IF YOU WANT TO COMPILE THE CODE: You will need a Java 7 compiler and a 
version of ant (known to work on Ant 1.8.4 binary version) in your path. From 
this directory, run 'ant clean' to clean out the compiled code, compiled 
jarfile, and javadocs. Then run 'ant jar' to compile and create the jar file.
Compiled code will be in the bin subdirectory. Compiled jar in the dist
subdirectory.

TO RUN THE CODE: You will need a Java 7 interpreter in your path. cd to the
dist subdirectory. To start the server on the local host, run either the
run-server.bat file (on Windows) or the run-server.sh file (on Linux). To
run the server, run either the run-client.bat file (on Windows) or the
run-client.sh file (on Linux), passing as a single command line parameter
the host you wish to connect to (e.g., run-client.sh tux64-12.cs.drexel.edu).
If you aren't compiling locally, you will be using the pre-compiled jar file
that we included.

PORT INFORMATION: The protocol uses TCP over port 55555. Your firewall will
need to be configured appropriately if you wish to run a client against a
remote server.

USER INFORMATION: A default database of users has been created. See the
ReadMe.txt file in the dist subdirectory for details. In general, you can
use usernames of "user1" through "user4" with a password of "password" to
log in. Only one simultaneous login per user is allowed.

-----------------------------------------------------------------------------
2. The user interface
-----------------------------------------------------------------------------

The user interface is a simple command-line interface allowing the user to
play blackjack. A user can only be logged in once at a time, and can only
play one game at a time. Money is 'virtual', stored in an account balance.
The interface is menu driven, and includes some options purely for the
purpose of showing off protocol functionality; these are indicated 
parenthetically as being for debug purposes.

Aside from the command-line interface, a graphical debug window exists for
viewing of raw protocol messages sent and received. It can be accessed from
almost any menu, once authenticated, with the 'T' option (for toggling).

-----------------------------------------------------------------------------
3. Contents of this zip file
-----------------------------------------------------------------------------

The file structure included is as follows:

blackjack                         Root directory of project
|-- src                           Our source code (directory plus subs)
|-- config                        Supplied configuration files
|    |-- blackjack.keystore       Keystore with X509 security certificate
|    |-- commands.txt             Server configuration file
|    +-- ReadMe.txt               More details on above files
|-- dist                          Distribution directory
|    |-- games_serialized         Default flatfile database of games
|    |-- run-client.bat           Windows batch file for client
|    |-- run-client.sh            Linux batch file for client
|    |-- run-server.bat           Windows batch file for server
|    |-- run-server.sh            Linux batch file for server
|    |-- setup-games.bat          Windows batch file for game administration
|    |-- setup-games.sh           Linux batch file for game administration
|    |-- setup-users.bat          Windows batch file for user administration
|    |-- setup-users.sh           Linux batch file for user administration
|    +-- users_serialized         Default flatfile database of users
|-- doc                           Pre-compiled javadocs (directory plus subs)
|-- libs                          Supplied JAR files
|    |-- hamcrest-core-1.3.jar    Required by JUnit
|    |-- junit-4.11.jar           Testing framework
|    +-- mockito-all.1.9.5.jar    Mocking framework to extend JUnit
|-- licenses                      Licenses of included third-party code
|-- build.xml                     Ant buildfile
+-- ReadMe.txt                    This file

-----------------------------------------------------------------------------
4. Where things are implemented
-----------------------------------------------------------------------------

In addition to our group paper, the javadocs for the project contain some
information about where different aspects of the protocol are implemented.
You can use the pre-generated javadocs found in the doc/index.html directory.
If you wish to regenerate the javadocs youself, simply run the 'ant javadoc'
task from this directory. Top-level commentary is at the bottom of the main
index page, with supporting details in documentation on various classes and
packages. 

Additionally, you can search the source code for the following keywords:

1. STATEFUL
   Code related to maintaining and checking protocol state
2. CONCURRENT
   Code related to supporting concurrent connections
3. SERVICE
   Code related to the service binding to a hard-coded port
4. CLIENT
   Code related to the client allowing specification of host
5. UI
   Code related to the user interface
6. SECURITY
   Code related to security of the client and server

-----------------------------------------------------------------------------
5. Ant tasks
-----------------------------------------------------------------------------

We use an Ant buildfile for our build system. The following common tasks
are supported:

o init
  Ensure needed directory structure is in place
o clean
  Deleted generated code, jars, and javadocs
o compile
  Compiles code (excludes test-specific code) into bin directory
o jar
  Create backjack.jar file in dist directory
o run-server
  Runs the server, only showing errors (not warnings or debug info)
o run-client
  Runs the client, only showing errors (not warnings or debug info)
  Requires -Dest=xxx.xxx.xxx.xx for specifying host
o run-client-headless
  Runs the client in headless mode (no GUI), only showing errors
  Requires -Dest=xxx.xxx.xxx.xx for specifying host
o run-client-local
  Runs the client, only showing errors (not warnings or debug info)
  Connects to the localhost
o javadoc
  Generates javadocs in the doc directory
o user-manager
  Runs a command-line tool for administering users within the server.
  REQUIRES SERVER RESTART to recognize changes
o game-manager
  Runs a command-line tool for administering games within the server
  REQUIRES SERVER RESTART to recognize changes

The following tasks are related to testing:

o compile-test
  Compiles code (includes test-specific code) into bin directory
o test
  Run JUnit tests
o run-server-warning
  Run the server, showing warning and error messages (not debug info)
o run-server-info
  Run the server, showing errors, warnings, and debug info
o run-client-local-warning
  Run the client, showing errors and warnings (not debug info)
o run-client-local-info
  Run the client, showing errors, warnings, and debug info (not low-level)
o run-client-local-debug
  Run the client, showing even the most low-level debug messages

-----------------------------------------------------------------------------
6. External tools and libraries used
-----------------------------------------------------------------------------

The following external tools and libraries were used.

 1. Java compiler, interpeter, javadoc creator
   http://java.sun.com
   The language we implemented our project in.
 2. Apache Ant
   https://ant.apache.org/
   Our buildfile system.
 3. JUnit4 jar file
   http://junit.org/
   Our testing framework
 4. Hamcrest 1.3 jar file
   http://junit.org/
   Required by JUnit
 5. Mockito 1.9.5 jar file
   https://code.google.com/p/mockito/
   Mocking framework to aid in implementing JUnit tests
 6. eclEmma
   http://www.eclemma.org/
   Testing tool used to verify code coverage of tests
 7. Single Java file from the lcpn project
    https://code.google.com/p/lpcn/source/browse/trunk/src/lpcn/xbee/LimitLinesDocumentListener.java
    Used in our debugging message monitor to limit lines shown.
 8. Eclipse
   http://www.eclipse.org/
   Our IDE of choice
 9. git
   http://git-scm.com/
   Our revision control system
10. Java keytool
   http://docs.oracle.com/javase/6/docs/technotes/tools/solaris/keytool.html
   Used for self-signing X509 certificates and creating a keystore
11. Github web site
   https://github.com/
   Where our code is hosted: https://github.com/rabidstoat/blackjack

Relevant licenses of included code can be found in the licenses subdirectory.
   