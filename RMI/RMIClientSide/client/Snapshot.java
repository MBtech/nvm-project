package client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Snapshot {
	
	private static String pass = "Ayman1234a";
	static int count = 0;
	int snapshotID;
	Client client;
	String filename;
	String noise; 	// holding extra noise to increase the size of the snapshot
	
	
	public Snapshot(Client client)
	{
		this.snapshotID = count++;
		this.client = client;
		this.noise = "This is just some noise";
		try {
			this.filename = "snapshots" + client.getName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void takeSnapShot() throws JSchException, IOException, SftpException {
		// TODO Auto-generated method stub
		
		BufferedWriter out = null;
		try  
		{
		    FileWriter fstream = new FileWriter(filename, true); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    out.write(snapshotID + ", " + client.getName() + ", " + client.getAmount() + "\n");
		}
		catch (IOException e)
		{
		    System.err.println("Error: " + e.getMessage());
		}
		finally
		{
		    if(out != null) {
		        try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		
		RemoteWriteToNVM();
	}
	
	public void RemoteWriteToNVM() throws JSchException, IOException, SftpException {
		
		org.apache.tools.ant.taskdefs.optional.ssh.Scp scp = new Scp();
		int portSSH = 22;
		String srvrSSH = "kw60174.cbrc.kaust.edu.sa";
		String userSSH = "alkhalaa"; 
		String pswdSSH = pass;//new String ( jPasswordField1.getPassword() );
		String localFile = "/Users/A_Y_M_A_N/Documents/workspace/RMI/RMIClientSide/bin/snapshots" + client.getName();
		String remoteDir = "/home/alkhalaa/testSCP";

		System.out.println("SCPing " + localFile + "to Remote NVM node...");
		
		scp.setPort( portSSH );
		scp.setLocalFile( localFile );
		scp.setTodir( userSSH + ":" + pswdSSH + "@" + srvrSSH + ":" + remoteDir );
		scp.setProject( new Project() );
		scp.setTrust( true );
		scp.execute();
		
//		Copy copy = new Copy("kw60174.cbrc.kaust.edu.sa","alkhalaa", pass);
//		Path path = Paths.get("/Users/A_Y_M_A_N/Documents/workspace/RMI/RMIClientSide/bin/snapshotCommand.txt");
//		copy.cp(path, "/home/alkhalaa/testSCP");
		
		
			
	}
	

}
