import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import common.ClientInterface;
import common.ServerInterface;

public class ServerDriver {
	
	public static void main(String args[]) throws RemoteException, MalformedURLException {
		Server theServer = new Server();
		Naming.rebind("RMIServer", theServer);
		System.out.println("Server is Ready...");
//		new Thread(theServer).start();
		
	}

}
