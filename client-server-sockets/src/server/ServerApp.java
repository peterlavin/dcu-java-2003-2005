package server;

import java.io.*; // contains the stream classes
import java.net.*; //  contains the socket classes

class ServerApp {
	/**
	 * 
	 * @author Peter Lavin xxxxxxxxx, CA587 PT, GDF1 DCU
	 * @version April 19th 2004
	 *
	 *          This is the server part of a client-server program to implement the
	 *          file transfer protocal (FTP). The program functions are as
	 *          follows... Responds to four possible instructions from the client.
	 *          LIST - Listing files on the server and passing this to the client
	 *          program. STOR, saving a file from the client to the server. RETR,
	 *          sent a requested file to the client. DELE, deleting a file stored on
	 *          the server.
	 * 
	 *          This program calls methods in the ServerUtils class for the
	 *          following functions... creating and making entries to the
	 *          ServerLog.log file, Checking that filenames sent from the client are
	 *          valid/file exixts.
	 *
	 *          Algorithm is as follows... Server starts and waits for contact from
	 *          the client. Initial client contact will be a string STOR, RETR, etc.
	 *          This string is examined and appropriate instructions carried out.
	 *          After each client instruction is carried out, the program loops
	 *          indefinetly waiting for another client instruction.
	 * 
	 *          KNOWN BUGS/FAULTS.
	 * 
	 *          Sending the acknowlegement for the STOR command is not implemented
	 *          as per the project spec.
	 * 
	 *          imports java.io and java.net
	 *
	 */

	/**
	 * 
	 * Main method receives a user instruction from the client and implements the
	 * request, LIST, RETR, etc.
	 * 
	 * @throws java.net exception
	 * 
	 */
	public static void main(String[] args) {

		// calls log creator in ServerUtils
		ServerUtils.logFileCreator();

		// blank log entry for clarity in Serverlog file
		ServerUtils.logEntry();
		ServerUtils.logEntry("Server started.");

		// display for user
		System.out.println("Java Project Server in running");
		System.out.println("Press Ctrl-C to close server");

		// declaring an empty string which will become the
		// instruction received from the client
		String instrFromClient = "";

		try { // begin 1st try

			// declares and opens server socket
			ServerSocket srvSocket = new ServerSocket(7777);

			// begin while to loop while server program is open,
			// end while is at end of the program
			while (true) {

				try { // begin 2nd try

					// program waits here until a request from the
					// client is received. When the request is received,
					// only then is the socket created.
					Socket svSock = srvSocket.accept();

					// log entry
					ServerUtils.logEntry("Socket accepted");

					// create an Input,DataInput/OutputStreams,
					// to be associated with this socket
					InputStream inStream = svSock.getInputStream();
					OutputStream outStream = svSock.getOutputStream();
					DataInputStream dis = new DataInputStream(inStream);
					DataOutputStream dos = new DataOutputStream(outStream);

					// receiving instruction form client, LIST, RETR, etc.
					instrFromClient = dis.readUTF();

					// log entry
					ServerUtils.logEntry(instrFromClient + " instruction received from client.");

					// conditional testing of instrFromClient received

					if (instrFromClient.equals("LIST")) {

						// creates an array containing all the files in the server
						// (i.e. files in this directory)
						// array creation here from javaalmanac.com
						File dir = new File("/home/peter/temp/server");
						String[] children = dir.list();

						// checks to see if the array is null
						if (children == null) {
							// sending zero as noOfFiles to accompany error message
							dos.writeInt(0);

							// sending error message to client if array is null
							dos.writeUTF("Problem creating list of server files.");

							// log entry
							ServerUtils.logEntry("Problem creating array " + "for server file list.");
						} else { // begin if

							// sending the length of the array (number of files)
							// to client for displaying only
							int noOfFiles = children.length;
							dos.writeInt(noOfFiles);

							// log entry
							ServerUtils
									.logEntry("Int number of files on server " + "sent to client (" + noOfFiles + ").");

							// making a long string of the array elements
							// calling a method in ServerUtils to make
							// a string of the file names
							String allFiles = ServerUtils.arrayToString(children);

							// sending the allFiles string to client for displaying
							dos.writeUTF(allFiles);

							// log entry
							ServerUtils.logEntry("List of files on server " + "sent to client.");

						} // end children 'is null' if
					} // end 'if' to test for LIST

					if (instrFromClient.equals("STOR")) {
						// receiving name to use when saving the file
						String fileOutputName = dis.readUTF();
						// log entry
						ServerUtils.logEntry(fileOutputName + " filename recieved from client " + "for STOR cmd.");

						FileOutputStream fos = new FileOutputStream(fileOutputName);

						int value = inStream.read();

						while (value != -1) {
							fos.write(value);
							value = inStream.read();
						}
						// flushing FileOutputStream to ensure all data goes to file
						fos.flush();

						// log entry
						ServerUtils.logEntry("File " + fileOutputName + "  saved on server.");

						// closing any streams created and used here
						fos.close();

					} // end 'if' to test for STOR

					if (instrFromClient.equals("RETR")) {
						// server is now expecting a filename form the server
						String fileToRetreiveName = dis.readUTF();

						// log entry
						ServerUtils.logEntry(fileToRetreiveName + " filename received from client " + "for RETR cmd.");

						// checking to see if the filename sent exists
						boolean retrFileExists = ServerUtils.fileExistsCheck(fileToRetreiveName);

						// writing this boolean value to client
						dos.writeBoolean(retrFileExists);

						// log entry
						ServerUtils.logEntry("Boolean " + retrFileExists + " file exists " + "for RETR cmd.");

						if (retrFileExists) {
							// client is now expecting a file from the server (here)
							FileInputStream fis = new FileInputStream(fileToRetreiveName);

							// sending filestream to client via a DataOutputStream
							int value = fis.read();
							while (value != -1) {
								outStream.write(value);
								value = fis.read();
							}
							// flushing OutputStream to ensure all data goes to client
							outStream.flush();

							// log entry
							ServerUtils.logEntry("File " + fileToRetreiveName + " sent to client " + "for RETR cmd.");

							// closing any streams created and used here
							fis.close();

						} // end fileExists if
					} // end if for RETR

					if (instrFromClient.equals("DELE")) {
						// program is now expecting a file name to use
						// in the delete method, received here.
						String fileToDelete = dis.readUTF();

						boolean deleFileExists = ServerUtils.fileExistsCheck(fileToDelete);

						if (deleFileExists) {
							// deleting file
							File myFileObj = new File(fileToDelete);
							myFileObj.delete();

							// log entry
							ServerUtils.logEntry("File " + fileToDelete + " deleted on server.");

							// sending ack to client
							dos.writeUTF("This ia a file " + fileToDelete + " delete ack from the server.");

							// log entry
							ServerUtils.logEntry("Delete ack sent to client for " + fileToDelete + ".");

						}
						if (!deleFileExists) {
							// log entry
							ServerUtils.logEntry("Unable to delete " + fileToDelete + " file on server.");

							// sending ack to client
							dos.writeUTF("File " + fileToDelete + " not deleted, check file name/spelling.");

							// log entry
							ServerUtils.logEntry("NOT DELETED ack sent for " + fileToDelete + ".");
						}

					} // end if for DELE

					// close socket (svSock), dis and dos each
					// time after any operation
					// FileInput/Outputs are closed within IF dependant
					// statements where used
					dis.close();
					dos.close();
					svSock.close();

				} // end 2nd try
				catch (Exception ex) // catch for 2nd try
				{
					ex.printStackTrace();

					// log entry for this exception
					ServerUtils.logEntry("SApp exception " + ex + ".");
				}

			} // end 'while' which started at beginning of program

		} // end 1st try
		catch (Exception ex) // catch for 1st try
		{
			ex.printStackTrace();

			// log entry for this exception
			ServerUtils.logEntry("SApp exception " + ex + ".");
		}
	} // end main
} // end class