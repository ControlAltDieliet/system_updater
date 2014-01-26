package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class packetInstaller {

	/**
	 * Downloads a file from a remote webserver and installs it 
	 * Works for programs who support silent installing.
	 */
	
	private String action=new String();
	private String download =new String();
	private String cmd=new String();
	private String filename=new String();
	private String feedbackto=new String();
	private String emailto=new String();
	private String registry=new String();
	private static String downloadPath="c:\\System update\\Downloads\\";
	private boolean result=false;



	public  packetInstaller(String action_,String download_, String 
			cmd_,String filename_,String registry_){
		/*
		 * Downloads the file and runs the command
		 * @param action_ has to be INSTALL, can be differed in other versions
		 * @param download_ url where I can download the program
		 * @param filename_ The name i have to give to the download
		 * @param cmd_ the command i have to run after downloading
		 * @param registry_ which registry I have to check first/write afterwards (not implemented yet)
		 * 
		 */
		
		this.action=action_;
		this.download=download_;
		this.cmd=cmd_;
		this.filename=filename_;
		this.registry=registry_;

		if (action.equals("INSTALL")){
			System.out.println("check filename"+this.filename);
			result=download(download,filename);
			if (result){
				System.out.println("running cmd: "+downloadPath+cmd);
				runCommand(cmd);
				System.out.println("runned the cmd: "+ cmd);
			}
		}
	}


	private boolean download(String url, String fileName){
		/*
		 * Downloads a file ands saves it
		 * @param url that I download
		 * @param fileName the name I use for saving the file
		 */
		URL website;
		File theDir = new File(downloadPath);

		if (!theDir.exists()) {
			System.out.println("creating directory: " +downloadPath );
			boolean result = theDir.mkdirs();
			theDir.setWritable(true);
			theDir.setReadable(true);
		}

		try {
			website = new URL(url);
			ReadableByteChannel rbc;
			rbc = Channels.newChannel(website.openStream());

			FileOutputStream fos = new FileOutputStream(downloadPath+fileName);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}


	private boolean runCommand(String command_){
		/*
		 * run the command
		 */
		String s = null;

		try {
			Process p = Runtime.getRuntime().exec(downloadPath+command_);

			BufferedReader stdInput = new BufferedReader(new
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new
					InputStreamReader(p.getErrorStream()));

			// read the output from the command
			System.out.println("Here is the standard output of thecommand:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			return true;
		}
		catch (IOException e) {
			System.out.println("exception happened - here's what I know: "+e);
			return false;
		}

	}




}

