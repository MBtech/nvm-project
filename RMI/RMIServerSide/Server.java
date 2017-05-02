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
	
}
