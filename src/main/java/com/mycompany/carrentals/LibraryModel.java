package com.mycompany.carrentals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

public class LibraryModel {

    // For use in creating dialogs and making them modal
    private JFrame dialogParent;
    private String userPassword;
    private String userID;
    private String myURL;
    private Connection con;

    /**
     * Comment the following line to enable changes to the database (used for
     * testing) *
     */
    private boolean TEST_MODE = true;

    private ImageIcon icon = new ImageIcon("Icon.png");

    public LibraryModel(JFrame parent, String userid, String password) {
        dialogParent = parent;
        this.userPassword = password;
        this.userID = userid;
        this.myURL = "jdbc:postgresql://localhost:5432/CarRentalsDb";

        try {
            con = DriverManager.getConnection(myURL, this.userID, this.userPassword);
            if (TEST_MODE == true) {
                con.setAutoCommit(false);
            }
        } catch (SQLException e) {

            System.out.println("Error connecting to the database.");
            JOptionPane.showMessageDialog(dialogParent, e.toString(), "An Error Occured", JOptionPane.ERROR_MESSAGE, this.icon);
            closeDBConnection();
            System.exit(-1);
        }
        System.out.println("Connection successful");
    }

    public String carLookup(int plate_number) {

        if (plate_number < 0) {
            return "Invalid PlateNumber number. The PlateNumber number can not be less than 0.";
        }

        //***  Make sure to use NATURAL JOIN not Join, otherwise it won't work. ***///
        String Query1 = "SELECT plate_number, TITLE, Edition_No AS Edition, NumOfCop, NumLeft " + "FROM car " + "WHERE plate_number =" + plate_number + ";";
        boolean plate_number_Is_Valid = false; //check if real plate_number num
        String output = "";
        String brandInfo = "";
        boolean multipleBrands = false;
        try {

            //Execute the first Query (Query1) and find the first result set.
            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery(Query1);

            //Loop through all the rows in the result set for car, if there is a row that is returned then the PlateNumber is valid.
            while (rs1.next()) {
                //Car Title
                output = ("Car Lookup:\n" + "\t" + rs1.getInt("plate_number") + ": " + rs1.getString("title") + "\n"
                        + "\tEdition: " + rs1.getInt("Edition") + " - Number of copies: " + rs1.getInt("NumOfCop") + " - Copies Left: "
                        + rs1.getInt("NumLeft") + "\n");

                //Since rows has been returned, the plate_number is a valid plate_number in the library.
                plate_number_Is_Valid = true;
            }

            //Create the second Query for brand info
            String Query2 = "SELECT surname FROM car NATURAL JOIN car_brand NATURAL JOIN brand "
                    + "WHERE plate_number =" + plate_number + ";";

            //Using the first result set, determine the other information that is needed to be displayed.
            Statement s2 = con.createStatement();
            ResultSet rs2 = s2.executeQuery(Query2);

            int amount_of_brands = 0;
            while (rs2.next()) {
                String brand_name = rs2.getString("surname").trim();
                if (amount_of_brands >= 1) {
                    multipleBrands = true;
                    brandInfo += ",";
                    brandInfo += " ";
                }
                brandInfo += (brand_name);
                amount_of_brands++;
            }

            //	If PlateNumber is not valid, the PlateNumber is not in the library.
            //	Print everything from the output.
            if (plate_number_Is_Valid == false) {
                output = ("\tNo such PlateNumber: " + plate_number);
            } else {
                if (amount_of_brands == 0) {
                    output += ("\t(No Brands)");
                } else {
                    output += "\tBrand";

                    if (multipleBrands == true) {
                        output += ("s");
                    }
                    output += (": " + brandInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String showCatalogue() {

        String brandsInfo = "";
        String output = "";
        try {
            output = ("Show Catalogue\n\n");

            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery("SELECT plate_number, title, edition_no AS Edition, numofcop, numleft FROM car ORDER BY plate_number;");

            while (rs1.next()) {
                output += ("\n\t" + rs1.getInt("plate_number")
                        + ": " + rs1.getString("title") + "\n\t\tEdition: " + rs1.getInt("Edition")
                        + " - Number of copies: " + rs1.getInt("numofcop")
                        + " - Copies left: " + rs1.getInt("numleft") + "\n");

                Statement s2 = con.createStatement();
                ResultSet rs2 = s2.executeQuery("SELECT plate_number, surname FROM car NATURAL JOIN car_brand NATURAL JOIN brand WHERE plate_number = " + rs1.getInt("plate_number") + ";");

                int count = 0;
                while (rs2.next()) {
                    if (count > 0) {
                        brandsInfo += (", ");
                    }
                    brandsInfo += (rs2.getString(2).trim());
                    count++;
                }

                if (count > 0) {
                    output += ("\t\tBrand");

                    if (count > 0) {
                        output += ("s");
                    }

                    output += ": " + brandsInfo;
                } else {
                    output += ("\t\t(No Brands)");
                }

                count = 0;
                brandsInfo = "";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String showLoanedCars() {

        int counta = 0;
        int brandCount = 0;
        ResultSet rs1;
        ResultSet rs2;
        ResultSet rs3;
        boolean results = false;

        String output = "";
        String brandInfo = "";
        String borrowerInfo = "";

        try {

            output = "Show Loaned Cars:\n";
            brandInfo = "";
            borrowerInfo = "";

            Statement s1 = con.createStatement();
            rs1 = s1.executeQuery("select * from car NATURAL JOIN cust_car ORDER BY plate_number asc;");

            while (rs1.next()) {

                int plate_number = rs1.getInt("plate_number");
                output += "\n\t" + plate_number
                        + ": " + rs1.getString("title") + "\n" + "\t\t" + "Edition:" + " " + rs1.getInt("edition_no") + " - " + "Number of copies: " + rs1.getInt("numofcop") + " - " + "Copies left: " + rs1.getInt("numleft") + "\n";
                Statement s2 = con.createStatement();
                rs2 = s2.executeQuery("SELECT surname, plate_number, title FROM car NATURAL JOIN car_brand NATURAL JOIN brand WHERE plate_number = " + plate_number + ";");

                while (rs2.next()) {
                    if (counta >= 1) {
                        brandInfo += ", ";
                        brandCount++;
                    }
                    brandInfo += (rs2.getString("surname").trim());
                    counta++;
                }

                if (counta >= 1) {
                    output += "\t\tBrand";

                    if (brandCount >= 1) {
                        output += "s";
                    }

                    output += ": " + brandInfo;
                } else {
                    output += "\t\tno brands";
                }

                output += "\n";
                counta = 0;
                brandCount = 0;
                brandInfo = "";
                Statement s3 = con.createStatement();
                rs3 = s3.executeQuery("select customerid, l_name, f_name, city, plate_number FROM customer NATURAL JOIN cust_car WHERE plate_number = " + plate_number + ";");

                output += "\tBorrowers:\n";
                while (rs3.next()) {
                    borrowerInfo += "\t\t" + rs3.getInt(1) + ": " + rs3.getString(2).trim() + ", " + rs3.getString(3).trim() + " - ";

                    String city = "no city";
                    if (rs3.getString(4) != null) {
                        city = rs3.getString(4).trim();
                    }
                    borrowerInfo += city + "\n";
                }
                output += borrowerInfo;
                borrowerInfo = "";
                results = true;
            }
            if (results == false) {
                output += ("\tno Loaned Cars");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return output;
    }

    public String showBrand(int brandID) {

        if (brandID < 0) {
            return "Invalid brandID. The brandID can not be less than 0.";
        }

        String Query1 = "SELECT brandid, name, surname, plate_number, title FROM car NATURAL JOIN car_brand NATURAL JOIN brand WHERE brandid = " + brandID + ";";
        String carInfo = "";
        String output = "";
        boolean resultExists = false;
        boolean multipleCars = false;

        try {

            output = "Show Brand:\n";
            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery(Query1);

            while (rs1.next()) {
                if (resultExists == false) {
                    output += ("\t" + rs1.getInt(1)
                            + " - " + rs1.getString("name").trim() + " " + rs1.getString("surname").trim() + "\n");

                    resultExists = true;
                } else {
                    multipleCars = true;
                    carInfo += ("\n");
                }
                carInfo += ("\t\t" + rs1.getInt("plate_number") + " - " + rs1.getString("title").trim());
            }
            if (resultExists == false) {
                output += "\tThe selected brand ID of " + brandID + "does not exist.";
            } else {
                output += ("\tCar");

                if (multipleCars == true) {
                    output += ("s");
                }
                output += " written:\n" + carInfo;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return output;
    }

    public String showAllBrands() {

        String output = "";
        String Query1 = "SELECT brandid, surname, name "
                + "FROM brand "
                + "ORDER BY brandid;";

        try {

            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery(Query1);
            output = "Show All Brands:\n";

            while (rs1.next()) {

                output += "\t" + rs1.getInt("brandid") + ": " + rs1.getString("surname").trim()
                        + ", " + rs1.getString("name").trim() + "\n";
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return output;
    }

    public void addCustomer() {
        dialogParent.setVisible(false);
        new NewCustomer(dialogParent, con).setVisible(true);
    }

    public String showCustomer(int customerID) {

        if (customerID < 0) {
            return "Invalid Customer ID";
        }

        String Query1 = "select * from customer where customerID = " + customerID + ";";
        String Query2 = "select cb.plate_number, title from cust_car cb natural join car where customerid = " + customerID + ";";
        String output = "";
        String carDetails = "";
        String city = "";

        try {

            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery(Query1);

            while (rs1.next()) {
                output = "Show Customer:\n";
                output += "\t" + rs1.getInt("customerID") + ": " + (rs1.getString("L_Name").trim() + ", " + rs1.getString("F_Name").trim())
                        + " - ";
                //https://stackoverflow.com/questions/5991360/handling-the-null-value-from-a-resultset-in-java
                city = rs1.getString("city");
                if (rs1.wasNull()) {
                    city = "No city avaliable";
                }
                output += city + "\n" + "\tBorrowed Cars:\n";
            }

            if (output.equals("")) {
                return "This customerID does not exist in the database yet.";
            }

            Statement s2 = con.createStatement();
            ResultSet rs2 = s2.executeQuery(Query2);

            while (rs2.next()) {
                carDetails = rs2.getInt("plate_number") + " - " + rs2.getString("title") + "\n";

            }
            if (carDetails.equals("")) {
                carDetails = "No Borrowed Cars";
            }
            output += "\t\t" + carDetails.trim();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String showAllCustomers() {
        String Query1 = "select * from customer;";
        String output = "Show All Customers:\n";

        try {
            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery(Query1);
            while (rs1.next()) {
                output += "\t" + rs1.getInt("customerID") + ": " + rs1.getString("f_name").trim()
                        + ", " + rs1.getString("l_name").trim() + "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String borrowCar(int plate_number, int customerID, int day, int month, int year) {

        if (customerID < 0) {
            return "Invalid Customer ID. CustomerID can not be less than 0.";
        }
        if (plate_number < 0) {
            return "Invalid PlateNumber number. The PlateNumber number can not be less than 0.";
        }
        if (day > 31 || day < 0 || month > 12 || month < 0 || year < 0) {
            return "Invalid Date";
        }

        boolean plate_number_is_valid = false;
        boolean validCustomer = false;
        String output = "";
        String carName = "";

        try {
            Statement s1 = con.createStatement();

            //Query for specific car (by PlateNumber) returns details about that car eg. how many are left, title etc.
            ResultSet rs1 = s1.executeQuery("SELECT plate_number, numleft, title "
                    + "FROM car WHERE plate_number = " + plate_number + ";");

            int carsLeft = 0;

            while (rs1.next()) {

                carsLeft = rs1.getInt("numLeft");

                if (carsLeft < 1) {

                    //	If there is no cars you cant borrow a car.
                    return "There is not enough copies of the car" + rs1.getInt("plate_number") + ": " + rs1.getString("Title");
                }

                carName = rs1.getString("title").trim();
                plate_number_is_valid = true; // turns true because it has at least one result that has been returned.
            }

            if (plate_number_is_valid == false) {
                return ("\tNo such PlateNumber: " + plate_number);
            }

            String Query2 = "SELECT customerid, f_name, l_name FROM customer WHERE customerid = " + customerID + ";";
            Statement s2 = con.createStatement();
            ResultSet rs2 = s2.executeQuery(Query2);
            String customerName = "";

            while (rs2.next()) {
                customerName = rs2.getString("f_name").trim() + " " + rs2.getString("l_name").trim();
                validCustomer = true;
            }

            if (validCustomer == false) {
                return ("\tNo such customer ID: " + customerID);
            }

            output += "\tCar: " + plate_number + " " + carName + "\n"
                    + "\tLoaned to: " + customerID + " (" + customerName + ")\n"
                    + "\tDue Date: " + day + " " + getMonthAsString(month) + year;

            String date = year + "-" + month + "-" + day;

            PreparedStatement s3 = con.prepareStatement("INSERT INTO cust_car "
                    + "VALUES (" + plate_number + ", date'" + date + "', " + customerID + ");");
            PreparedStatement s4 = con.prepareStatement("UPDATE car "
                    + "SET numleft = " + (carsLeft - 1)
                    + " WHERE plate_number = " + plate_number + ";");

            //https://stackoverflow.com/questions/24970176/joptionpane-handling-ok-cancel-and-x-button
            int a = JOptionPane.showOptionDialog(new JFrame(), "Confirm Borrow:\n\t" + customerName + " (CustomerID: " + customerID + ")\n\tPress YES to continue. Press NO to cancel.",
                    "CONFIRM BORROW", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_NO_OPTION);

            if (a == JOptionPane.YES_OPTION) {
                s3.executeUpdate();
                s4.executeUpdate();
                return "Success.\n" + output;

            } else if (a == JOptionPane.NO_OPTION) {
                return ("Borrow has been cancelled.");
            } else if (a == JOptionPane.CLOSED_OPTION) {
                return ("Window has closed\n\tBorrow has been cancelled.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting customer";
        }
        return "error";
    }

    public String returnCar(int plate_number, int customerid) {
        if (customerid < 0) {
            return "Invalid Customer ID. CustomerID can not be less than 0.";
        }
        if (plate_number < 0) {
            return "Invalid PlateNumber number. The PlateNumber number can not be less than 0.";
        }

        int countPlateNumber = 0;
        int customerCount = 0;
        int loanCount = 0;

        PreparedStatement s4;
        PreparedStatement s5;
        ResultSet rs1;
        ResultSet rs2;
        ResultSet rs3;

        String output = "";

        try {

            output = "Return Car:\n";
            Statement s1 = con.createStatement();
            rs1 = s1.executeQuery("SELECT plate_number, numleft, title "
                    + "FROM car "
                    + "WHERE plate_number = " + plate_number + ";");

            String carName = "";
            int carsLeft = 0;
            while (rs1.next()) {
                carsLeft = rs1.getInt("numleft");
                carName = rs1.getString("title");
                countPlateNumber++;
            }

            if (countPlateNumber == 0) {
                output += "\tNo such PlateNumber: " + plate_number;
                return output;
            }

            Statement s2 = con.createStatement();
            rs2 = s2.executeQuery("SELECT customerid, f_name, l_name "
                    + "FROM customer "
                    + "WHERE customerid = " + customerid + ";");

            String custFullName = "";
            while (rs2.next()) {
                custFullName = rs2.getString("f_name").trim() + " " + rs2.getString("l_name").trim();
                customerCount++;
            }
            if (customerCount == 0) {
                output += "\tNo such customer ID: " + customerid;
                return output;
            }
            Statement s3 = con.createStatement();
            rs3 = s3.executeQuery("SELECT plate_number, customerid "
                    + "FROM cust_car "
                    + "WHERE plate_number = " + plate_number + " AND customerID = " + customerid + ";");

            while (rs3.next()) {
                loanCount++;
            }

            if (loanCount == 0) {
                output += "\tCar " + plate_number + " is not loaned to customer " + customerid;
                return output;
            }

            s4 = con.prepareStatement("DELETE FROM cust_car WHERE plate_number = " + plate_number + " and customerid = " + customerid + ";");
            s5 = con.prepareStatement("UPDATE car SET numleft = " + (carsLeft + 1) + "WHERE plate_number = " + plate_number + ";");

            int answer = JOptionPane.showOptionDialog(new JFrame(), "Confirm Return:\n\t" + custFullName.trim() + " returning car " + plate_number + " :" + carName + "\nPress YES to continue. Press NO to cancel.",
                    "CONFIRM RETURN", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, this.icon, new Object[]{"Yes", "No"}, JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.YES_OPTION) {
                s4.executeUpdate();
                s5.executeUpdate();
                return (output + "\nSuccess. Customer " + customerid + ": " + custFullName + " has returned the car: " + plate_number + " :" + carName);
            } else if (answer == JOptionPane.NO_OPTION) {
                return (output + "\nCancelled returning the car " + plate_number + " for customer " + customerid + ": " + custFullName);
            } else if (answer == JOptionPane.CLOSED_OPTION) {
                return (output + "\nWindow was closed\n\t" + "Cancelled returning the car " + plate_number + " for customer " + customerid + ": " + custFullName);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }

        return output;
    }

    private String getMonthAsString(int month) {
        String monthInString = "";

        switch (month) {
            case 0:
                monthInString = "January" + " ";
                break;
            case 1:
                monthInString = "Febuary" + " ";
                break;
            case 2:
                monthInString = "March" + " ";
                break;
            case 3:
                monthInString = "April" + " ";
                break;
            case 4:
                monthInString = "May" + " ";
                break;
            case 5:
                monthInString = "June" + " ";
                break;
            case 6:
                monthInString = "July" + " ";
                break;
            case 7:
                monthInString = "August" + " ";
                break;
            case 8:
                monthInString = "September" + " ";
                break;
            case 9:
                monthInString = "October" + " ";
                break;
            case 10:
                monthInString = "November" + " ";
                break;
            case 11:
                monthInString = "December" + " ";
                break;
            default:
                monthInString = "Invalid month";
                break;
        }
        return monthInString;
    }

    public void closeDBConnection() {
        try {
            this.con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String deleteCus(int customerID) {

        if (customerID < 0) {
            return "Invalid customerID. The customerID can not be less than 0.";
        }

        String Query1 = "select * from Customer C WHERE C.CustomerId = " + customerID + ";";
        String Query2 = "DELETE FROM Customer WHERE CustomerId = " + customerID + ";";
        PreparedStatement s2;
        try {
            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery(Query1);
            if (rs1.next()) {
                s2 = con.prepareStatement(Query2);
            } else {
                return "There is no customer with that ID.";
            }
            String customerName = rs1.getString("f_name").trim() + " " + rs1.getString("l_name").trim();

            int a = JOptionPane.showOptionDialog(new JFrame(), "Confirm Delete:\n\t" + customerName + " (CustomerID: " + customerID + ")\nPress YES to continue. Press NO to cancel.",
                    "CONFIRM DELETION", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_NO_OPTION);

            if (a == JOptionPane.YES_OPTION) {
                s2.executeUpdate();
                return ("Success. The Customer " + customerName + " with a customerID of: " + customerID + " has been deleted.");
            } else if (a == JOptionPane.NO_OPTION) {
                return ("Deletion of customer " + customerName + " with a customerID of: " + customerID + " has been cancelled.");
            } else if (a == JOptionPane.CLOSED_OPTION) {
                return ("Window was closed\n\tDeletion of customer " + customerName + " with a customerID of: " + customerID + " has been cancelled.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting customer";
        }
        return "Error has occurred";
    }

    public String deleteBrand(int brandID) {

        if (brandID < 0) {
            return "Invalid brandID. The brandID can not be less than 0.";
        }

        String Query1 = "select * from Brand A WHERE A.brandID = " + brandID + ";";
        String Query2 = "delete from Brand A WHERE A.brandID = " + brandID + ";";
        PreparedStatement s2;

        try {
            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery(Query1);
            if (rs1.next()) {
                s2 = con.prepareStatement(Query2);
            } else {
                return "There is no brand with that ID.";
            }

            String brandFullName = rs1.getString("name").trim() + " " + rs1.getString("surname").trim();

            int a = JOptionPane.showOptionDialog(new JFrame(), "Confirm Delete:\n\t" + brandFullName.trim() + " (BrandID: " + brandID + ")\nPress YES to continue. Press NO to cancel.",
                    "CONFIRM DELETION", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, this.icon, new Object[]{"Yes", "No"}, JOptionPane.YES_NO_OPTION);

            if (a == JOptionPane.YES_OPTION) {
                s2.executeUpdate();
                return ("Success. The brand " + brandFullName + " with a brandID of: " + brandID + " has been deleted.");
            } else if (a == JOptionPane.NO_OPTION) {
                return ("Cancelled the deletion of customer " + brandFullName + " with a brandID of: " + brandID);
            } else if (a == JOptionPane.CLOSED_OPTION) {
                return ("Window was closed\n\tCancelled the deletion of brand " + brandFullName + " with a brandID of: " + brandID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting customer";
        }
        return "Error has occurred";
    }

    public String deleteCar(int plate_number) {
        if (plate_number < 0) {
            return "Invalid PlateNumber number. The PlateNumber number can not be less than 0.";
        }

        String Query1 = "select * from car B WHERE B.plate_number = " + plate_number + ";";
        String Query2 = "delete from Car B where B.plate_number = " + plate_number + ";";

        PreparedStatement s2;
        try {
            Statement s1 = con.createStatement();
            ResultSet rs1 = s1.executeQuery(Query1);
            if (rs1.next()) {
                s2 = con.prepareStatement(Query2);
            } else {
                return "There is no car with that PlateNumber.";
            }
            String carNameAndPlateNumber = rs1.getString("plate_number").trim() + ": " + rs1.getString("title").trim();

            int a = JOptionPane.showOptionDialog(new JFrame(), "Confirm Delete:\n\t" + carNameAndPlateNumber + "\n Press YES to continue. Press NO to cancel.",
                    "CONFIRM DELETION", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, JOptionPane.YES_NO_OPTION);

            if (a == JOptionPane.YES_OPTION) {
                s2.executeUpdate();
                return ("Success. The car " + carNameAndPlateNumber + "has been deleted.");
            } else if (a == JOptionPane.NO_OPTION) {
                return ("Cancelled the deletion of " + carNameAndPlateNumber + ".");
            } else if (a == JOptionPane.CLOSED_OPTION) {
                return ("Window was closed\n\t" + "Cancelled the deletion of " + carNameAndPlateNumber + ".");
            }
        } catch (SQLException e) {
            return "Error deleting car, could be because car is loaned to a customer.";
        }
        return "Error has occurred";
    }
}
