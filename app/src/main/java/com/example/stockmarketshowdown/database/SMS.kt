package com.example.stockmarketshowdown.database

import android.os.StrictMode
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.Date

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

    suspend fun getUserCash(userID: String): BigDecimal? = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        var cash: BigDecimal? = null
        try {
            connection = getConnection()
            val sql = "SELECT Cash FROM Users WHERE UserID = ?"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, userID)
            resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                cash = resultSet.getBigDecimal("Cash")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            resultSet?.close()
            preparedStatement?.close()
            connection?.close()
        }
        cash
    }

    suspend fun updateCash(userID: String, newCash: BigDecimal) = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = getConnection()
            val sql = "UPDATE Users SET Cash = ? WHERE UserID = ?"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setBigDecimal(1, newCash)
            preparedStatement.setString(2, userID)
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }

    suspend fun insertTransaction(userID: String, tradeType: String, tradeValue: BigDecimal, tradeCompany: String) {
        withContext(Dispatchers.IO) {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null
            try {
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                connection = getConnection()
                val sql = "INSERT INTO TransactionHistory (UserID, TradeType, TradeValue, TradeDate, TradeCompany) VALUES (?, ?, ?, SYSDATE(), ?)"
                preparedStatement = connection.prepareStatement(sql)
                preparedStatement.setString(1, userID)
                preparedStatement.setString(2, tradeType)
                preparedStatement.setBigDecimal(3, tradeValue)
                preparedStatement.setString(4, tradeCompany)
                preparedStatement.executeUpdate()
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }
    }

    suspend fun insertPortfolio(userID: String, company: String, ownership: Int, cost: BigDecimal) = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = getConnection()
            val sql = "INSERT INTO Portfolio (UserID, Company, Ownership, Cost) VALUES (?, ?, ?, ?)"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, userID)
            preparedStatement.setString(2, company)
            preparedStatement.setInt(3, ownership)
            preparedStatement.setBigDecimal(4, cost)
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }
}