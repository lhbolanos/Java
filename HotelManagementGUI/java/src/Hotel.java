/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.sql.Timestamp;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement ();

      ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");

                //the following functionalities basically used by managers
                System.out.println("5. Update Room Information");
                System.out.println("6. View 5 recent Room Updates Info");
                System.out.println("7. View booking history of the hotel");
                System.out.println("8. View 5 regular Customers");
                System.out.println("9. Place room repair Request to a company");
                System.out.println("10. View room repair Requests history");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewHotels(esql); break;
                   case 2: viewRooms(esql); break;
                   case 3: bookRooms(esql, authorisedUser); break;
                   case 4: viewRecentBookingsfromCustomer(esql, authorisedUser); break;
                   case 5: updateRoomInfo(esql, authorisedUser); break;
                   case 6: viewRecentUpdates(esql, authorisedUser); break;
                   case 7: viewBookingHistoryofHotel(esql, authorisedUser); break;
                   case 8: viewRegularCustomers(esql, authorisedUser); break;
                   case 9: placeRoomRepairRequests(esql, authorisedUser); break;
                   case 10: viewRoomRepairHistory(esql, authorisedUser); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter userID: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
            return userID;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

  public static void viewHotels(Hotel esql) {
        try{
        System.out.print("\tEnter latitude1: ");
        String userLat = in.readLine();


        System.out.print("\tEnter longitude1: ");
        String userLong = in.readLine();

        
        String query = String.format("SELECT hotelName FROM Hotel WHERE calculate_distance(%s, %s, Hotel.latitude, Hotel.longitude) < 30.0", userLat, userLong);


        esql.executeQueryAndPrintResult(query);
        }
        catch(Exception e){
                System.err.println(e.getMessage ());
        }




        }

   public static void viewRooms(Hotel esql) {   
      try{
      System.out.print("\tEnter hotelID: ");
      String hotelID = in.readLine();

      System.out.print("\tEnter date: ");
      String strDate = in.readLine();

  
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
      java.util.Date date = format.parse(strDate);

      DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
      String datePostgres = dFormat.format(date);
      
      String query = String.format("SELECT Rooms.roomNumber, Rooms.price FROM Rooms WHERE Rooms.hotelID = %s AND Rooms.roomNumber NOT IN (SELECT RoomBookings.roomNumber FROM RoomBookings WHERE RoomBookings.hotelID = %s AND RoomBookings.bookingDate = '%s')", hotelID, hotelID, datePostgres);
      
      esql.executeQueryAndPrintResult(query);
      
   }
   catch(Exception e){
         System.err.println (e.getMessage ());
      }
      }


   public static void bookRooms(Hotel esql, String aUser) {
      try{
      String authUser = aUser;

      System.out.print("\tEnter hotelID: ");
      String hotelID = in.readLine();

      System.out.print("\tEnter room number: ");
      String roomNum = in.readLine();

      System.out.print("\tEnter date: ");
      String strDate = in.readLine();

  
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
      java.util.Date date = format.parse(strDate);

      DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
      String dateTmp = dFormat.format(date);
      
      String query = String.format("SELECT Rooms.roomNumber, Rooms.price FROM Rooms WHERE Rooms.hotelID = %s AND Rooms.roomNumber = %s AND Rooms.roomNumber NOT IN (SELECT RoomBookings.roomNumber FROM RoomBookings WHERE RoomBookings.hotelID = %s AND RoomBookings.bookingDate = '%s')", hotelID, roomNum, hotelID, dateTmp);
      
      if(esql.executeQueryAndPrintResult(query)>0){
         System.out.println("\tRoom is available for booking! Room is now booked.");
         query = String.format("INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) VALUES (%s, %s, %s, '%s')", authUser, hotelID, roomNum, dateTmp);
         esql.executeUpdate(query);
      }
      else{
         System.out.println("\tWe're sorry - the selected room is currently unavailable for the given date.");
      } 
   }
   catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }


   public static void viewRecentBookingsfromCustomer(Hotel esql, String aUser) {
try{

//Save userID
String uID = aUser;

//Select: hotelID, roomNumber, billing information, date of booking.
String query = String.format("SELECT RoomBookings.hotelID, RoomBookings.roomNumber, Rooms.price, RoomBookings.bookingDate FROM Rooms,RoomBookings WHERE RoomBookings.customerID = %s AND RoomBookings.roomNumber = Rooms.roomNumber AND RoomBookings.hotelID = Rooms.hotelID ORDER BY RoomBookings.bookingDate DESC LIMIT 5", aUser);


esql.executeQueryAndPrintResult(query);

}
catch(Exception e){
      System.err.println (e.getMessage ());
   }
}



//Manager can update information of any room.
   public static void updateRoomInfo(Hotel esql, String aUser) {
try{

   

//Must check if hotel is managed by that manager. Users.userID == Hotels.managerUserID
String authUser = aUser;

//Must check to make sure the user is a manager. userType == manager
String query = String.format("SELECT * FROM Users WHERE Users.userID = %s AND Users.userType = 'manager'", authUser);

if (esql.executeQuery(query)>0){

//Show last 5 on updatesLog
//query = String.format("SELECT * FROM RoomUpdatesLog ORDER BY updateNumber DESC LIMIT 5");

//esql.executeQueryAndPrintResult(query);

   //Input hotelID
System.out.print("\tEnter hotelID: ");
String hotelID = in.readLine();

//Input roomNumber
System.out.print("\tEnter room number: ");
String roomNum = in.readLine();

//Query to check if room exists at hotel
query = String.format("SELECT Rooms.price, Rooms.imageURL FROM Rooms, Hotel WHERE Rooms.hotelID = %s AND Rooms.roomNumber = %s AND Hotel.managerUserID = %s AND Hotel.hotelID = Rooms.hotelID", hotelID, roomNum, authUser);

     if(esql.executeQueryAndPrintResult(query)>0){
         Timestamp timestamp = new Timestamp(System.currentTimeMillis());

         System.out.println("\tRoom exists.");

         System.out.print("\tEnter new price: ");
         String newPrice = in.readLine();

         System.out.print("\tEnter new imageURL: ");
         String newURL = in.readLine();

         query = String.format("UPDATE Rooms SET price = %s, imageURL = '%s' WHERE Rooms.hotelID = %s AND Rooms.roomNumber = %s", newPrice, newURL, hotelID, roomNum);

         esql.executeUpdate(query);

         query = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) VALUES (%s, %s, %s, '%s')", authUser, hotelID, roomNum, timestamp);

         esql.executeUpdate(query);

         System.out.println("\tRoom successfully updated.");

     }
     else{
      System.out.println("\tYou do not manage this hotel.");
     }
}
else{
   System.out.println("User is not a manager.");
}


}
   catch(Exception e){
         System.err.println (e.getMessage ());
      }


   }



   public static void viewRecentUpdates(Hotel esql, String aUser) {
      try{

      //Must check if hotel is managed by that manager. Users.userID == Hotels.managerUserID
String authUser = aUser;

//Must check to make sure the user is a manager. userType == manager
String query = String.format("SELECT * FROM Users WHERE Users.userID = %s AND Users.userType = 'manager'", authUser);

if (esql.executeQuery(query)>0){

//Show last 5 on updatesLog
query = String.format("SELECT * FROM RoomUpdatesLog ORDER BY updateNumber DESC LIMIT 5");

esql.executeQueryAndPrintResult(query);
}
else{
   System.out.println("\tYou are not a manager.");
}


      }
         catch(Exception e){
         System.err.println (e.getMessage ());
      }



}



   public static void viewBookingHistoryofHotel(Hotel esql, String aUser) {

      try{




//Must check if hotel is managed by that manager. Users.userID == Hotels.managerUserID
String authUser = aUser;

//Must check to make sure the user is a manager. userType == manager
String query = String.format("SELECT * FROM Users WHERE Users.userID = %s AND Users.userType = 'manager'", authUser);

if (esql.executeQuery(query)>0){

query = String.format("SELECT RoomBookings.bookingID, Users.name, RoomBookings.hotelID, RoomBookings.roomNumber, RoomBookings.bookingDate FROM Users,RoomBookings,Hotel WHERE RoomBookings.customerID = Users.userID AND RoomBookings.hotelID = Hotel.hotelID AND RoomBookings.customerID = Users.userID AND Hotel.managerUserID = %s", aUser);

esql.executeQueryAndPrintResult(query);


System.out.print("\tEnter start date: ");
String strDate = in.readLine();

System.out.print("\tEnter end date: ");
String endDate = in.readLine();

  
SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
java.util.Date date = format.parse(strDate);

DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
String dateTmp = dFormat.format(date);

java.util.Date date2 = format.parse(endDate);

String dateTmp2 = dFormat.format(date2);


query = String.format("SELECT RoomBookings.bookingID, Users.name, RoomBookings.hotelID, RoomBookings.roomNumber, RoomBookings.bookingDate FROM Users,RoomBookings WHERE RoomBookings.customerID = Users.userID AND RoomBookings.bookingDate >= '%s' AND RoomBookings.bookingDate <= '%s' ORDER BY RoomBookings.bookingDate DESC", dateTmp, dateTmp2);

esql.executeQueryAndPrintResult(query);

}else{

System.out.println("You are not a manager.");

}

      }
         catch(Exception e){
         System.err.println (e.getMessage ());
      }

   }




public static void viewRegularCustomers(Hotel esql, String aUser) {

try{

String authUser = aUser;

//Must check to make sure the user is a manager. userType == manager
String query = String.format("SELECT * FROM Users WHERE Users.userID = %s AND Users.userType = 'manager'", authUser);

if (esql.executeQuery(query)>0){

//Input userID
System.out.print("\tEnter hotelID for customer history: ");
String hID = in.readLine();


query = String.format("SELECT Hotel.hotelID FROM Hotel WHERE Hotel.hotelID = %s AND Hotel.managerUserID = %s",hID, aUser);

//Check to see if it's managers hotel
if (esql.executeQuery(query)>0){

query = String.format("SELECT Users.name, COUNT(customerID) AS Total FROM Users,RoomBookings WHERE RoomBookings.hotelID = %s AND Users.userID = RoomBookings.customerID GROUP BY Users.name ORDER BY Total DESC LIMIT 5", hID);

esql.executeQueryAndPrintResult(query);
}
else{
   System.out.println("You do not manage this hotel.");
}

}else{

System.out.println("You are not a manager.");

}
      }
         catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }




public static void placeRoomRepairRequests(Hotel esql, String aUser) {

try{

String authUser = aUser;

//Must check to make sure the user is a manager. userType == manager
String query = String.format("SELECT * FROM Users WHERE Users.userID = %s AND Users.userType = 'manager'", authUser);

if (esql.executeQuery(query)>0){

//Input userID
System.out.print("\tEnter hotelID: ");
String hID = in.readLine();

query = String.format("SELECT Hotel.hotelID FROM Hotel WHERE Hotel.hotelID = %s AND Hotel.managerUserID = %s",hID, aUser);

//Check to see if it's managers hotel
if (esql.executeQuery(query)>0){




System.out.print("\tEnter room number: ");
String roomNum = in.readLine();

System.out.print("\tEnter companyID: ");
String cID = in.readLine();



System.out.print("\tEnter date of repair: ");
String strDate = in.readLine();

  
SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
java.util.Date date = format.parse(strDate);

DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
String dateTmp = dFormat.format(date);


query = String.format("INSERT INTO RoomRepairs (companyID, hotelID, roomNumber, repairDate) VALUES (%s, %s, %s, '%s')", cID, hID, roomNum, dateTmp);

esql.executeUpdate(query);

query = String.format("INSERT INTO RoomRepairRequests (managerID, repairID) VALUES (%s, currval('roomrepairs_repairid_seq'))", aUser);

esql.executeUpdate(query);
}
else{
   System.out.println("This is not your hotel.");
}

}else{
System.out.println("You are not a manager.");
}
      }
         catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }


public static void viewRoomRepairHistory(Hotel esql, String authUser) {


   try{

String aUser = authUser;

//Must check to make sure the user is a manager. userType == manager
String query = String.format("SELECT * FROM Users WHERE Users.userID = %s AND Users.userType = 'manager'", authUser);

if (esql.executeQuery(query)>0){

query = String.format("SELECT RoomRepairs.companyID, RoomRepairs.hotelID, RoomRepairs.roomNumber, RoomRepairs.repairDate FROM RoomRepairs,RoomRepairRequests WHERE RoomRepairRequests.managerID = %s AND RoomRepairRequests.repairID = RoomRepairs.repairID", aUser);

esql.executeQueryAndPrintResult(query);

}
else{
   System.out.println("You are not a manager.");
}
   }

         
catch(Exception e){
System.err.println (e.getMessage ());

}
}
}//end Hotel



//BookingHistory: Looks good, need to seperate feature to filter by range.
//TODO: RegCustomers:View regular customers only show for hotel they manage DONE
//SubmitRepairRequests:Don't show all repair reuqests after entry - fixed
//View hotels wihtin 30 units --- Done
//View last 5 updated hotels of the MANAGER'S hotels.