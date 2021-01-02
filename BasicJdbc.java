package se.kth.iv1351.jdbcintro;

import java.sql.*;
import java.util.*;


/**
 * A JDBC  program.
 */
public class BasicJdbc {

  public static void main (String[] args) throws SQLException {


    Connection connection = null;
    try {

      connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/soundgood_school?serverTimezone=UTC", "root", "minRocky4");
      connection.setAutoCommit(false);
      System.out.println("------------------available instruments that can be leased---------------------");

      //create connection
      Statement statement = connection.createStatement();
      ResultSet results0;

      results0 = statement.executeQuery(
              "SELECT instrument_kind,price,instrument.id,brand\n" +
                      " FROM instrument \n" +
                      " WHERE instrument.id NOT IN (SELECT lease.instrument_id FROM lease WHERE terminated_rental=0); ");


      while (results0.next()) {
        String instrumentKind = results0.getString("instrument_kind");
        String price = results0.getString("price");
        String id = results0.getString("id");
        String brand = results0.getString("brand");
        System.out.println("brand:" + brand + " " + instrumentKind + ": " + price + "kr" + " " + "id:" + id);

      }
      System.out.println("-------------------Rent an instrument--------------------\n");
      System.out.println("requirements, you must already have a student_id, the ones available to use for testing purposes are the number from 1 to 6 ");

      Scanner scanner = new Scanner(System.in);
      System.out.print("Type in your student id: ");
      String studentId = scanner.nextLine();

      String queryy = "SELECT count(student_id) As s FROM lease WHERE student_id=? AND terminated_rental=0  GROUP BY student_id HAVING s>1 ";
      PreparedStatement st = connection.prepareStatement(queryy);
      st.setString(1, studentId);
      ResultSet result1 = st.executeQuery();

      if (result1.next()) {
        System.out.println("ERROR STUDENT HAVE ALREADY REACHED RENT LIMIT");

        System.out.println("*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n");
        System.out.println("---------terminate rental-----------");
        Scanner scanner2 = new Scanner(System.in);

        System.out.println("Type in your student id: ");
        String leaseId2 = scanner2.nextLine();
        String query2 = "SELECT id, terminated_rental FROM lease WHERE student_id=?";
        PreparedStatement stmt1 = connection.prepareStatement(query2);
        stmt1.setString(1, leaseId2);
        ResultSet result2 = stmt1.executeQuery();
        System.out.println("if, terminated rental= 0 it's an ongoing rental, if terminated_rental= 1 you have already terminated it");
        System.out.println("YOUR HISTORY LEASES");

        while (result2.next()) {
          String id = result2.getString("id");
          String rentals = result2.getString("terminated_rental");
          System.out.println("lease id: " + id + " " + "terminated rental:" + rentals);

        }

        System.out.println("Type in your leased id: ");
        String leaseId = scanner2.nextLine();

        String query3 = "UPDATE lease SET terminated_rental=1 WHERE lease.id =?;";
        PreparedStatement stmt2 = connection.prepareStatement(query3);
        stmt2.setString(1, leaseId);
        stmt2.executeUpdate();
        connection.commit();
        System.out.println("rental was terminated");
        System.exit(0);
      }

      System.out.print("Type in the instrument id for the instrument you will lease: ");
      String instrument_id = scanner.nextLine();
      System.out.print("Type in the date as year-month-day: ");
      String rented_date = scanner.nextLine();

      String query = "INSERT INTO lease (student_id,instrument_id,rented_date,terminated_rental)" + " VALUES (?,?,?,0) ";
      PreparedStatement stmt = connection.prepareStatement(query);
      stmt.setString(1, studentId);
      stmt.setString(2, instrument_id);
      stmt.setString(3, rented_date);
      stmt.executeUpdate();
      connection.commit();

      System.out.println("The instrument is now rented ");

      System.out.println("*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n" + "*\n");


      System.out.println("---------terminate rental-----------");
      Scanner scanner2 = new Scanner(System.in);

      System.out.println("Type in your student id: ");
      String userInput = scanner2.nextLine();
      String query3 = "SELECT id, terminated_rental FROM lease WHERE student_id=?";
      PreparedStatement stmt3 = connection.prepareStatement(query3);
      stmt3.setString(1, userInput);
      ResultSet result3 = stmt3.executeQuery();
      System.out.println("if, terminated rental= 0 it's an ongoing rental, if terminated_rental= 1 you have already terminated it");
      System.out.println("YOUR HISTORY LEASES");

      while (result3.next()) {
        String id = result3.getString("id");
        String rentals = result3.getString("terminated_rental");
        System.out.println("lease id: " + id + " " + "terminated rental:" + rentals);

      }
      System.out.println("Type in your leased id: ");
      String leaseId = scanner2.nextLine();

      String query4 = "UPDATE lease SET terminated_rental=1 WHERE lease.id =?;";
      PreparedStatement stmt4 = connection.prepareStatement(query4);
      stmt4.setString(1, leaseId);
      stmt4.executeUpdate();
      connection.commit();
      System.out.println("Terminated rental was successful");
      connection.close();

    } catch (SQLException e) {

      connection.rollback();
      e.printStackTrace();

    }

  }
}