package TrainingDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Class to interact with database storing the information. All SQLite commands
 * are done through here.
 * 
 * @author turnerw
 * 
 */
public class DBinterface {

	private String driverName = "org.sqlite.JDBC";
	private String dbName = "trainings.db";
	private String dbPath = "C:/Users/turnerw/Desktop/Training Database/";
	private String jdbc = "jdbc:sqlite:";
	private String dbURL = jdbc + dbPath + dbName;
	private int timeout = 30;
	private Connection conn;
	private Statement stmt;

	/**
	 * Public constructor.
	 * 
	 * @throws Exception
	 */
	public DBinterface() throws Exception {
		// Register the driver.
		Class.forName(driverName);
	}

	/**
	 * Clean the database. Remove all data and return to pristine condition.
	 * 
	 * @throws Exception
	 */
	private void cleanDB() throws Exception {
		this.dropAllTables();
		this.dropAllViews();
		this.dropAllTriggers();
		this.initializeDB();
	}

	/**
	 * Clear district data from the database. Leave all the course and position
	 * information so do not need to initialize again.
	 * 
	 * @throws Exception
	 */
	private void clearDB() throws Exception {
		// ensure all tables exist
		this.initializeDB();

		this.openConnection();
		stmt.executeUpdate("delete from trainings;");
		stmt.executeUpdate("delete from scouters;");
		stmt.executeUpdate("delete from units;");
		stmt.executeUpdate("delete from unitMembers;");
		this.closeConnection();
	}

	/**
	 * Remove all tables from the database.
	 * 
	 * @throws Exception
	 */
	private void dropAllTables() throws Exception {
		this.openConnection();
		stmt.executeUpdate("PRAGMA writable_schema = 1;");
		stmt.executeUpdate("delete from sqlite_master where type = 'table';");
		stmt.executeUpdate("PRAGMA writable_schema = 0;");
		stmt.executeUpdate("VACUUM");
		stmt.executeUpdate("PRAGMA INTEGRITY_CHECK;");
		this.closeConnection();
	}

	/**
	 * Remove all views from the database.
	 * 
	 * @throws Exception
	 */
	private void dropAllViews() throws Exception {
		this.openConnection();
		stmt.executeUpdate("PRAGMA writable_schema = 1;");
		stmt.executeUpdate("delete from sqlite_master where type = 'view';");
		stmt.executeUpdate("PRAGMA writable_schema = 0;");
		stmt.executeUpdate("VACUUM");
		stmt.executeUpdate("PRAGMA INTEGRITY_CHECK;");
		this.closeConnection();
	}

	/**
	 * Remove all triggers from the database.
	 * 
	 * @throws Exception
	 */
	private void dropAllTriggers() throws Exception {
		this.openConnection();
		stmt.executeUpdate("PRAGMA writable_schema = 1;");
		stmt.executeUpdate("delete from sqlite_master where type = 'trigger';");
		stmt.executeUpdate("PRAGMA writable_schema = 0;");
		stmt.executeUpdate("VACUUM");
		stmt.executeUpdate("PRAGMA INTEGRITY_CHECK;");
		this.closeConnection();
	}

	/**
	 * Initialize the database. Ensure all tables exist.
	 * 
	 * @throws Exception
	 */
	private void initializeDB() throws Exception {
		this.dropAllViews();
		this.dropAllTriggers();

		this.openConnection();

		/*
		 * Create tables for training data
		 */

		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "trainings (memberId INTEGER, courseId STRING, date STRING)");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "scouters (memberId INTEGER PRIMARY KEY ON CONFLICT REPLACE, firstName STRING, middleName STRING, lastName STRING)");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "courses (courseId STRING UNIQUE ON CONFLICT REPLACE, courseName STRING)");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "courseLengths (courseId STRING UNIQUE ON CONFLICT REPLACE, years INTEGER)");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "expiredCourses (courseId STRING UNIQUE ON CONFLICT REPLACE, date STRING)");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "positions (positionCode STRING UNIQUE ON CONFLICT REPLACE, positionName STRING)");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "requirements (positionCode STRING, unitType STRING, courseId String)");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "units (unitId STRING UNIQUE ON CONFLICT REPLACE, unitType STRING, unitNumber INTEGER, charterOrg STRING)");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "
				+ "unitMembers (unitId STRING, memberId INTEGER, positionCode STRING)");

		/*
		 * Create triggers to ensure no duplicate records in databases without
		 * unique attributes.
		 */
		stmt.executeUpdate("create trigger if not exists "
				+ "trainingsNoDups before insert on trainings for each row "
				+ "when exists (select * from trainings where new.memberId = memberId and new.courseId = courseId and new.date = date) "
				+ "begin select raise(ignore); end;");
		stmt.executeUpdate("create trigger if not exists "
				+ "unitMembersNoDups before insert on unitMembers for each row "
				+ "when exists (select * from unitMembers where new.unitId = unitId and new.memberId = memberId and new.positionCode = positionCode) "
				+ "begin select raise(ignore); end;");
		stmt.executeUpdate("create trigger if not exists "
				+ "requirementsNoDups before insert on requirements for each row "
				+ "when exists (select * from requirements where new.positionCode = positionCode and new.unitType = unitType and new.courseId = courseId) "
				+ "begin select raise(ignore); end;");
		// Trigger that units can only have multiple assistant leaders and committee members
		stmt.executeUpdate("create trigger if not exists "
				+ "limitLeaders before insert on unitMembers for each row "
				+ "when (new.positionCode = 'CC' or new.positionCode = 'SM' or new.positionCode = 'CM' or new.positionCode = 'CR' or new.positionCode = 'IH' or new.positionCode = 'NL' or new.positionCode = 'SK' or new.positionCode = 'VC') "
				+ " and exists (select * from unitMembers where new.positionCode = positionCode and new.unitId = unitId) "
				+ "begin delete from unitMembers where new.positionCode = positionCode and new.unitId = unitId; end;");

		/*
		 * Create views for trained and untrained scouters.
		 */
		// View of entire database
		stmt.executeUpdate("create view if not exists entireDB as "
				+ "select * from scouters natural join (units natural join unitMembers) natural join positions natural left outer join (courses natural join trainings);");
		// stmt.executeUpdate("create view if not exists entireDB as select * from scouters natural join unitMembers natural left outer join (courses natural join trainings);");

		// Views of expired and unexpired trainings
		stmt.executeUpdate("create view if not exists expiredTrainings as "
				+ "select memberId, courseId, date from trainings join courseLengths using (courseId) where julianday('now') > julianday(date, replace('+X years','X',years));");
		stmt.executeUpdate("create view if not exists unexpiredTrainings as "
				+ "select * from trainings except select * from expiredTrainings;");
		stmt.executeUpdate("create view if not exists neededTrainings as "
				+ "select unitId, unitMembers.positionCode, memberId, courseId from unitMembers natural join units natural join requirements except select unitId, positionCode, memberId, courseId from unitMembers natural join unexpiredTrainings;");
		stmt.executeUpdate("create view if not exists untrained as "
				+ "select * from scouters natural join (units natural join unitMembers) natural join positions courses natural join (courses natural join neededTrainings);");
		stmt.executeUpdate("create view if not exists trained as "
				+ "select * from scouters natural join (units natural join unitMembers) natural join positions natural join (select * from unitMembers where positionCode <> 'M' except select unitId, memberId, positionCode from untrained);");

		// Views of expired YPT
		stmt.executeUpdate("create view if not exists maxYPTjulianday as "
				+ "select memberId, courseId, max(julianday(date)) as maxJD from trainings natural join courses where courseName like '%Youth Protection Training%' group by memberId, courseId;");
		stmt.executeUpdate("create view if not exists latestYPTrainings as "
				+ "select trainings.memberId, trainings.courseId, date from trainings, maxYPTjulianday where trainings.memberID = maxYPTjulianday.memberID and trainings.courseID = maxYPTjulianday.courseID and julianday(date) = maxJD;");
		stmt.executeUpdate("create view if not exists expiredYPT as "
				+ "select * from (select * from untrained where courseName like '%Youth Protection Training%') natural join latestYPTrainings;");

		this.closeConnection();
	}

	/**
	 * Convert database to a string for display.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String dbToString() throws Exception {
		String output = ""; // Initially empty output string
		this.openConnection();

		// get names of tables in a ResultSet
		ResultSet tables = stmt
				.executeQuery("SELECT tbl_name from sqlite_master where type='table'");

		// iterate over tables, appending each to the string
		while (tables.next()) {
			String tableName = tables.getString("tbl_name");
			output += "TABLE: "
					+ tableName
					+ "\n"
					+ "-------------------------------------------------------------------------\n";
			output += this.tableToString(tableName) + "\n\n";
		}

		this.closeConnection();

		return output;
	}

	/**
	 * Convert a table to a string for displaying
	 * 
	 * @return
	 * @throws Exception
	 */
	public String tableToString(String table) throws Exception {
		String output = ""; // initially empty output String
		this.openConnection();

		// get all information from table
		ResultSet rs = stmt.executeQuery("SELECT * from " + table);

		if (rs != null) {
			ResultSetMetaData metaData = rs.getMetaData();
			int numberOfColumns = metaData.getColumnCount();

			// append column headers to output string
			for (int i = 1; i <= numberOfColumns; ++i)
				output += String.format("%-8s\t", metaData.getColumnName(i));
			output += "\n";

			// append each row to the output string
			while (rs.next()) {
				for (int i = 1; i <= numberOfColumns; ++i)
					output += String.format("%-8s\t", rs.getObject(i));
				output += "\n";
			}
		}

		this.closeConnection();

		return output;
	}

	private void openConnection() throws Exception {
		conn = DriverManager.getConnection(dbURL);
		stmt = conn.createStatement();
		stmt.setQueryTimeout(timeout);
	}

	private void closeConnection() throws Exception {
		stmt.close();
		conn.close();
	}

	/**
	 * Load training records from a file. The file may be either a CSV file or a
	 * directory of CSV files.
	 * 
	 * @param file
	 */
	public void loadTrainings(File file) {
		if (file.isFile())
			this.loadTrainingsFile(file);
		else if (file.isDirectory()) {

			// get array of files in directory
			File[] listOfFiles = file.listFiles();

			// load each file in directory using recursive in case have
			// subdirectories
			for (File innerFile : listOfFiles)
				this.loadTrainings(innerFile);

		}

	}

	/**
	 * Inserting training records from CSV file into database.
	 * 
	 * This is a private method that should only be used through the public
	 * loadTrainings method.
	 * 
	 * @param fileName
	 */
	private void loadTrainingsFile(File file) {
		// File file = new File(fileName);
		System.out.println("Loading trainings file " + file.getAbsolutePath()
				+ " . . .");

		try {
			// create Scanner object for ease of grabbing lines
			Scanner fileScanner = new Scanner(file);

			// check if file is empty
			if (!fileScanner.hasNext()) {
				fileScanner.close();
				return;
			}

			// first line contains header information
			List<String> headers = parseLine(fileScanner.nextLine());

			// parse rest of lines
			List<String> line;
			int space1, space2;
			String name, firstName, middleName, lastName, courseId, courseName, date, unit, unitId, unitType, charterOrg, positionCode;
			int unitNumber, memberId;
			Scanner positions;
			this.openConnection();
			while (fileScanner.hasNext()) {
				line = parseLine(fileScanner.nextLine());

				// insert scouter into database
				memberId = Integer.parseInt(line.get(2));
				name = line.get(3);
				space1 = name.indexOf(" ");
				space2 = name.indexOf(" ", space1 + 1);
				firstName = name.substring(0, space1).trim();
				middleName = name.substring(space1 + 1, space2).trim();
				lastName = name.substring(space2 + 1).trim();
				stmt.executeUpdate("insert into scouters(memberId, firstName, middleName, lastName) values('"
						+ memberId
						+ "',\""
						+ firstName
						+ "\",\""
						+ middleName
						+ "\",\"" + lastName + "\")");

				// insert course into database
				if (line.size() > 6) {
					courseId = line.get(5);
					courseName = line.get(6);
					stmt.executeUpdate("insert into courses(courseId, courseName) values('"
							+ courseId + "',\"" + courseName + "\")");

					// insert training into database
					if (line.size() > 7) {
						date = this.timestring(line.get(7));
						stmt.executeUpdate("insert into trainings(memberId, courseId, date) values('"
								+ memberId
								+ "','"
								+ courseId
								+ "','"
								+ date
								+ "')");
					}

				}

				// insert unit information
				unit = line.get(1);
				space1 = unit.indexOf(" ");
				space2 = unit.indexOf(" ", space1 + 1);
				unitType = unit.substring(0, space1);
				unitNumber = Integer.parseInt(unit
						.substring(space1 + 1, space2));
				charterOrg = unit.substring(space2 + 1);
				unitId = unitType + "-" + unitNumber;
				stmt.executeUpdate("insert into units(unitId, unitType, unitNumber, charterOrg) values('"
						+ unitId
						+ "','"
						+ unitType
						+ "',"
						+ unitNumber
						+ ",\""
						+ charterOrg + "\");");

				// insert unit volunteer information
				positions = new Scanner(line.get(4));
				positions.useDelimiter(",");
				while (positions.hasNext()) {
					positionCode = positions.next();
					stmt.executeUpdate("insert into unitMembers(unitId, memberId, positionCode) values('"
							+ unitId
							+ "','"
							+ memberId
							+ "','"
							+ positionCode
							+ "');");
				}

			}

			this.closeConnection();
			fileScanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// this.removeDuplicatesFromTable("trainings");
			// this.removeDuplicatesFromTable("unitMembers");
		}

	}

	/**
	 * Load initializations from a directory
	 * 
	 * @param file
	 *            Directory containing initalization files
	 */
	public void loadInitializations(File file) {

		String directory = file.getAbsolutePath();
		this.loadCoursesFile(new File(directory + "/courses.csv"));
		this.loadPositionsFile(new File(directory + "/positions.csv"));
		this.loadRequirementsFile(new File(directory + "/requirements.csv"));

	}

	/**
	 * Inserting course information from CSV file into database.
	 * 
	 * @param fileName
	 */
	public void loadCoursesFile(File file) {

		System.out.println("Loading courses file " + file.getAbsolutePath()
				+ " . . .");

		try {
			// create Scanner object for ease of grabbing lines
			Scanner fileScanner = new Scanner(file);

			// check if file is empty
			if (!fileScanner.hasNext()) {
				fileScanner.close();
				return;
			}

			// first line contains header information
			List<String> headers = parseLine(fileScanner.nextLine());

			// parse rest of lines
			List<String> line;
			this.openConnection();
			while (fileScanner.hasNext()) {
				line = parseLine(fileScanner.nextLine());

				// insert course into database
				stmt.executeUpdate("insert into courses(courseId, courseName) values('"
						+ line.get(0) + "',\"" + line.get(1) + "\")");

				// insert length into database
				if (line.size() > 2 && !line.get(2).equals(""))
					stmt.executeUpdate("insert into courseLengths(courseId, years) values('"
							+ line.get(0) + "','" + line.get(2) + "')");

				// insert expired dates into database
				if (line.size() > 3 && !line.get(3).equals(""))
					stmt.executeUpdate("insert into expiredCourses(courseId, date) values('"
							+ line.get(0)
							+ "','"
							+ this.timestring(line.get(3)) + "')");

			}

			this.closeConnection();
			fileScanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Remove duplicate rows from the tables.
			// Not necessary because all have unique attributes.
			// this.removeDuplicatesFromTable("courses");
			// this.removeDuplicatesFromTable("lengths");
			// this.removeDuplicatesFromTable("expired");
		}

	}

	/**
	 * Inserting position information from CSV file into database.
	 * 
	 * @param fileName
	 */
	public void loadPositionsFile(File file) {

		System.out.println("Loading positions file " + file.getAbsolutePath()
				+ " . . .");

		try {
			// create Scanner object for ease of grabbing lines
			Scanner fileScanner = new Scanner(file);

			// check if file is empty
			if (!fileScanner.hasNext()) {
				fileScanner.close();
				return;
			}

			// first line contains header information
			List<String> headers = parseLine(fileScanner.nextLine());

			// parse rest of lines
			List<String> line;
			this.openConnection();
			while (fileScanner.hasNext()) {
				line = parseLine(fileScanner.nextLine());

				// insert position into database
				stmt.executeUpdate("insert into positions(positionCode, positionName) values('"
						+ line.get(0) + "',\"" + line.get(1) + "\")");
			}

			this.closeConnection();
			fileScanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Remove duplicate rows from the tables.
			// Not necessary because all have unique attributes.
			// this.removeDuplicatesFromTable("positions");
		}

	}

	/**
	 * Inserting training requirement information from CSV file into database.
	 * 
	 * @param fileName
	 */
	public void loadRequirementsFile(File file) {

		System.out.println("Loading requirements file "
				+ file.getAbsolutePath() + " . . .");

		try {
			// create Scanner object for ease of grabbing lines
			Scanner fileScanner = new Scanner(file);

			// check if file is empty
			if (!fileScanner.hasNext()) {
				fileScanner.close();
				return;
			}

			// first line contains header information
			List<String> headers = parseLine(fileScanner.nextLine());

			// parse rest of lines
			List<String> line;
			String query, positionCode, unitType;
			this.openConnection();
			while (fileScanner.hasNext()) {
				line = parseLine(fileScanner.nextLine());

				// store position code and unit type for later use
				positionCode = line.get(0);
				unitType = line.get(1);

				// insert requirements into database
				for (int i = 2; i < line.size(); ++i) {
					query = "insert into requirements(positionCode, unitType, courseId) values('"
							+ positionCode
							+ "','"
							+ unitType
							+ "','"
							+ line.get(i) + "')";
					stmt.executeUpdate(query);
					// System.out.println(query);
				}
			}

			this.closeConnection();
			fileScanner.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Remove duplicate rows from the tables.
			// this.removeDuplicatesFromTable("requirements");
		}

	}

	/**
	 * Converts a date string to an SQLite-compatible timestring format. This
	 * assumes the input date is of the form m/d/y or m-d-y.
	 * 
	 * @param date
	 * @return
	 */
	private String timestring(String date) {
		// temporary storage for date components
		int year = 0;
		int month = 0;
		int day = 0;
		int delim1, delim2; // positions of the deliminators

		// First try / as deliminator
		if (0 < (delim1 = date.indexOf("/"))) {
			delim2 = date.indexOf("/", delim1 + 1);
			month = Integer.parseInt(date.substring(0, delim1));
			day = Integer.parseInt(date.substring(delim1 + 1, delim2));
			year = Integer.parseInt(date.substring(delim2 + 1));
		} else if (0 < (delim1 = date.indexOf("-"))) {
			delim2 = date.indexOf("-", delim1 + 1);
			month = Integer.parseInt(date.substring(0, delim1));
			day = Integer.parseInt(date.substring(delim1 + 1, delim2));
			year = Integer.parseInt(date.substring(delim2 + 1));
		}

		//

		return String.format("%4d-%02d-%02d", year, month, day);
	}

	/**
	 * Remove all the duplicate rows from a table.
	 * 
	 * @param table
	 *            Name of the table from which to remove duplicates.
	 */
	private void removeDuplicatesFromTable(String table) {
		try {
			this.openConnection();
			stmt.execute("create temporary table temp_table as select distinct * from "
					+ table + ";");
			stmt.execute("drop table " + table + ";");
			stmt.execute("create table " + table
					+ " as select distinct * from temp_table;");
			// stmt.execute("drop table temp_table;");
			this.closeConnection();
			
			// Ensure all triggers are active
			this.initializeDB();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Print contents of file
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	private void printContents(File file) throws FileNotFoundException {
		Scanner input = new Scanner(file);

		while (input.hasNext()) {
			System.out.println(input.nextLine());
		}

		input.close();
	}

	/**
	 * Parse line of a CSV file
	 * 
	 * @param string
	 * @return
	 */
	private List<String> parseLine(String string) {
		// System.out.println("Scanning line " + string);

		// create scanner object to search for commas
		Scanner line = new Scanner(string);
		line.useDelimiter(",");

		// create empty ArrayList for output
		ArrayList<String> list = new ArrayList<String>();

		// Iterate over entries in line
		String item;
		while (line.hasNext()) {
			item = line.next();
			if (item.startsWith("\"") && !item.endsWith("\"")) {
				do {
					item += "," + line.next();
				} while (!item.endsWith("\"") && line.hasNext());

				item = item.substring(1, item.length() - 1);
			}

			list.add(item);
		}

		line.close();
		// System.out.println("Complete line: " + list.toString());

		return list;

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		DBinterface dbi = new DBinterface();
		// dbi.cleanDB();
		dbi.initializeDB();
		// System.out.println(dbi.tableToString("sqlite_master"));

		 // load initializations from directory
		 dbi.loadInitializations(new File(
		 "C:/Users/turnerw/Desktop/Training Database/Initializations/"));
		
		// // load trainings from directory
		// dbi.loadTrainings(new File(
		// "C:/Users/turnerw/Desktop/Training Database/2013-05/"));

		// print entire database for testing
		System.out.println(dbi.dbToString());

		System.out.println(dbi.tableToString("untrained"));

	}

}
