/*--------------------------------------------------------

1. Name / Date: Wisa Terhune-Praphruettam, 9/27/2020

2. Java version used, if not the official version for the class:

Version: 1.8

3. Precise command-line compilation examples / instructions:

> javac JokeServer.java
> java JokeServer

>javac JokeClient.java     #this is the main one
>java JokeClient

>javac JokeClientAdmin.java
>java JokeClientAdmin

4. Precise examples / instructions to run this program:

In 3 separate shell windows or terminals:

> cd to the terminal where the java files are located

Window 1:
I find it useful to get this one running first.
> javac JokeServer
> java JokeServer

Window 2:
> javac JokeClient
> java JokeClient
In the terminal, enter the Name and presses <Enter> to begin.
A Response from the server should appear

Window 3:
> javac JokeClientAdmin
> java JokeClientAdmin

5. List of files needed for running the program.

 a. JokeServer.java
 b. JokeClient.java
 c. JokeClientAdmin.java

5. Notes:

This Joke Client class is the main interface for the user. It will print out basic information and
asks the user to enter their name, which will become their user name. Also, it will generate a random
user ID and prints the number to the user. Otherwise, the Joke Client will connect with the Joke Server with
vital information such as userID, userName, userInput. UserInput is this case, will be <enter>.

I tried implementing multiple Clients but the implementation was not smooth so I focused on getting the default port working.
----------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class JokeClient {

    static String serverName = "localhost"; //default server name is local host
    static int port = 4545; //default port

    //-------- STATUS-----------//
    /* This is to address the second client server but commented out due to unsuccessful run
      static String serverName2; //port 4546 server name
     */

    public static void main(String args[]) {

        // boolean serverSwitch = false;  This is if switch server implementation worked correctly

        System.out.println("Wisa Terhune-Praphruettam's Joke Client. \n");

        if (args.length < 1) {
            serverName = "localhost"; //default server name is localhost, especially if user just presses enter
            port = 4545;
            System.out.format("Using default server: %s, Port: 4545. " +
                    "Joke Client Administrator will run on port 5050 ", serverName);
        }
        else serverName = args[0];

        //-------STATUS-----//
        /* this is if we have multiple client servers and setting the second server name
        else if (args.length == 1) {
            serverName = args[0]; //server name is the first field in the arg[0]
            //showing the first port as 4545 the same as default and printing to user
            port = 4545;
            System.out.format("Using server 1: %s, Port: 4545. " +
                    "Joke Client Administrator will run on port 5050");
        }

        else { //setting port to 4546 with the second server name and printing to user
            serverName2 = args[1];
            port = 4546;
            System.out.format("Using server 2: %s, Port: 4546. " +
                    "Joke Client Administrator will run on port 5051.");
        }
        */

        System.out.format("Using default server: %s, Port: 4545. " +
                "Joke Client Administrator will run on port 5050 ", serverName);

        //setting up input to read data from the user
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("\nHello and welcome! Please enter your name: ");
            System.out.flush(); //clears prior data

            String userName;
            userName = in.readLine(); //setting the userName to be what the user writes down

            System.out.printf("Hello %s!\nHoping you are staying staying safe in the pandemic! \n", userName); //printing more fields

            /*
            generate a random user Id for the user name up to 5000 and convert to a String.
            The range could be more than just 5000.
             */
            String userId = String.valueOf((0 + (int) (Math.random() * ((5000 - 0) + 1))));
            System.out.println("Your user ID is: " + userId + "\n"); //letting user know their user ID

            String userInput;

            do {
                System.out.println("Press enter to begin!");

                //System.out.println("If you want to change the server, type 'switch'."); for switch client server implementation

                System.out.flush(); //clears out anything that was already in the input buffer
                userInput = in.readLine(); //reads the user's action
                System.out.flush();

                //if the user did not enter 'quit', initiate server connection with the required fields
                if (userInput.indexOf("quit") < 0) {

                        startServerConnection(serverName, userName, userInput, userId);

                        /* ------STATUS---------
                    This is how I would have liked to handle the switch client server situation but never got it to work properly
                    if ((JokeClient.port == 4545) && (userInput.equalsIgnoreCase("switch"))) {
                        serverSwitch = true; //update switch
                        //we will create server connection on a different port: 4546
                        startServerConnection(serverName2, userName, 4546, userId);
                        System.out.println("We are switching to port 4546 and calling the Joke Server with this port number.");
                        JokeClient.port = 4546; //update the port just in case
                        serverSwitch = false; //update
                    }
                    //switching back to server 1 port 4545
                    else if ((JokeClient.port == 4546) && (userInput.equalsIgnoreCase("switch"))) {
                        serverSwitch = true; //update switch
                        //we will create server connection on a different port: 4545
                        startServerConnection(serverName1, userName, 4545, userId);
                        System.out.println("We are switching to port 4545 from port 4546 and calling the Joke Server on port 4545.");
                        JokeClient.port = 4545; //update the port
                        serverSwitch = false; //update switch
                    } else if (JokeClient.port == 4545 && userInput.equalsIgnoreCase("Joke")){
                        //just continue with the current port
                        updateMode = true;
                        //continue making server connections with the current port if there is no switching
                        startServerConnection(serverName1, userName, 4545, userId);
                    } else if (JokeClient.port == 4545 && userInput.equalsIgnoreCase("Proverb")) {
                        //just continue with the current port
                        updateMode = true;
                        //continue making server connections with the current port if there is no switching
                        startServerConnection(serverName1, userName, 4545, userId);
                    } else if (JokeClient.port == 4546 && userInput.equalsIgnoreCase("Joke")) {
                        //just continue with the current port
                        updateMode = true;
                        //continue making server connections with the current port if there is no switching
                        startServerConnection(serverName1, userName, 4546, userId);
                    } else if (JokeClient.port == 4546 && userInput.equalsIgnoreCase("Proverb")) {
                        //just continue with the current port
                        updateMode = true;
                        //continue making server connections with the current port if there is no switching
                        startServerConnection(serverName1, userName, 4546, userId);
                    }
                    */

                }
            } while (userInput.indexOf("quit") < 0);
            //the user decided to leave the program instead with "quit" as input
            System.out.println("Leaving the program...We are sorry to see you go :( ");
            System.out.println("You have left the Joke Server program.");
            System.exit(0); //exit the program
        } catch (IOException e) {
            //printing error messages to help with debugging
            System.out.println(" There was an issue with the Joke Client making the call on port " + JokeClient.port);
            e.printStackTrace();
            e.getMessage();
            e.getCause();
        }
    }

    /*code is from CS435 with minor add ons.
    This method makes a socket connection call with the appropriate port.
    In this case, the only implementation that worked is the default port, 4545.
    If switching the client server worked, then it will make the socket call
    with whatever port was assigned for the server.
    */
    static void startServerConnection(String serverName, String userName, String userInput, String userId) {

        Socket socket; //new Socket object to communicate
        BufferedReader fromServer; //input data from the server
        PrintStream sendingToServer; //output and/or send data to the server
        String textFromServer; //texts from the server

        try {
            //setting fields to communicate and setting the socket with serverName and default port number, 4545
            socket = new Socket(serverName, 4545);

            //reading incoming inputs from the socket's server
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //sends data and/or writing the info to the server
            sendingToServer = new PrintStream(socket.getOutputStream());

            //sending the fields to the server
            //The server must receive the data in this order: userName, userId, and userInput
            sendingToServer.println(userName); //sending username to the server
            sendingToServer.println(userId); //sending the randomized ID to server
            sendingToServer.println(userInput); //sending user's joke/proverb preference to the server

            sendingToServer.flush(); //clears data sent to the server

            /*Reads line responses from the server
            and block while synchronously waiting.
            Since our jokes have 4 in the array, maximum size of 6 should be plenty
            */
            for (int i = 0; i <= 5; i++) {
                textFromServer = fromServer.readLine(); //reading socket's each line as long as i < 4

                if (textFromServer != null) { //checking if field is null
                    //Printing text from the server, which can be either jokes or proverbs
                    System.out.println("Text from server: " + textFromServer);
                }
            }
            socket.close(); //close socket

        } catch (UnknownHostException e) {
            //print messages for debugging purposes
            e.printStackTrace();
            e.getMessage();
            e.getCause();
        } catch (IOException e) {
            //print messages for debugging purposes
            System.out.println("Socket error");
            e.printStackTrace();
            e.getMessage();
            e.getCause();
        }
    }
    //Code is from CS435
    public static String toText(byte ip[]) { //Make portable for 128 bit format
        StringBuffer result = new StringBuffer();

        for (int x = 0; x < ip.length; ++x) {
            if (x > 0) {
                result.append(".");
            }
            result.append((0xff & ip[x]));
        }
        return result.toString();
    }

}