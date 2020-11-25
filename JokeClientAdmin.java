/*--------------------------------------------------------

1. Name / Date: Wisa Terhune-Praphruettam, 9/27/2020

2. Java version used, if not the official version for the class:

Version: 1.8

3. Precise command-line compilation examples / instructions:

> javac JokeServer.java
> java JokeServer

>javac JokeClient.java
>java JokeClient

>javac JokeClientAdmin.java   #this is the main one
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

I wanted to implement port 5051 but the implementation was not smooth, so I focused on port 5050 instead.
I have the commented code as example of how I would implement client switching wit the associated admin port that
would start a connection with the server.

As of right now, the user will see an introduction prompt. The user has two choice: type "Quit" or presses enter.
I believe that it is case sensitive and there is no switching port at the moment.
This class mainly passes the user's input to the Joke Server.
----------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

//The JokeClientAdmin class has an interface and relays the application mode status to the Joke Server.
public class JokeClientAdmin {

    static int JokeClientAdminPort = 5050; //set the port to default 5050
    static String serverName1 = "localhost";
    static String mode = "Joke";

    //------- STATUS ----//
    //for the second admin port. I tried implementing this but it was unsuccessful so the variables are not being used
    static int JokeClientAdminPort2 = 5051;
    static String serverName2 = "localhost";
    static boolean serverSwitch = false;

    public static void main(String args[]) throws IOException {

        System.out.println("Wisa Terhune-Praphruettam's Joke Client Administrator....\n");

        //setting server name and printing associated client administrator port, in this case, client admin port 5050
        //this code is very similar to CS 435
        if (args.length < 1) {
            serverName1 = "localhost"; //default server name is localhost if there is no argument
            System.out.printf("Using Server Name: %s with port 4545 and Client Administration Port Number: 5050\n",
                    serverName1);
        } else if (args.length == 1) {
            serverName1 = args[0];
            System.out.printf("Using Server Name: %s with Port 4545 and Client Administration Port Number: 5050\n",
                    serverName1);
        }
        /*//if we can implement port 5051
        else {
            serverName2 = args[2]; //setting the second server name
            JokeClientAdminPort = 5051; //change the client admin port to 5051
            System.out.printf("Using Server Name: %s with Port 4546 and Client Administration Port Number: 5051\n",
                    serverName2);
        }
         */

        //setting up input from what the user writes in
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            String userInput; //get the status that the user wants to run on

            //System.out.println("You may also enter 'switch' to change the Joke Client Administrator's port");

            do {
                System.out.println("Hello and welcome to Joke Client Administrator." +
                        "\nPlease press enter to begin or press quit.\n");
                System.out.flush(); //clears everything

                userInput = in.readLine(); //read the mode that the user prefers

                System.out.println("Press 'Enter' to begin or you can press 'Quit'");
                startServerConnection(userInput, serverName1);

                //-------------- Status -----------------------//
                /* In this section of the code, I tried to implementing admin port changing from 5050 to 5051 (and vice versa),
                but was unsuccessful. I felt like I was close...

                if (userInput.indexOf("Quit") < 0) { //if user did not press "Quit"
                to handle the switch server situation between port 5050 and 5051...
                if we are currently on client admin port 5050 and we want to switch to client admin port 5051
                    if ((JokeClientAdminPort == 5050) && (userInput.equalsIgnoreCase("switch"))) {
                        serverSwitch = true; //update switch
                        //update the client admin port to talk with 4546 on joke client
                        JokeClientAdmin.JokeClientAdminPort = 5051;
                        System.out.println("Changing the Joke Client Administrator port to 5051...");
                        serverSwitch = false; //update switch
                        startServerConnection(serverName2, JokeClientAdmin.JokeClientAdminPort, JokeClientAdmin.mode, true);
                    }
                    //if we are currently on client admin port 5050 and we want to switch
                    // to client admin port 5050
                    else if ((JokeClientAdminPort == 5051) && (userInput.equalsIgnoreCase("switch"))) {
                        serverSwitch = true; //update switch
                        //update the client admin port to talk with 4545 on joke client
                        JokeClientAdmin.JokeClientAdminPort = 5050;
                        System.out.println("Changing the Joke Client Administrator port to 5050...");
                        serverSwitch = false; //update switch)
                        startServerConnection(serverName1, JokeClientAdmin.JokeClientAdminPort, JokeClientAdmin.mode, true);
                    }
                if the user does not want to update the server and would rather
                 continue with the current 5050 client admin port
                    if (JokeClientAdminPort == 5050 && userInput.equalsIgnoreCase("Joke")) {
                        startServerConnection(serverName1, 5050, userInput, true);
                    }
                    else if (JokeClientAdminPort == 5051 && userInput.equalsIgnoreCase("Joke")) {
                        startServerConnection(serverName1, 5051, userInput, true);
                    }
                    else if (JokeClientAdminPort == 5050 && userInput.equalsIgnoreCase("Proverb")) {
                        startServerConnection(serverName1, 5050, userInput, true);
                    }
                    else if (JokeClientAdminPort == 5051 && userInput.equalsIgnoreCase("Proverb")) {
                        startServerConnection(serverName1, 5051, userInput, true);
                    }
                } */
            }
            while (userInput.indexOf("Quit") < 0);
            //if the user does enter "quit" in the console then leave the Joke Client Admin program
            System.out.println("We are now exiting out of Joke Client Administrator."); //for debugging purposes
            System.out.println("We are now leaving the program."); //debugging purposes
            System.exit(0);

        } catch (IOException e) {
            //for debugging
            e.printStackTrace();
            e.getMessage();
            e.getCause();
            System.out.println("Client Admin Connection to Server error");
        }
    }

    /*This method communicates with the server as 5050. It could also be used for port 5051 if the
     port switching had worked. The main goal is to update the server
    with status "Joke" or "Proverb"
    */
    public static void startServerConnection(String userInput, String serverName) {

        //a majority of the code framework in this method is from CS 435

        Socket socket; //new Socket object to communicate
        BufferedReader receivingFromServer; //input data from the server
        PrintStream sendingToServer; //output and/or send data to the server
        String textFromServer; //texts from the server

        try {
            //create a communication line with server name and incoming port number, which is currently defaulted to 5050
            socket = new Socket(serverName, JokeClientAdminPort);

            sendingToServer = new PrintStream(socket.getOutputStream()); //sending to the server

            receivingFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); //receiving data from server

            sendingToServer.println(userInput);
            sendingToServer.flush(); //clears data

            /*Reads line responses from the server
            and block while synchronously waiting.
            This code is from CS 435 but got rid of the for loop since working code only handles one port, 5050
           */
            textFromServer = receivingFromServer.readLine();
            if (textFromServer != null) { //checking if field is null
                //Printing text from the server, which can be either jokes or proverbs
                System.out.println("Text from server: " + textFromServer);
            }
        } catch (UnknownHostException e) {
            //for debugging purposes
            e.printStackTrace();
            e.getCause();
            e.getLocalizedMessage();
        } catch (IOException e) {
            //for debugging purposes
            System.out.println("Joke Client Admin socket (start server connection) error with port.");
            e.printStackTrace();
            e.getCause();
            e.getMessage();
        }
    }
}
