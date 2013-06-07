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
TABLE OF CONTENTS
-----------------------------------------------------------------------------

1. Quickstart instructions
2. Robustness analysis
3. Extra credit
4. Where things are implemented
5. The user interface
6. Contents of the zip file
7. Ant tasks
8. External tools and libraries used

-----------------------------------------------------------------------------
1. Quickstart instructions
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
2. Robustness analysis
-----------------------------------------------------------------------------

The protocol requires the use of TLS for encryption, which we leverage for
the security benefits it provides; no commands or responses are sent over
clear text. The protocol attempts to hide information about certain details
of the protocol or underlying data in its error messages as well. An example
of this is that when use authentication fails, the protocol error message 
merely notes that it failed. It does not verify if the username given
is valid or not, to discourage attempts at hacking into accounts that are 
verified to exist.

Another thing that contributes to the robustness is that, despite there 
being no explicit code dealing with non-repudiation in the protocol (e.g.,
there are no sequence numbers), in most cases 'replaying' a packet will
not cause undue harm. The protocol is stateful, for one thing. Commands
such as the bet command, if issues multiple times (maliciously or purposefully)
will not circumvent the bet limit on a game, it will merely update the
bet value needlessly to the same thing. Some commands, such as replaying
a 'hit' command, could have unintended and detrimental consequences, however.
On the other hand, there are no commands that directly affect any user's
bank balance in the protocol, avoiding that as a vulnerability.

As far as fuzzing, we have done both manual and automated fuzz testing
of our protocol, albeit of a simplistic nature. The javadocs for the
drexel.edu.blackjack.test.SimpleFuzzTest class provides details on the
approach. In general, the automated fuzz testing uses some default
values (which can be overridden by system properties) to establish
a number of test threads of varying types, which are then executed in
parallel. The three types of test threads include:

  1. Unathenticated binary streams: This type of thread connects, does
     the TLS handshake, and immediately starts streaming a random number
     of binary sequences of random values with random lengths over the
     socket. The range for the possible number of sequences sentence, as
     well as the range of the number of bytes in any given sequence,
     follow a linear distribution within either default or overriden
     ranges.
     
  2. Authenticated binary streams: This is similar to test #1, with the
     exception that the user is authenticated following the TLS handshake
     and prior to sending the random data.
     
  3. Test strings: This test sends ASCII text strings to the server, which
     is what the protocol specifies. However, there is a randomization
     algorithm used to send randomly selected valid or invalid command
     types, with a variable number of string or numeric parameters.

Fuzz testing did discover a problem early on where sending large, 
unterminated streams of binary data could crash the server with
an Out Of Memory error. This was corrected by enforcing the 1024-byte
limit on single lines of input, and terminating connections for any client
that violated this (without reading the extraneous data).

Our testing is detailed in Section 15 of our updated protocol paper. In
general we used a combination of methods that included JUnit tests (and
coverage reports thereof), automated fuzz testing, a message monitor GUI
for viewing unencrypted message traffic, a debug client that allowed us
to enter erroneous commands (e.g., wrong arguments, wrong state, etc.),
java logging for diagnostic prints, and general 'hands on' user testing
of the software.

However, the implementation was done in a very short time frame and is not
entirely secure. Passwords are stored in plain text, for example, and anyone
with access to an editor could change bank account balances at will. This is
on the implementation side, and not a vulnerability of the protocol itself,
owing more to the quick turnaround nature of this project.

-----------------------------------------------------------------------------
3. Extra credit
-----------------------------------------------------------------------------

We did implement the extra credit. The limitation is that a server must be
run on the local area network, and that only IPv4 is supported. Starting 
the client without a parameter for the server will search for this server 
and, if found, connect to the default port.

The approach taken is a multicast text-based request-response pair of messages
on an agreed UDP port and broadcast group. The client generates the request up
to ten times, using a backoff strategy to incrementally increase the amount
of time between requests. Repeating the requests is done because UDP is not a
reliable protocol, and the backoff strategy is used to avoid undue network
congestion.

As a server may be connected to multiple networks through multiple network
interfaces, it is possible that it will have multiple IP addresses under which
it identifies itself. Depending on where a client is broadcasting from, some
of these IP addresses may not be appropriate to us. For example, a server
might be on two totally separate LANs, neither of which can see one another,
so that a location request from a client on the first network should be given
one address, and a request from a client on the second network be given a
different address. The server attempts to handle these issues by keeping a 
list of all the addresses it associates itself with for any network interfaces
it has, and along with the addresses, the corresponding subnet mask for 
identifying addresses on the same subnet. When a client request is received,
it knows the address it came from. This, combined with the list of addresses
and subnet masks, is used to find the first host address variant that is
located on the same subnet, which is then broadcast in response. (It is 
because of this implementation for handling the case where a server is on
multiple LANs which might not have visibility into one another that only
IPv4 is supported. It is probably possible to do something similar for IPv6,
but this was not implemented.)

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
7. SECURITY
   Code related to our extra credit

-----------------------------------------------------------------------------
5. The user interface
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
6. Contents of this zip file
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
|-- ReadMe.txt                    This file
+-- Updated_Design_Team_10.pdf    Our updated protocol specification

-----------------------------------------------------------------------------
7. Ant tasks
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
o default-fuzz
  Small fuzz test, sending in random binary and text strings from multiple threads
o big-fuzz
  More threads, more commands/messages sent, and longer binary strings
o huge-fuzz
  Even MORE threads, MORE commands/messages sent, and even longer binary strings

-----------------------------------------------------------------------------
8. External tools and libraries used
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
12. Camtasia
   http://www.techsmith.com/camtasia.html
   Used in video production
13. Adobe After Effects
   http://www.adobe.com/products/aftereffects.html
   Used in video production

Relevant licenses of included code can be found in the licenses subdirectory.
   