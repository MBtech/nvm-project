import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import common.ClientInterface;
import common.ServerInterface;

public class ClientDriver {
	
	public static void readInput(ServerInterface server) {
		
		Scanner scanner = new Scanner(System.in);
		String message;
		
		while(true) {
			message = scanner.nextLine();
			String delims = "[ ]+";
			String[] tokens = message.split(delims);

			try {
				
				//i.e. b hello everyone !
				if (tokens[0].equals("b")) {
					server.broadcastMessage(message.substring(2));
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
		System.out.println("This is a new client");
		String serverURL = "rmi://localhost/RMIServer";
		ServerInterface server = (ServerInterface) Naming.lookup(serverURL);
		
		for (int i = 0; i < Integer.parseInt(args[0]); i++) {
			new Client(args[0], server);
		}
		
		readInput(server);
	
	}

}
