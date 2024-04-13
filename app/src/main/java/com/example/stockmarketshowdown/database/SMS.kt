package com.example.stockmarketshowdown.database

import android.os.StrictMode
import android.util.Log
import com.example.stockmarketshowdown.ui.home.Asset
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

    data class PortfolioEntry(
        val company: String,
        val totalOwnership: Int,
        val averagePrice: BigDecimal
    )

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

            // Check if a row for the user and company already exists
            val existingRowQuery = "SELECT Ownership, Cost FROM Portfolio WHERE UserID = ? AND Company = ?"
            val existingRowStatement = connection.prepareStatement(existingRowQuery)
            existingRowStatement.setString(1, userID)
            existingRowStatement.setString(2, company)
            val resultSet = existingRowStatement.executeQuery()

            if (resultSet.next()) {
                // Row exists, update the existing row
                val existingOwnership = resultSet.getInt("Ownership")
                val existingCost = resultSet.getBigDecimal("Cost")

                val totalOwnership = existingOwnership + ownership
                val totalCost = existingCost * BigDecimal(existingOwnership) + cost * BigDecimal(ownership)
                val newAverageCostPerShare = if (totalOwnership != 0) totalCost / BigDecimal(totalOwnership) else BigDecimal.ZERO

                val updateQuery = "UPDATE Portfolio SET Ownership = ?, Cost = ? WHERE UserID = ? AND Company = ?"
                val updateStatement = connection.prepareStatement(updateQuery)
                updateStatement.setInt(1, totalOwnership)
                updateStatement.setBigDecimal(2, newAverageCostPerShare)
                updateStatement.setString(3, userID)
                updateStatement.setString(4, company)
                updateStatement.executeUpdate()
                updateStatement.close()
            } else {
                // Row doesn't exist, insert a new row
                val insertQuery = "INSERT INTO Portfolio (UserID, Company, Ownership, Cost) VALUES (?, ?, ?, ?)"
                preparedStatement = connection.prepareStatement(insertQuery)
                preparedStatement.setString(1, userID)
                preparedStatement.setString(2, company)
                preparedStatement.setInt(3, ownership)
                preparedStatement.setBigDecimal(4, cost)
                preparedStatement.executeUpdate()
            }

            resultSet.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }

    suspend fun getUserPortfolio(userID: String): List<PortfolioEntry> = withContext(Dispatchers.IO) {
        val connection = getConnection()
        val query = """
        SELECT Company, SUM(Ownership) AS TotalOwnership, SUM(Cost * Ownership) / SUM(Ownership) AS AveragePrice
        FROM Portfolio
        WHERE UserID = ?
        GROUP BY Company
    """.trimIndent()

        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, userID)
        val resultSet = preparedStatement.executeQuery()

        val updatedPortfolio = mutableListOf<PortfolioEntry>()
        while (resultSet.next()) {
            val company = resultSet.getString("Company")
            val totalOwnership = resultSet.getInt("TotalOwnership")
            val averagePrice = resultSet.getBigDecimal("AveragePrice")
            updatedPortfolio.add(PortfolioEntry(company, totalOwnership, averagePrice))
        }

        resultSet.close()
        preparedStatement.close()

        // Clear existing portfolio entries for the user
        val clearQuery = "DELETE FROM Portfolio WHERE UserID = ?"
        val clearStatement = connection.prepareStatement(clearQuery)
        clearStatement.setString(1, userID)
        clearStatement.executeUpdate()
        clearStatement.close()

        // Insert updated portfolio entries
        val insertQuery = "INSERT INTO Portfolio (UserID, Company, Ownership, Cost) VALUES (?, ?, ?, ?)"
        val insertStatement = connection.prepareStatement(insertQuery)
        updatedPortfolio.forEach { entry ->
            insertStatement.setString(1, userID)
            insertStatement.setString(2, entry.company)
            insertStatement.setInt(3, entry.totalOwnership)
            insertStatement.setBigDecimal(4, entry.averagePrice)
            insertStatement.addBatch()
        }
        insertStatement.executeBatch()
        insertStatement.close()

        connection.close()

        updatedPortfolio
    }
    suspend fun updatePortfolio(userID: String, company: String, quantityChange: Int) = withContext(Dispatchers.IO) {
        val connection = getConnection()

        // Update the ownership for the specified company
        val updateQuery = "UPDATE Portfolio SET Ownership = Ownership + ? WHERE UserID = ? AND Company = ?"
        val updateStatement = connection.prepareStatement(updateQuery)
        updateStatement.setInt(1, quantityChange)
        updateStatement.setString(2, userID)
        updateStatement.setString(3, company)
        val rowsAffected = updateStatement.executeUpdate()
        updateStatement.close()

        // If ownership becomes 0, delete the row
        if (rowsAffected > 0) {
            val deleteQuery = "DELETE FROM Portfolio WHERE UserID = ? AND Company = ? AND Ownership = 0"
            val deleteStatement = connection.prepareStatement(deleteQuery)
            deleteStatement.setString(1, userID)
            deleteStatement.setString(2, company)
            deleteStatement.executeUpdate()
            deleteStatement.close()
        }

        connection.close()
    }

    suspend fun getUserAssets(userID: String): List<Asset> = withContext(Dispatchers.IO) {
        val connection = getConnection()
        val query = """
        SELECT Company, Ownership, Cost
        FROM Portfolio
        WHERE UserID = ?
    """.trimIndent()

        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, userID)
        val resultSet = preparedStatement.executeQuery()

        val assets = mutableListOf<Asset>()
        while (resultSet.next()) {
            val name = resultSet.getString("Company")
            val quantity = resultSet.getInt("Ownership")
            val cost = resultSet.getBigDecimal("Cost")
            assets.add(Asset(name, quantity, cost))
        }

        resultSet.close()
        preparedStatement.close()
        connection.close()

        assets
    }

}