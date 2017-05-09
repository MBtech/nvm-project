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
import com.jcraft.jsch.SftpException;

import common.ClientInterface;
import common.ServerInterface;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class Client extends UnicastRemoteObject implements ClientInterface{

	
	private static String pass = "yourps";
	private static final double epsilon = Double.MIN_VALUE;
	private double theta0 = 0.1;
	private double theta1 = 0.1;
	private int maxIterations = 1_000_000_000;
	private int remainingIterations = 1_000_000_000;
	private ServerInterface server;
	private int id;
	int account;
	InputParameters snapshotParams;
	private Snapshot snapshot;
	static int ID = 0;
	//	private int ID;
	//	private static int count;

	protected Client(ServerInterface server, InputParameters inputParams) throws FileNotFoundException, IOException {

		// if there is a current snapshot that matches the retrievel node, then use it
		// Otherwise, start a new instance
		this.server = server;
		this.id = Integer.parseInt(inputParams.retrieval_node);
		this.snapshotParams = inputParams;
		
		
		
		if(Integer.parseInt(inputParams.state) == 0) {

			this.server.registerClient(this);
			this.account = 0;
			this.snapshot = new Snapshot(this);
//			this.gradDescent(0.1,0.1,"gd_points", 1_000_000);
		}
		else {
			File snapshotFile = new File(inputParams.snapshot_filename);
						
			if(!snapshotFile.exists()) { 
				System.out.println("Snapshot file doesn't exist, starting a new instance...");
				this.server.registerClient(this);
				this.account = 0;
				this.snapshot = new Snapshot(this);
			}
			else {
				ClientInitModule();
			}
		}
	}
	
	public void run_gd() throws JSchException, IOException, SftpException {
		this.gradDescent(this.theta0, this.theta1, this.snapshotParams.gd_file, this.remainingIterations);
	}

	public void ClientInitModule() throws FileNotFoundException, IOException {
		// read remote snapshot file on the NVM node
//		JSch jsch = new JSch();
//		Session session = null;
//		FileReader reader =null;
//		BufferedReader buffer = null;
//
//		String srvrSSH = "kw60174.cbrc.kaust.edu.sa";
//		String userSSH = "alkhalaa"; 
//		String pswdSSH = pass;
//		String remoteDir = "/home/alkhalaa/testSCP";
		String prevLine = null;
//
//		// TODO: optimize & refactor
//		try 
//		{
//
//			session = jsch.getSession(userSSH, srvrSSH);
//			java.util.Properties config = new java.util.Properties(); 
//			config.put("StrictHostKeyChecking", "no");
//			session.setConfig(config);  
//			session.setPassword(pswdSSH);                
//			session.connect();              
//			Channel channel = (Channel) session.openChannel("sftp");
//			((com.jcraft.jsch.Channel) channel).connect();
//			ChannelSftp sftpChannel = (ChannelSftp) channel;
//			System.out.println("Is connected to IP:"+((com.jcraft.jsch.Channel) channel).isConnected());
//			Vector ls=sftpChannel.ls(remoteDir);
//			//			for(int i=0;i<ls.size();i++){
//			//				System.out.println("Ls:"+ls.get(i));
//			//			}
//			sftpChannel.cd(remoteDir);             
//			File file = new File("snapshot00");
//			sftpChannel.get("snapshot00");              
//			reader = new FileReader(file);
//			buffer = new BufferedReader(reader);                
//			String getLine = "";
//			prevLine = getLine;
//			while ((getLine = buffer.readLine())!=null)
//			{
//				prevLine = getLine;
//				//				System.out.println("Line: "+getLine);
//			}
//			System.out.println(prevLine);
//
//			sftpChannel.exit();             
//			session.disconnect();
//		}
//		catch (JSchException e) {
//			e.printStackTrace();
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		} 
//		finally{
//			try{
//				if(reader!=null)
//					reader.close();
//				if(buffer!=null)
//					buffer.close();
//			}
//			catch(Exception e){
//				System.out.println("Exception:"+e);
//			}
//		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(this.snapshotParams.snapshot_filename))) {
			System.out.println("Reading " + this.snapshotParams.snapshot_filename + " for recovery");
		    String line;
		    while ((line = br.readLine()) != null) {
		    	prevLine = line;
		       // process the line.
		    }
		}

		initClient(prevLine.split(","));
	}

	void initClient(String[] params) throws RemoteException {
		String snapshotCount = params[0];
		String ClientID = params[1];
		this.theta0 = Double.parseDouble(params[2].trim());
		this.theta1 = Double.parseDouble(params[3].trim());
		this.remainingIterations = (int) Double.parseDouble(params[4].trim());
		
//		this.gradDescent(this.theta0, this.theta1, "gd_points", this.remainingIterations);
//		this.account = Integer.parseInt(account.trim());
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
	// grad decsent 'getAmount' version
	public double[] get_thetas(){
        double[] thetas = {this.theta0, this.theta1, this.remainingIterations};
        return thetas;
    }

	public int getName() throws RemoteException {
		return this.id;
	}

	public void gradDescent(double middleTheta0, double middleTheta1, String fileName, int remainingIterations) throws JSchException, IOException, SftpException {

		List<Point2D> data = loadDataFromFile(fileName);
		double alpha = 0.000000001;
		Point2D finalTheta = singleVarGradientDescent(data, middleTheta0, middleTheta1, alpha, remainingIterations);
		System.out.printf("theta0 = %f, theta1 = %f", finalTheta.getX(), finalTheta.getY());
		this.snapshot.takeSnapShot();

	}

	private Point2D singleVarGradientDescent(List<Point2D> data, double initialTheta0, double initialTheta1, double alpha, int maxIterations) {
		this.theta0 = initialTheta0;
		this.theta1 = initialTheta1;
		this.maxIterations = maxIterations;
		this.remainingIterations = maxIterations;
		double oldTheta0 = 0, oldTheta1 = 0;

		for (int i = 0 ; i < this.maxIterations; i++) {
			if (hasConverged(oldTheta0, theta0) && hasConverged(oldTheta1, theta1))
				break;
			oldTheta0 = theta0;
			oldTheta1 = theta1;

			theta0 = theta0 - (alpha * gradientofThetaN(theta0, theta1, data, x -> 1.0));
			theta1 = theta1 - (alpha * gradientofThetaN(theta0, theta1, data, x -> x));
			this.remainingIterations -= 1;
		}
		return new Point2D.Double(theta0, theta1);
	}

	private boolean hasConverged(double old, double current) {
		return (current - old) < epsilon;
	}

	private double gradientofThetaN(double theta0, double theta1, List<Point2D> data, DoubleUnaryOperator factor) {
		double m = data.size();
		return (1.0 / m) * sigma(data, (x, y) ->  (hypothesis(theta0, theta1, x) - y) * factor.applyAsDouble(x));
	}

	private double hypothesis(double theta0, double theta1, double x) {
		return theta0 + (theta1 * x);
	}

	private double sigma(List<Point2D> data, DoubleBinaryOperator inner) {
		return data.stream()
				.mapToDouble(theta -> {
					double x = theta.getX(), y = theta.getY();
					return inner.applyAsDouble(x, y);
				})
				.sum();
	}

	private List<Point2D> loadDataFromFile(String fileName){
		List<Point2D> data = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			int a;
			int b;
			String line;
			while ((line = br.readLine()) != null) {
				String pair[] = line.split(" ");
				a = Integer.parseInt(pair[0]);
				b = Integer.parseInt(pair[1]);
				data.add(new Point2D.Double(a,b));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return data;
	}



	public String getSnapshotFilename() {
		return this.snapshotParams.snapshot_filename;

	}

	public Snapshot getSnapshot() {
		return this.snapshot;

	}

}
