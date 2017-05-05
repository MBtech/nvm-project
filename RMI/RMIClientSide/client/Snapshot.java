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
	
	private static String pass = "yourpass";
	static int count = 0;
	int snapshotID; // the same as the client's ID
	int snapshotCount; // a monotonically increasing number starting from zerp
	Client client;
	String filename;
	String noise; 	// holding extra noise to increase the size of the snapshot
	
	
	public Snapshot(Client client) throws RemoteException
	{
		this.client = client;
		this.snapshotID = this.client.getName();
		this.noise = "This is just some noise";
		this.filename = this.client.getSnapshotFilename();
		this.snapshotCount = 0;
	}
	

	public void takeSnapShot() throws JSchException, IOException, SftpException {
		// TODO Auto-generated method stub
		
		BufferedWriter out = null;
		try  
		{
		    FileWriter fstream = new FileWriter(filename, true); 
		    out = new BufferedWriter(fstream);
		    out.write(this.snapshotCount++ + ", " + client.getName() + ", " + client.getAmount() + "\n");
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
		
		//TODO: optimize
		org.apache.tools.ant.taskdefs.optional.ssh.Scp scp = new Scp();
		int portSSH = 22;
		String srvrSSH = "kw60174.cbrc.kaust.edu.sa";
		String userSSH = "yourun"; 
		String pswdSSH = pass;
		String localFile = "/Users/A_Y_M_A_N/Documents/workspace/RMI/RMIClientSide/bin/" + this.filename;
		String remoteDir = "/home/alkhalaa/testSCP";

		System.out.println("SCPing " + localFile + " to Remote NVM node...");
		// Meeting notes:
		// command line: DONT // fsync to flush the filesystem buffers
		// When SCPing stuff, open a pipe and keep it open
		scp.setPort( portSSH );
		scp.setLocalFile( localFile );
		scp.setTodir( userSSH + ":" + pswdSSH + "@" + srvrSSH + ":" + remoteDir );
		scp.setProject( new Project() );
		scp.setTrust( true );
		scp.execute();

	}
	

}
