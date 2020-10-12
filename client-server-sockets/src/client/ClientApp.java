package client;

/**
* 
* @author Peter Lavin xxxxxxxx, CA587 PT, GDF1 DCU
* @version April 19th 2004
*
* This is the client part of a client-server program to implement the
* file transfer protocal (FTP). The program functions are as follows...
* Listing files on the server,                                                  
* Listing local files (client side),
* Saving a file from the client to the server side,
* Retrieving a file from the server to the client side,
* Deleting a file stored on the server,
* Deleting a file stored on the client.
*
* This program calls methods in the ClientUtils class for the 
* following functions... generating the user display menu, getting  
* the user input for each fucntion, creating and making entries 
* to the  ClientLog.log file and checking that use inputs involving  
* filenames are valid, or that the entered filename  exists.
*
* Algorithm is as follows...
* The user menu is displayed, 1-6. The user input is used to select a 
* switch-case in this the main program here. Each case program carried
* out the required action, creating sockets, In/OutputStream(s) as 
* necessary. More details of each case are included in comments at  
* each point in the code.
*
* KNOWN BUGS/FAULTS.
* User input requires an intgeer input, typing a character will
* cause and IO exception and be treated by the program as 0, two 
* successive character entries will cause the program to exit.
* 
* Sending the acknowlegement for the STOR command is not implemented
* as per the project spec.
* 
* imports java.io and java.net 
*
*/
import java.io.*; // contains the stream classes
import java.net.*; //  contains the socket classes
import java.util.Scanner;


class ClientApp { // begin class

	///////////////////////////////////////////////////////////////////
	// program begins here

	/**
	 * 
	 * 
	 * Gets a user input and implemtes the request, LIST, local list, STOR (send a
	 * file to server), RETR (retrieve from server) or DELE (delete a file on
	 * srever).
	 * 
	 * @throws java.net exception
	 * 
	 */
	public static void main(String[] args) { // begin main

		// creates a ClientLog.log if necessary.
		ClientUtils.logFileCreator();

		// blank log entry for clarity in Clientlog file
		ClientUtils.logEntry();
		ClientUtils.logEntry("Client program started.");

		int menuChoice = 0;

		Scanner scanner = new Scanner(System.in);

		// start of do loop, using sentinel at end of program to keep client app
		// running as long as menuChoice !=0
		do {
			// generating menu
			String menuString = ClientUtils.buildMenu();
			System.out.println(menuString);

			// seeking choice form the user
			menuChoice = ClientUtils.getMenuChoice();

			///////////////////////////////////////////////////////////////////
			// each switch-case below corresponds to a possible user input

			switch (menuChoice)

			{ // begin case
			case 1: // begin case for listing server files

				// declaring the string to be sent in this case
				String listServerMsg = "LIST";

				try {
					// create a socket as outputStream to be associated with it
					// 127.0.0.1 is used to loop back, this would typically
					// be the network address of another computer
					Socket clientSocket = new Socket("127.0.0.1", 7777);

					// create an DataInput/OutputStreams,
					// to be associated with this socket
					DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
					DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

					// writing the LIST string to the server
					dos.writeUTF(listServerMsg);
					// log entry
					ClientUtils.logEntry("LIST instruction sent to server.");

					// program now waiting for number of files from server
					int noOfFiles = dis.readInt();
					System.out.println("\n\nThere are " + noOfFiles + " files on the SERVER.");

					// program now waiting for list of files string from server
					String fileListString = dis.readUTF();
					System.out.println(fileListString);

					// log entry
					ClientUtils.logEntry("List of files received from server");

					// closing streams and clientSocket
					dis.close();
					dos.close();
					clientSocket.close();
					ClientUtils.logEntry("Client socket closed for LIST cmd.");
				}

				catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("\nException while running LIST cmd, " + "see ClientLog.log.");

					// log entry
					ClientUtils.logEntry("CApp exception " + ex + ".");
				}
				break; // end LIST from server case

			///////////////////////////////////////////////////////////////////

			case 2:
				// begin case for listing local files

				// declares a new file object, c: is the current folder
				File dir = new File("/home/peter/temp/client");

				// generates a tring array, each element being a file name
				String[] children = dir.list();

				int noOfFiles = children.length;

				if (children == null) {
					// Either dir does not exist or is not a directory
					System.out.println("\nProblem listing local files.");

					// log entry
					ClientUtils.logEntry("Problem listing local files on client.");
				} else {
					System.out.println("\nThere are " + noOfFiles + "  LOCAL files.\n");

					// cals a method making a long string of the
					// array elements for display
					String allFiles = ClientUtils.arrayToString(children);
					System.out.println(allFiles);

				}

				break; // end LIST from local client case

			///////////////////////////////////////////////////////////////////

			case 3:
				// begin case for sending (STOR) a file to server

				// declaring the string to be sent in this case
				String storServerMsg = "STOR";

				// requesting name of file to be sent
				System.out.println("Enter local name of file to be " + "saved to the server...");
				String fileToSend = scanner.nextLine();

				// checks that file to be sent exists
				boolean storFileExists = ClientUtils.fileExistsCheck(fileToSend);

				if (!storFileExists) {
					System.out.println("File may not exist, check for exact " + "name/spelling.");
				}
				if (storFileExists) {
					try {
						// create a socket
						Socket clientSocket = new Socket("127.0.0.1", 7777);

						// create an DataInput/OutputStreams,
						// to be associated with this socket
						OutputStream outStream = clientSocket.getOutputStream();
						DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
						DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

						// writing the STOR string to the server
						dos.writeUTF(storServerMsg);

						// log entry
						ClientUtils.logEntry("STOR instruction sent to server.");

						// requesting name of file from user
						System.out.println("Enter name to be given to " + "file when saved " + "on the server...");
						String fileNameforServer = scanner.nextLine();

						// sending name to be given to the file on the server
						dos.writeUTF(fileNameforServer);

						// log entry
						ClientUtils.logEntry(
								"Filename for server for STOR " + "sent to server (" + fileNameforServer + ").");

						// server is now waiting for the file stream
						FileInputStream fis = new FileInputStream(fileToSend);

						// sending the file stream to the server
						int value = fis.read();
						while (value != -1) {
							outStream.write(value);
							value = fis.read();
						}
						outStream.flush();

						System.out.println(fileToSend + " sent to server.");

						// log entry
						ClientUtils.logEntry("file " + fileToSend + " sent to server.");

						// closing streams and clientSocket
						dis.close();
						dos.close();
						outStream.close();
						fis.close();
						clientSocket.close();

						// log entry
						ClientUtils.logEntry("Client socket closed for STOR cmd.");

					} // end try

					catch (Exception ex) {
						ex.printStackTrace();
						System.out.println("\nException while running " + "STOR cmd, see ClientLog.log.");

						// log entry
						ClientUtils.logEntry("CApp exception " + ex + ".");
					}
				} // end if storFileExixts
				break; // end case for sending (STOR) a file to server

			////////////////////////////////////////////////////////////////

			case 4:
				// begin case for retreiving (RETR) a file from the server

				// declaring the string to be sent in this case
				String retrServerMsg = "RETR";

				try {
					// create a socket
					Socket clientSocket = new Socket("127.0.0.1", 7777);

					// create an DataInput/OutputStreams,
					// to be associated with this socket
					InputStream inStream = clientSocket.getInputStream();
					DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
					DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

					// writing the STOR string to the server
					dos.writeUTF(retrServerMsg);

					// log entry
					ClientUtils.logEntry("RETR instruction sent to server.");

					// server is expecting the name of file to retrieve
					System.out.println("Enter the name of the file " + "you wish to retreive...");
					String fileToRetreiveName = scanner.nextLine();

					// sending file name to retrieve to server
					dos.writeUTF(fileToRetreiveName);

					// log entry
					ClientUtils.logEntry(fileToRetreiveName + " filename " + "for RETR cmd sent to server.");

					// receive boolean here, base next 'if' on this variable
					boolean retrFileExists = dis.readBoolean();

					// log entry
					ClientUtils.logEntry("boolean file exists " + retrFileExists + " received from server.");

					if (!retrFileExists) {
						System.out.println("File may not exist on server, " + "please check file name.");
					}

					if (retrFileExists) {
						// client is now expecting a file stream back
						System.out.println("Enter name for file when " + "saved on the client...");
						String fileToClientName = scanner.nextLine();

						// creating a new FileOutputStream
						FileOutputStream fos = new FileOutputStream(fileToClientName);

						// server is expecting a file in a stream, received here
						int value = inStream.read();
						while (value != -1) {
							fos.write(value);
							value = inStream.read();
						}
						// flushing fos to ensure all data goes to the file
						fos.flush();

						// log entry
						ClientUtils.logEntry("file " + fileToClientName + " received from server.");

						System.out.println("file " + fileToClientName + " received from server.");

						// closing streams and clientSocket
						dis.close();
						dos.close();
						fos.close();
						inStream.close();
						clientSocket.close();

						// log entry
						ClientUtils.logEntry("Client socket closed for RETR cmd.");

					} // end if for retrFileExists
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("\nException while running RETR cmd, " + "see ClientLog.log.");

					// log entry
					ClientUtils.logEntry("CApp exception " + ex + ".");
				}
				break; // end case for RETR cmd

			//////////////////////////////////////////////////////////////

			case 5: // begin case for DELE, deleting a file on the server

				// declaring the string to be sent in this case
				String deleServerMsg = "DELE";

				try { // begin try
						// create a socket
					Socket clientSocket = new Socket("127.0.0.1", 1966);

					// create an DataInput/OutputStreams,
					// to be associated with this socket
					DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
					DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

					// writing the STOR string to the server
					dos.writeUTF(deleServerMsg);

					// log entry
					ClientUtils.logEntry("DELE instruction sent to server.");

					// server is expecting the name of file to retreive
					System.out.println("Enter the name of the file " + "you wish to DELETE...");
					String fileToDeleteName = scanner.nextLine();

					// sending the file name to the server
					dos.writeUTF(fileToDeleteName);

					// log entry
					ClientUtils.logEntry(fileToDeleteName + " filename for DELE instruction " + "sent to server.");

					// reeiving a delete acknowlegement form server
					String ack = dis.readUTF();
					System.out.println(ack);

					// log entry
					ClientUtils.logEntry("Delete ack received.");

					// closing streams and clientSocket
					dis.close();
					dos.close();
					clientSocket.close();

					// log entry
					ClientUtils.logEntry("Client socket closed for DELE cmd.");

				} // end try
				catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("\nException while " + "running DELE cmd, see ClientLog.log.");

					// log entry
					ClientUtils.logEntry("CApp exception " + ex + ".");
				}
				break;

			//////////////////////////////////////////////////////////////////////

			case 6: // begin case for deleting a file on the local client

				// get name of file to be deleted
				System.out.println("Enter local file name to delete...");
				String localFileName = scanner.nextLine();

				// checking file exits, next 'if' based on this variable
				boolean localFileToDelete = ClientUtils.fileExistsCheck(localFileName);

				if (!localFileToDelete) {
					// displaying error msg to user
					System.out.println("\nLocal File may not exist, " + "check for exact " + "name/spelling.");
				}

				if (localFileToDelete) {
					// deleting file on client
					// deleting file
					File myFileObj = new File(localFileName);
					myFileObj.delete();

					// display for user
					System.out.println(localFileName + " was deleted.");

					// log entry
					ClientUtils.logEntry("File " + localFileName + " deleted on client.");
				}

				break; // end case for deleting a file on the server

			/////////////////////////////////////////////////////////////////////

			case 0: // begin case for closing CApp, client program

				// log entry with blank line
				ClientUtils.logEntry("Client program closed.");

				break; // end case closing CApp, client program

			} // end case for menuChoice

		} // sentinal to keep program running until 0 is chosen at menu
		while (menuChoice != 0);

	}// end main method
} // end class