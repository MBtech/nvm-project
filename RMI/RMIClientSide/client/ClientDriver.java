package client;

import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import common.ClientInterface;
import common.ServerInterface;
import common.FileWatcher;

public class ClientDriver {
	
	public static void readInput(ServerInterface server) {
		System.out.println("Reading Input...");
		Scanner scanner = new Scanner(System.in);
		String line = null;
		String delims = "[ ]+";
		String[] tokens;
		
		
		while(!scanner.nextLine().equals("s")){
			System.out.println("You must Enter 's' to interact with other clients");
		}
		
		int max = 13;
		int min = 0;
	    int randomNum ;	
		
		while(true) {
			
			randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
			// read a random line from a transfer operations file and execute the line
			try {
				line = Files.readAllLines(Paths.get("transferOps.txt")).get(randomNum);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			tokens = line.split(delims);

			try {
				
				//i.e. b hello everyone !
				if (tokens[0].equals("b")) {
					server.broadcastMessage(line.substring(2));
				}
				//i.e. t 0 1 1890 
				else if (tokens[0].equals("t")) {
					server.sendAmount(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
				}
				
				//i.e. p 0
				else if (tokens[0].equals("p")) {
					server.printAccount(Integer.parseInt(tokens[1]));
				}
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main (String[] args) throws MalformedURLException, RemoteException, NotBoundException {
		
		System.out.println("Starting a new client...");
		String serverURL = "rmi://localhost/RMIServer";
		ServerInterface server = (ServerInterface) Naming.lookup(serverURL);		
		
		InputParameters input = new InputParameters(args[0], args[1], args[2], args[3]);
		Client newClient = new Client(server, input);
		new FileWatcher(new File("/Users/A_Y_M_A_N/Documents/workspace/RMI/RMIClientSide/bin/"+  newClient.snapshotParams.event_filename), newClient).start();
		readInput(server);
		
	}

}