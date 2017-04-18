package common;
import java.rmi.Remote;
import java.rmi.RemoteException;



public interface ServerInterface extends Remote{
	
	void registerClient(ClientInterface client) throws RemoteException;
	void broadcastMessage(String message) throws RemoteException;
	void sendAmount(int fromClientID, int toClientID, int amount) throws RemoteException;
	void printAccount(int clientID) throws RemoteException;

}
