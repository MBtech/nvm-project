package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.rmi.RemoteException;

import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import common.ClientInterface;
import common.ServerInterface;

public class Client extends UnicastRemoteObject implements ClientInterface{

	private ServerInterface server;
	private int id;
	int account;
	InputParameters snapshotParams;
	private Snapshot snapshot;
	static int ID = 0;
	//	private int ID;
	//	private static int count;

	protected Client(ServerInterface server, InputParameters inputParams) throws RemoteException {

		// if there is a current snapshot that matches the retrievel node, then use it
		// Otherwise, start a new instance
		this.server = server;
		this.id = Integer.parseInt(inputParams.retrieval_node);
		this.snapshotParams = inputParams;

		if(inputParams.state == "0") {
			
			
			this.server.registerClient(this);
			this.account = 0;
			this.snapshot = new Snapshot(this);
		}
		else {
			ClientInitModule();
		}


	}

	public void ClientInitModule() throws RemoteException {
		// read remote snapshot file on the NVM node
		JSch jsch = new JSch();
		Session session = null;
		FileReader reader =null;
		BufferedReader buffer = null;

		String srvrSSH = "kw60174.cbrc.kaust.edu.sa";
		String userSSH = "yourun"; 
		String pswdSSH = "yourpass";
		String remoteDir = "/home/alkhalaa/testSCP";
		String prevLine = null;

		// TODO: optimize & refactor
		try 
		{

			session = jsch.getSession(userSSH, srvrSSH);
			java.util.Properties config = new java.util.Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);  
			session.setPassword(pswdSSH);                
			session.connect();              
			Channel channel = (Channel) session.openChannel("sftp");
			((com.jcraft.jsch.Channel) channel).connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			System.out.println("Is connected to IP:"+((com.jcraft.jsch.Channel) channel).isConnected());
			Vector ls=sftpChannel.ls(remoteDir);
//			for(int i=0;i<ls.size();i++){
//				System.out.println("Ls:"+ls.get(i));
//			}
			sftpChannel.cd(remoteDir);             
			File file = new File("snapshot00");
			sftpChannel.get("snapshot00");              
			reader = new FileReader(file);
			buffer = new BufferedReader(reader);                
			String getLine = "";
			prevLine = getLine;
			while ((getLine = buffer.readLine())!=null)
			{
				prevLine = getLine;
//				System.out.println("Line: "+getLine);
			}
			System.out.println(prevLine);
			
			sftpChannel.exit();             
			session.disconnect();
		}
		catch (JSchException e) {
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		} 
		finally{
			try{
				if(reader!=null)
					reader.close();
				if(buffer!=null)
					buffer.close();
			}
			catch(Exception e){
				System.out.println("Exception:"+e);
			}
		}
		
		initClient(prevLine.split(","));
	}

	void initClient(String[] params) throws RemoteException {
		String snapshotCount = params[0];
		String ClientID = params[1];
		String account = params[2];
		
		this.account = Integer.parseInt(account.trim());
		this.snapshot = new Snapshot(this);
		
		
		
		
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

	public int getName() throws RemoteException {
		return this.id;
	}

	public String getSnapshotFilename() {
		return this.snapshotParams.snapshot_filename;

	}

	public Snapshot getSnapshot() {
		return this.snapshot;

	}

}
