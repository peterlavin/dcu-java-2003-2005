package client;

import java.util.*;
import java.text.*;
import java.io.*;

/**
 *
 * @author Peter Lavin xxxxxxxx, CA587 PT, GDF1 DCU
 * @version April 19th 2004
 *
 *          This class contains methods to support the client app.
 *
 */
class ClientUtils {


	/**
	 *
	 * Gets a user input (int) after the menu is displayed. It uses validation to
	 * check the value returned is matched by case in the CApp class.
	 * 
	 * No parameter is passed in here.
	 * 
	 * @return menuChoice int value 1-6
	 *
	 */
	public static int getMenuChoice() {
		int menuChoice = 0;
		boolean ok;
		int closeValidation = 0;

		Scanner scanner = new Scanner(System.in);

		do {
			System.out.println("Type and Enter your choice, " + "1 - 6 or 0 to EXIT");

			menuChoice = scanner.nextInt();

			// validation for entry
			ok = ((menuChoice < 7) && (menuChoice > -1));
			if (!ok)
				System.out.println("Use 1 to 6, or 0 to exit, try again");
		} while (!ok);

		switch (menuChoice) {
		// case for 0 to validate exit choice
		case 0:

			try {
				do {
					System.out.println("\nIncorrect entry OR EXIT chosen, " + "Enter 0 to close Client Program");
					System.out.println("or enter any other menu option 1 - 6 to continue");
					closeValidation = scanner.nextInt();

					// validation for entry
					ok = ((closeValidation < 7) && (closeValidation > -1));
					if (!ok)
						System.out.println("Use 1 to 6, or 0 to exit, try again");
				} while (!ok);
				if (closeValidation == 0) {
					menuChoice = closeValidation;
				} else {
					menuChoice = closeValidation;
				}
			} // end try

			catch (Exception ex) {
				ex.printStackTrace();
			}
			break;
		}

		return menuChoice;

	}


	/**
	 *
	 * Builds a string which can be printed in CApp to display a menu. The
	 * information used for the menu is coded here.
	 * 
	 * @return menuString value containing all menu options, numbers, etc.
	 * 
	 */
	public static String buildMenu() {

		// making array for menu string
		int[] menuNosArray = { 1, 2, 3, 4, 5, 6, 0 };
		String[] menuStrArray = { "List Files on Server", "List local files (client files)", "Sent File to Server",
				"Retrieve files from Server", "Delete Files on Server", "Delete Files on Client side", "Exit Program" };

		String menuString = "\n";

		for (int i = 0; i < menuNosArray.length; i++) {
			menuString = menuString + menuNosArray[i] + " - " + menuStrArray[i] + "\n";
		}

		return menuString;
	}


	/**
	 *
	 * Generates a string which is used by the logEntry(entry) method in this class.
	 * It creates a Calender object and modifies slightly the values obtained from
	 * that. Miliseconds are also used here for debugging, performance measuring,
	 * etc.
	 * 
	 * @return dateTime string containing date and time
	 * 
	 */
	private static String dateTime() {

		// creating a new calender object
		// code used here is from javaalmanac.com
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

		// changes 0 and 1 to AM and PM
		String AMPM = "";
		if (ampm == 0) {
			AMPM = "AM";
		} else {
			AMPM = "PM";
		}

		// switch-case sets month int to string, 0 = Jan, 1 = Feb
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

		// applying formatting, converting to strings for dateTime string
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


	/**
	 *
	 * Creates one long string from all the elements of a string array, returns a
	 * String, called by the local LIST option in CApp class this list is used for
	 * the local list display (2)
	 * 
	 * @return allFiles string for display of local file list
	 */
	public static String arrayToString(String[] allFilesString) {
		String allFiles = "\n";

		// joining all array elements to make a single string
		for (int k = 0; k < allFilesString.length; k++) {
			allFiles = allFiles + allFilesString[k] + "\n";
		}

		return allFiles;
	}


	/**
	 *
	 * Creates a log file if ClientLog.log does not already exist opening entry is
	 * also made to log its creation. This method calls calls fileExistsCheck() in
	 * this class to check if a log needs to be created. dateTime() method is also
	 * called in this class. It also creates FileWriter and PrintWriter objects
	 *
	 * @throws IO exception
	 */
	public static void logFileCreator() {
		String logFileName = "ClientLog.log";

		try {
			if (fileExistsCheck("ClientLog.log") == true) {
				System.out.println("\nClientLog already exists and will be appended.");
			} else {
				// file is created if it does not already exist
				System.out.println("\nNew ClientLog file created   " + dateTime() + ".");
				FileWriter fw = new FileWriter(logFileName);
				// adds an opening entry
				PrintWriter pw = new PrintWriter(fw);
				pw.println(dateTime() + "   ClientLog file created.");
				pw.close();
				fw.close();
			}
		} catch (Exception ex) {
			System.out.println("Problem with creating ClientLog.log");
			ex.printStackTrace();
		}
	} // end method


	/**
	 *
	 * Adds to log file once ClientLog.log exists, creates a FileWriter and
	 * PrintWriter objects. This method uses the dateTime() in this class for each
	 * log entry
	 * 
	 * @param entry text string comment from CApp
	 * 
	 * @throws IO exception
	 *
	 */
	public static void logEntry(String entry) {
		try {
			// opening the existing log, true passed in here indicates
			// that the file already exists
			FileWriter fw = new FileWriter("ClientLog.log", true);
			// creating a PrintWriter object to write to file
			PrintWriter pw = new PrintWriter(fw);
			pw.println(dateTime() + "    " + entry);
			pw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} // end method

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
			FileWriter fw = new FileWriter("ClientLog.log", true);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(" ");
			pw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} // end method

	/**
	 *
	 * Check if a file exists on the client side. This is used to catch incorrect
	 * file names and verify that the file which is assigned to the FileOutputStream
	 * exists.
	 * 
	 * No parameter is passed in here.
	 * 
	 * @return fileObj.exists boolean
	 * 
	 */
	public static boolean fileExistsCheck(String fileName) {

		File fileObj = new File(fileName);
		return fileObj.exists();
	} // end method

} // end class