package com.example.stockmarketshowdown.Database

import android.os.StrictMode
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

private const val JDBC_URL = "jdbc:mysql://stockmarketshowdown.cz2uyc2wg0ss.us-east-2.rds.amazonaws.com:3306/SMS"
private const val USER = "SMS"
private const val PASSWORD = "gr3atViolet66"
private const val JDBC_DRIVER = "com.mysql.cj.jdbc.Driver"
public class SMS {
    // Method to establish a database connection
    fun getConnection(): Connection {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD)
    }

    fun insertUser(userID: String, displayName: String?, email: String?, biography: String?, tagline: String?, cash: BigDecimal?) {//Insert new user into user table
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            connection = getConnection()
            val sql = "INSERT INTO Users (UserID, DisplayName, Email, Biography, Tagline, Cash) VALUES (?, ?, ?, ?, ?, ?)"
            preparedStatement = connection.prepareStatement(sql)
            // Set parameters
            preparedStatement.setString(1, userID)
            preparedStatement.setString(2, displayName)
            preparedStatement.setString(3, email)
            preparedStatement.setString(4, biography)
            preparedStatement.setString(5, tagline)
            preparedStatement.setBigDecimal(6, cash)
            // Execute SQL statement
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }
    fun checkUser(userID: String): Boolean { //Checks if a user exists
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            connection = getConnection()
            // Prepare SQL statement
            val sql = "SELECT UserID FROM Users WHERE UserID = ?"
            preparedStatement = connection.prepareStatement(sql)
            // Set parameter
            preparedStatement.setString(1, userID)
            // Execute query
            resultSet = preparedStatement.executeQuery()
            // Check if the user exists
            return resultSet.next() // If resultSet.next() is true, user exists; otherwise, it does not exist
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        } finally {
            // Close resources
            resultSet?.close()
            preparedStatement?.close()
            connection?.close()
        }
    }

    fun generateDBUser(){
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            if(!SMS().checkUser(user.uid)) {
                SMS().insertUser(
                    user.uid,
                    "test",
                    "test",
                    "test",
                    "test",
                    (10000.00).toBigDecimal()
                )
                Log.d("SQL", "User doesn't exist. Creating entry on SMS.Users")
            }
            else{
                Log.d("SQL", "User already exists!")
            }
        }
    }
}