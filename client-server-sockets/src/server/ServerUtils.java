package server;

import java.io.*; // contains the stream classes
import java.text.*; //  contains the format classes
import java.util.*; // contains the time classes

/**
 *
 * @author Peter Lavin xxxxxxxx, CA587 PT, GDF1 DCU
 * @version April 19th 2004
 *
 *          This class contains methods to support the server app
 * 
 */
class ServerUtils { // begin class

	///////////////////////////////////////////////////////////////

	/**
	 *
	 * Generates a string which is used by the logEntry(entry) method in this class.
	 * It creates a Calender object and modifies slightly the values obtained from
	 * that. Miliseconds are also used here for debugging, performance measuring,
	 * etc.
	 * 
	 * @return dateTime string date and time, with mSec
	 * 
	 */
	private static String dateTime() {

		// creating a new calender object
		// code used from javaalmanac.com
		Calendar cal = new GregorianCalendar();

		// Get the components of the date
		int era = cal.get(Calendar.ERA);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int hour12 = cal.get(Calendar.HOUR);
		int hour24 = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		int ms = cal.get(Calendar.MILLISECOND);
		int ampm = cal.get(Calendar.AM_PM);

		// changes 0 to 12 in both AM and PM cases
		if (hour12 == 0) {
			hour12 = 12;
		}

		// changes 0 and 12 to AM and PM
		String AMPM = "";
		if (ampm == 0) {
			AMPM = "AM";
		} else {
			AMPM = "PM";
		}

		// sets month int to string, 0=Jan, 1=Feb, ...
		String monthOutput = "";

		switch (month) {
		case 0:
			monthOutput = "Jan";
			break;
		case 1:
			monthOutput = "Feb";
			break;
		case 2:
			monthOutput = "Mar";
			break;
		case 3:
			monthOutput = "Apr";
			break;
		case 4:
			monthOutput = "May";
			break;
		case 5:
			monthOutput = "Jun";
			break;
		case 6:
			monthOutput = "Jul";
			break;
		case 7:
			monthOutput = "Aug";
			break;
		case 8:
			monthOutput = "Sep";
			break;
		case 9:
			monthOutput = "Oct";
			break;
		case 10:
			monthOutput = "Nov";
			break;
		case 11:
			monthOutput = "Dec";
			break;
		}

		// formats mins and seconds to 00
		DecimalFormat ooFormat = new DecimalFormat("00");
		DecimalFormat oooFormat = new DecimalFormat("000");
		DecimalFormat ooooFormat = new DecimalFormat("0000");
		DecimalFormat xxFormat = new DecimalFormat("");

		// applying formatting, converting to strings for
		String dayOutput = xxFormat.format(day);
		String yearOutput = ooooFormat.format(year);
		String hour12Output = xxFormat.format(hour12);
		String minOutput = ooFormat.format(min);
		String secOutput = ooFormat.format(sec);
		String mSecOutput = oooFormat.format(ms);

		String dateTime = dayOutput + " " + monthOutput + " " + yearOutput + "  " + hour12Output + ":" + minOutput + ":"
				+ secOutput + ":" + mSecOutput + " " + AMPM;

		return dateTime;

	}

	///////////////////////////////////////////////////////////////////
	/**
	 * 
	 * Creates one long string from all the elements of a string array, returns a
	 * String, called by the SApp when requested to send a list of files on the
	 * server to the client.
	 * 
	 * @param allFilesString array from SApp
	 * 
	 * @return allFiles string for sending to client
	 * 
	 */
	public static String arrayToString(String[] allFilesString) {
		String allFiles = "\n";

		for (int k = 0; k < allFilesString.length; k++) {
			allFiles = allFiles + allFilesString[k] + "\n";
		}

		return allFiles;
	}

	/////////////////////////////////////////////////////////////////////

	/**
	 *
	 * Creates a log file if ServerLog.log does not already exist opening entry is
	 * also made to log its creation. This method calls calls fileExistsCheck() in
	 * this class to check if a log needs to be created. dateTime() method is also
	 * called in this class. It also creates FileWriter and PrintWriter objects
	 *
	 * @throws IO exception
	 *
	 */
	public static void logFileCreator() {
		String logFileName = "ServerLog.log";

		try {
			if (fileExistsCheck("ServerLog.log") == true) {
				System.out.println("\nServerLog already " + "exists and will be appended.");
			} else {
				// file is created if it does not already exist
				System.out.println("\nNew ServerLog file created   " + dateTime() + ".");
				FileWriter fw = new FileWriter(logFileName);
				// adds an opening entry
				PrintWriter pw = new PrintWriter(fw);
				pw.println(dateTime() + "   ServerLog file created.");
				pw.close();
				fw.close();
			}
		} catch (Exception ex) {
			System.out.println("Problem with creating ServerLog.log");
			ex.printStackTrace();
		}
	} // end method

	////////////////////////////////////////////////////////////////////

	/**
	 *
	 * Adds to log file once ServerLog.log exists, creates a FileWriter and
	 * PrintWriter objects. This method uses the dateTime()in this class for each
	 * log entry
	 * 
	 * @param entry string comment from SApp
	 * 
	 * @throws IO exception
	 *
	 */
	public static void logEntry(String entry) {
		try {
			// opening the existing log, true passed in here indicates
			// that the file already exists
			FileWriter fw = new FileWriter("ServerLog.log", true);
			// creating a PrintWriter object to write to file
			PrintWriter pw = new PrintWriter(fw);
			pw.println(dateTime() + "    " + entry);
			pw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} // end method
		////////////////////////////////////////////////////////////////////

	/**
	 *
	 * Adds a blank line to log file for clarity, creates FileWriter and PrintWriter
	 * objects.
	 * 
	 * No parameter is passed in here.
	 * 
	 * @throws IO exception
	 *
	 */
	public static void logEntry() {
		try {
			// opening the existing log, true passed in here indicates
			// that the file already exists
			FileWriter fw = new FileWriter("ServerLog.log", true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(" ");
			pw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} // end method

	////////////////////////////////////////////////////////////////////

	/**
	 *
	 * Checks if a file exists on the client side. This is used to catch incorrect
	 * file names and verify that the file which is assigned to the FileOutputStream
	 * exists.
	 * 
	 * No parameter is passed in here.
	 * 
	 * @param fileName string for checking.
	 * 
	 */
	public static boolean fileExistsCheck(String fileName) {
		File fileObj = new File(fileName);
		return fileObj.exists();
	} // end method

} // end class