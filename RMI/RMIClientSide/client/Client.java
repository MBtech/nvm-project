package client;

import java.rmi.RemoteException;

import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import common.ClientInterface;
import common.ServerInterface;

public class Client extends UnicastRemoteObject implements ClientInterface{
	
	private ServerInterface server;
	private String name = null;
	int account;
	private Snapshot snapshot;
//	private int ID;
//	private static int count;
	
	protected Client(String name, ServerInterface server) throws RemoteException {
		this.name = name;
		this.server = server;
		this.server.registerClient(this);
		this.account = 0;
//		this.ID = count;
//		count++;
		
		
	}

	public void retrieveMessage(String message) throws RemoteException {
		System.out.println(message);
	}
	
	@Override
	public void retrieveAmount(int amount) throws RemoteException {
		account += amount;
	}
	
	@Override
	public int getAmount() throws RemoteException {
		return this.account; 
		
	}
	
	public String getName() throws RemoteException {
		return this.name;
	}
	
	public Snapshot getSnapshot() {
		Snapshot currentSnapShot = new Snapshot(this);
		return currentSnapShot;
		
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
//					server.broadcastMessage(name + ": " + message.substring(2));
//				}
//				//i.e. t 1 1890 
//				else if (tokens[0].equals("t")) {
//					server.sendAmount(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
//				}
//				
//				//i.e. p 0
//				else if (tokens[0].equals("p")) {
//					server.printAccount(Integer.parseInt(tokens[1]));
//				}
//				
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}
//		
//	}

}
