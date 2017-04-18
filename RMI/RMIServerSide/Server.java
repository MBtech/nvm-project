import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

import common.ClientInterface;
import common.ServerInterface;

public class Server extends UnicastRemoteObject implements ServerInterface {

	private ArrayList<ClientInterface> clients;
	
	
	
	protected Server() throws RemoteException {
		this.clients = new ArrayList<ClientInterface>();
		
	}

	@Override
	public synchronized void registerClient(ClientInterface client) throws RemoteException {
		this.clients.add(client);
	}

	@Override
	public synchronized void broadcastMessage(String message) throws RemoteException {
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).retrieveMessage(message);
		}
		
	}

	public synchronized void sendAmount(int fromClientID, int toClientID, int amount) throws RemoteException {
		clients.get(toClientID).retrieveAmount(amount);
	}

	@Override
	public synchronized void printAccount(int clientID) throws RemoteException {
		System.out.println("Client " + clientID + " has " + clients.get(clientID).getAmount());
	}
	
//	public void run() {
//		Scanner scanner = new Scanner(System.in);
//		String message;
//		
//		while(true) {
//			message = scanner.nextLine();
//			String delims = "[ ]+";
//			String[] tokens = message.split(delims);
//
//			try {
//				
//				//i.e. b hello everyone !
//				if (tokens[0].equals("b")) {
//					broadcastMessage(tokens[1] + ": " + message.substring(2));
//				}
//				//i.e. t 1 1890 
//				else if (tokens[0].equals("t")) {
//					sendAmount(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
//				}
//				
//				//i.e. p 0
//				else if (tokens[0].equals("p")) {
//					printAccount(Integer.parseInt(tokens[1]));
//				}
//				
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}
//		
//	}
	
}
