/*--------------------------------------------------------

1. Name / Date: Wisa Terhune-Praphruettam, 9/27/2020

2. Java version used, if not the official version for the class:

Version: 1.8

3. Precise command-line compilation examples / instructions:

> javac JokeServer.java
> java JokeServer

4. Precise examples / instructions to run this program:

In 3 separate shell windows or terminals:

> cd to the terminal where the java files are located

Window 1:
> javac JokeServer
> java JokeServer

Window 2:
> javac JokeClient
> java JokeClient

Window 3:
> javac JokeClientAdmin
> java JokeClientAdmin

5. List of files needed for running the program.

 a. JokeServer.java
 b. JokeClient.java
 c. JokeClientAdmin.java

5. Notes:

I wanted to the server to pay attention to the other ports as well beside default 4545 but the implementation was not smooth,
so I decided to commented the implementation out.

I wasn't sure how to implement the cookie but the static variables, jokeAlreadySeen and proverbAlreadySeen was a start
to keep track of which randomized joke the user saw already

This server class has an thread for the admin that connects with the JokeClientAdmin to see which application mode
we want to run on.

The server's worker class will send a joke/proverb output to the Joke Client after the user enters his/her name.
----------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class JokeServer {

    public static String appMode = "Joke"; //defaul application mode is on Joke

    /*
    These 2 variables below were supposed to be for keeping track of which jokes/proverbs
    were already displayed to the user, especially when we randomize the order.
    The will be split into a char[] later on
    */
    public static String jokeAlreadySeen = "0000";
    public static String proverbAlreadySeen = "0000";

    public static void main(String args[]) throws IOException {

        int queue_length = 6; //already set the queue length as static
        Socket socket; //creating a Socket object that can communicate
        int port = 4545;

        if (args.length < 1) {
            System.out.println("This is Wisa Terhune-Praphruettam's Joke Server starting up..." +
                    "listening at port: 4545\n"); //starting port 4545
        }

        /*-----------------STATUS------------------
        The code below would be implemented if the Joke Server had to handle listening to multiple ports: 4545 and 4546
        else if (args.length == 1) {
            port = 4545;
            System.out.println("This is Wisa Terhune-Praphruettam's Joke Server starting up..." +
                    "listening at port: 4545\n");
            System.out.println("The Joke Administration Client will be using port 5050.\n");

        }
        else {
            //port 4546
            System.out.println("This is Wisa Terhune-Praphruettam's Joke Server starting up..." +
                    "listening at port: 4546\n");
            System.out.println("The Joke Administration Client will be using port 5051.\n");
            port = 4546; //change the port
            if (isJokeMode.equalsIgnoreCase("Joke")) System.out.println("Running on Joke mode...");
            else System.out.println("Running on proverb mode...");
        }
        */


        /*
        Need to communicate with the JokeClientAdmin class to get details about mode
         and if toggling servers... this means creating a separate worker class just
         for this connection too
        this section also spins a new thread to handle joke client administration communication
        connects to adminClient either on default port 5050
        */
        AdminConnection clientAdminConnection = new AdminConnection();
        Thread thread = new Thread(clientAdminConnection);
        thread.start();

        /*Creating a server socket that will wait for requests to come in through the network.
         It has a determined port and maximum queue length.
        If the queue is full, then connection to the server socket will be refused.
        */
        ServerSocket serverSocket = new ServerSocket(4545, queue_length);
        //the implementation will only worry about the default port for now.
        System.out.println("The Joke Administration Client will be using port 5050.\n");

        while (true) { //meaning forever
            //checks if socket is closed and if not closed, will create a new socket
            //implementation is similar to 'this.socket = new Socket()'
            //waits for communication from the client server
            socket = serverSocket.accept();
            new Worker(socket).start(); //calls Worker class to create socket
        }
    }
}

/*
this class is to start up a connection to the Joke Client Admin on port 5050.
This class makes the call based on port is 4545
*/
class AdminConnection implements Runnable {
    public static boolean adminControlSwitch = true;

    public void run() {
        System.out.println("Now spinning the joke client administrator thread" +
                " to connect to the Joke Client Admin...");
        int queue_length = 6; //number of requests maximum at a time
        //default client admin port is 5050 because matching with client port 4545
        int clientAdminPortNumber = 5050; //default is port #5050
        Socket socket;

        try {

            /*-----STATUS-------
            This is to organize the port and its affiliated client port
            communicating with port 5050 if the client port is 4545
            if (port == 4545) clientAdminPortNumber = 5050;
                //communicating with port 5051 if the client port is 4546
            else if (port == 4546) clientAdminPortNumber = 5051;
            */
            //connect to the appropriate port with 6 maximum number simultaneous requests
            ServerSocket serverSocket = new ServerSocket(clientAdminPortNumber, queue_length);

            //code is from CS435
            while (adminControlSwitch) { //since it is true, meaning forever
                //waiting to connecting with admin client
                socket = serverSocket.accept();
                //create a worker for the client admin..either on port 5050.
                // This will get the joke or proverb.
                new JokeClientAdminWorker(socket).start();
            }
        } catch (IOException e) {
            //for debugging
            System.out.println("Error connecting to Joke Client Admin with port: " + clientAdminPortNumber);
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

/*
This method creates a worker thread for the joke client admin used by above
this will look at the input and see set Joke mode or Proverb Mode
 The main purpose of this class is to the the application mode given to us by the Client Admin
the framework is very similar to the Worker class for Joke Server main method
 */
class JokeClientAdminWorker extends Thread {

    Socket socket;

    JokeClientAdminWorker(Socket socket) {
        this.socket = socket;  //constructor for Socket
    }

    public void run() {
        PrintStream out = null;
        BufferedReader in = null;

        try {
            //code is from CS 435
            //reads incoming text from the socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream()); //output to the socket and sends information

            try {
                //reads the mode from the admin client
                // that the user wanted to run the application on
                String userInput = in.readLine();

                if (userInput.equals("")) { //if the client presses <enter>

                    //since the default is on "Joke", pressing enter will change to "Proverb" mode
                    if ((JokeServer.appMode).equals("Joke")) {
                        JokeServer.appMode = "Proverb";
                        //The server will print out this message verifying mode
                        out.println("\nTurning on Proverb Mode..."); //sending proverb mode
                        System.out.println("\nTurning on Proverb Mode..."); //displays mode on screen

                    } else {
                        //since the mode is on "Proverb" instead, pressing enter will change to "Joke" mode
                        JokeServer.appMode = "Joke"; //enable joke mode
                        //The server will print out this message verifying mode
                        out.println("\nTurning on Joke Mode..."); //sending joke mode
                        System.out.println("\nTurning on Joke Mode..."); //server will print this
                    }
                }
            } catch (IOException e) {
                //for debugging
                System.out.println("Unable to read what the user's mode selection on the Admin Client.");
                System.out.println(e.getCause());
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException e) {
            //for debugging purposes
            System.out.println("Error reading mode from the Joke Client Administrator.");
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
        }
    }
}

/*
Most of the code is from CS 435 class
This class created a Worker for the Joke Server.
Defining Worker class and features.
 */
class Worker extends Thread {
    List<String> jl = new ArrayList<String>();
    Socket socket; //creating a Socket object that will have a port number

    Worker(Socket s) {
        socket = s;
    } // Constructor

    public void run() {

        // Get I/O from the socket:
        //code framework is from CS 435
        BufferedReader in = null; //reads incoming text from input
        PrintStream outToServer = null; //output to server

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  //gets socket's input
            outToServer = new PrintStream(socket.getOutputStream()); //gets output

            try {

                //The following 3 variables have to be in the exact order as what the Joke Client sends: userName, userId, and userInput
                String userName = in.readLine(); //reading the first input from client: userName of the client
                System.out.println("UserName is " + userName);
                String userId = in.readLine(); //reading the second input from client: userId...supposed to be for cookie
                String userInput = in.readLine(); //reading the third input from client: userInput of Joke or Proverb

                //When the user presses <enter> from the Joke Client interface, we just need to display the jokes and/or proverbs
                //The Joke Client Admin should dictate the application mode.
                if (userInput.equals("")) {
                    if (JokeServer.appMode.equals("Joke")) {
                        //for debugging
                        System.out.printf("Searching %s for %s...", JokeServer.appMode, userName);
                        //this method call handles the response to send to JokeClient with jokes
                        sendsJoke(userName, outToServer);
                    } else {
                        System.out.printf("Searching Proverb for %s...", userName);
                        //this method call handles the response to send to JokeClient with proverbs
                        sendsProverb(userName, outToServer);
                    }
                }

                /*-----------STATUS------------
                these statement conditions resets the jokes and/or proverbs if we are implementing switches into our implementation
                if (!JokeServer.userInput.equalsIgnoreCase("switch")) { //just making sure that we are not including switch
                    if (JokeServer.isJokeMode.equalsIgnoreCase("Joke") && ("1111").equals(JokeServer.jokeAlreadySeen)) {
                        //letting the client know that we've reached the end of joke collection
                        //outToServer.println("JOKE CYCLE COMPLETED!");
                        JokeServer.jokeAlreadySeen = "0000"; //resets all jokes to have not seen any of them.
                        outToServer.println(JokeServer.sendsJoke(JokeServer.userName)); //sends jokes

                    } else if (JokeServer.isJokeMode.equalsIgnoreCase("Proverb") && ("1111").equals(JokeServer.proverbAlreadySeen)) {
                        //letting the client know that we've reached the end of proverb collection
                        //outToServer.println("PROVERB CYCLE COMPLETED!");
                        JokeServer.proverbAlreadySeen = "0000";
                        outToServer.println(JokeServer.sendsJoke(JokeServer.userName)); //sends jokes

                    } else if (("Joke").equalsIgnoreCase(JokeServer.userInput)
                            || (JokeServer.userInput.equals("") && JokeServer.isJokeMode.equalsIgnoreCase("Joke"))) {
                        //outToServer.printf("Searching jokes for %s...", JokeServer.userName);
                        outToServer.println(JokeServer.sendsJoke(JokeServer.userName)); //sends a user name to the client
                    }
                    else if (("Proverb").equalsIgnoreCase(JokeServer.userInput)
                            || (JokeServer.userInput.equals("") && JokeServer.isJokeMode.equalsIgnoreCase("Proverb"))) {
                        System.out.printf("Searching proverbs for %s", JokeServer.userName);
                        outToServer.println(JokeServer.sendsProverb(JokeServer.userName)); //sends a joke to the client
                    }
                    else if (("Quit").equalsIgnoreCase(JokeServer.userInput)) {
                        System.out.println("Joke Server detected Quit. Leaving the program now....");
                       System.exit(0);
                    }
                }*/

            } catch (IOException x) {
                //for debugging purposes
                System.out.println("Error with Worker");
                System.out.println(x.getCause());
                System.out.println(x.getMessage());
                x.printStackTrace();
            }
            socket.close(); //shutting down socket's connection but not deleting the socket itself

        } catch (IOException ioe) {
            System.out.println("Error with Worker and getting jokes/proverbs.");
            System.out.println(ioe);
        }
    }

    static void sendsJoke(String userName, PrintStream out) {

        List<String> jList = new ArrayList<String>(); //creating a list because it is easier to use shuffling

        //I've gotten inspirations for these jokes online or from my own experiences hearing these jokes.
        jList.add("Joke A: " + userName + " = I would be rich if I was given a dollar for every screechy noise I made on the piccolo.");
        jList.add("Joke B: " + userName + " = How many flute players does it take to fix a broken light bulb? None. They are all too busy fighting for the chair.");
        jList.add("Joke C: " + userName + " = What does a Thai boyfriend/husband say to his significant other while giving an air cooler? I am your biggest FAN!");
        jList.add("Joke D: " + userName + " = If my corgi can talk, she would say that I have OCD: Obsessive Corgi Disorder!");

        Collections.shuffle(jList); //easily randomizes elements in joke list

        int jokeNumber = 0;

        //We need to transform our joke list into an array to individually send out to the Joke Client
        String[] jArray = new String[4];
        jArray = jList.toArray(jArray);

        for (int i = 0; i < jArray.length; i++) {
            jokeNumber++;
            out.println(jArray[i]); //sending joke response to Joke Client
        }

        //This is to let the user know that we have reached the end of the Joke Cycle
        if (jokeNumber == 4) {
            System.out.println("JOKE CYCLE COMPLETED!");
            out.println("JOKE CYCLE COMPLETED!");
        }

        Collections.shuffle(jList); //easily randomizes elements in joke list for "fun"
    }

    /*
    This method sets the proverbs and returns the responses to Joke Client
     */
    static void sendsProverb(String userName, PrintStream out) {

        //We create and set the fields for our proverb list
        List<String> pList = new ArrayList<String>();
        pList.add("Proverb A: " + userName + " = A vacation always clear one's mind.");
        pList.add("Proverb B: " + userName + " = Patience is a virtue.");
        pList.add("Proverb C: " + userName + " = No good deeds go unpunished.");
        pList.add("Proverb D: " + userName + " = Work hard. Play hard.");

        char[] proverbDisplayedArray = JokeServer.proverbAlreadySeen.toCharArray(); //splits "0000" into an array and supposed to be for cookie

        Collections.shuffle(pList); //randomizing elements in our proverb list

        //like for setting jokes, we have to transform our list into an array to avoid Out of Bounds error
        //helps with sending each jokes to the Joke Client
        String[] pArray = new String[4];
        pArray = pList.toArray(pArray);

        //keeping track of the total number of proverbs we have sent over to Joke Client
        int proverbNumber = 0;

        //Looping through the array we've transformed and sending each proverb to the user
        for (int i = 0; i < pArray.length; i++) {
            proverbNumber++;
            out.println(pArray[i]); //sending to Joke Client
        }
        //Letting users know that we have reached the end of our proverbs
        if (proverbNumber == 4) {
            System.out.println("PROVERB CYCLE COMPLETED!");
            out.println("PROVERB CYCLE COMPLETED!");
        }

        Collections.shuffle(pList); //randomizing elements in our proverb list...to make it more "fun"
    }
}
