package com.example.stockmarketshowdown.database

import android.os.StrictMode
import android.util.Log
import com.example.stockmarketshowdown.model.LeaderboardEntry
import com.example.stockmarketshowdown.model.PortfolioEntry
import com.example.stockmarketshowdown.model.TransactionHistory
import com.example.stockmarketshowdown.model.UserProfile
import com.example.stockmarketshowdown.ui.history.SortColumn
import com.example.stockmarketshowdown.ui.history.SortInfo
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

private const val JDBC_URL = "jdbc:mysql://stockmarketshowdown.cz2uyc2wg0ss.us-east-2.rds.amazonaws.com:3306/SMS"
private const val USER = "SMS"
private const val PASSWORD = "gr3atViolet66" //Plain text :)
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
            val sql = "INSERT INTO Users (UserID, DisplayName, Email, Biography, Tagline, Cash, Picture) VALUES (?, ?, ?, ?, ?, ?, ?)"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, userID)
            preparedStatement.setString(2, displayName)
            preparedStatement.setString(3, email)
            preparedStatement.setString(4, biography)
            preparedStatement.setString(5, tagline)
            preparedStatement.setBigDecimal(6, cash)
            preparedStatement.setString(7, "")

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
                    "",
                    user.email,
                    "",
                    "",
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
            val totalValue = BigDecimal.ZERO // Initialize totalValue to zero
            assets.add(Asset(name, quantity, cost, totalValue))
        }

        resultSet.close()
        preparedStatement.close()
        connection.close()

        assets
    }

    suspend fun getTop10Scores(): List<LeaderboardEntry> = withContext(Dispatchers.IO) {
        val connection = getConnection()
        val query = """
        SELECT Score.ScoreID, Score.Score, Users.DisplayName, Users.Tagline, Users.UserID
        FROM Score
        INNER JOIN Users ON Score.UserID = Users.UserID
        ORDER BY Score.Score DESC
    """.trimIndent()

        val preparedStatement = connection.prepareStatement(query)
        val resultSet = preparedStatement.executeQuery()


        val leaderboardEntries = mutableListOf<LeaderboardEntry>()
        while (resultSet.next()) {
            val id = resultSet.getInt("Score.ScoreID")
            val userId = resultSet.getString("Users.UserID")
            val name = resultSet.getString("Users.DisplayName")
            val score = resultSet.getInt("Score.Score")
            val tagline = resultSet.getString("Users.Tagline")
            leaderboardEntries.add(
                LeaderboardEntry(id, userId, score, name, tagline)
            )
        }

        resultSet.close()
        preparedStatement.close()
        connection.close()

        leaderboardEntries
    }

    suspend fun getUser(userID: String) : UserProfile = withContext(Dispatchers.IO) {
        val connection = getConnection()
        val query = """
        SELECT UserID, DisplayName, Email, Biography, Tagline, Cash, Picture
        FROM Users
        WHERE UserID = ?
        """.trimIndent()

        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, userID)
        val resultSet = preparedStatement.executeQuery()


        val user = mutableListOf<UserProfile>()
        while (resultSet.next()) {
            val id = resultSet.getString("UserID")
            val name = resultSet.getString("DisplayName")
            val email = resultSet.getString("Email")
            val biography = resultSet.getString("Biography")
            val tagline = resultSet.getString("Tagline")
            val cash = resultSet.getDouble("Cash")
            val picture = resultSet.getString("Picture")
            user.add(
                UserProfile(id, name, email, biography, tagline, cash, picture)
            )
        }

        resultSet.close()
        preparedStatement.close()
        connection.close()

        user[0]
    }

    suspend fun updateUserProfile(userProfile: UserProfile) = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        Log.d("SMS: UpdateUserProfile", userProfile.toString())
        try {
            connection = getConnection()
            val sql = "UPDATE Users SET DisplayName = ?, Biography = ?, Tagline = ?, Picture = ? WHERE UserID = ?"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, userProfile.displayName)
            preparedStatement.setString(2, userProfile.biography)
            preparedStatement.setString(3, userProfile.tagline)
            preparedStatement.setString(4, userProfile.userID)
            preparedStatement.setString(5, userProfile.picture)
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }

    suspend fun getUserTransactionHistory(userID: String, sortInfo: SortInfo) : List<TransactionHistory> = withContext(Dispatchers.IO) {
        val connection = getConnection()
        var orderByColumn = "TradeCompany"
        if (sortInfo.sortColumn == SortColumn.COMPANY) {
            orderByColumn = "TradeCompany"
        } else if (sortInfo.sortColumn == SortColumn.VALUE) {
            orderByColumn = "TradeValue"
        } else if (sortInfo.sortColumn == SortColumn.TYPE) {
            orderByColumn = "TradeType"
        } else {
            orderByColumn = "TradeDate"
        }
        var sortOrder = "ASC"
        if (sortInfo.ascending) {
            sortOrder = "ASC"
        } else {
            sortOrder = "DESC"
        }
        val query = """
        SELECT UserID, TransactionID, TradeType, TradeValue, TradeDate, TradeCompany
        FROM TransactionHistory
        WHERE UserID = ?
        ORDER BY $orderByColumn $sortOrder
    """.trimIndent()

        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, userID)
        val resultSet = preparedStatement.executeQuery()


        val history = mutableListOf<TransactionHistory>()
        while (resultSet.next()) {
            val userID = resultSet.getString("UserID")
            val tranID = resultSet.getString("TransactionID")
            val type = resultSet.getString("TradeType")
            val value = resultSet.getDouble("TradeValue")
            val date = resultSet.getDate("TradeDate")
            val company = resultSet.getString("TradeCompany")
            history.add(
                TransactionHistory(userID, tranID, type, value, date, company)
            )
        }

        resultSet.close()
        preparedStatement.close()
        connection.close()

        history
    }

    suspend fun getScore(userID: String): Int? = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        var score: Int? = null
        try {
            connection = getConnection()
            val sql = "SELECT Score FROM Score WHERE UserID = ?"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, userID)
            resultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                score = resultSet.getInt("Score")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            resultSet?.close()
            preparedStatement?.close()
            connection?.close()
        }
        score
    }

    suspend fun setScore(userID: String, newScore: Int) = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = getConnection()
            val sql = "INSERT INTO Score (UserID, Score) VALUES (?, ?) ON DUPLICATE KEY UPDATE Score = ?"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, userID)
            preparedStatement.setInt(2, newScore)
            preparedStatement.setInt(3, newScore)
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }

    suspend fun updateScore(userID: String, newScore: Int) = withContext(Dispatchers.IO) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = getConnection()
            val sql = "UPDATE Score SET Score = ? WHERE UserID = ?"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, newScore)
            preparedStatement.setString(2, userID)
            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            preparedStatement?.close()
            connection?.close()
        }
    }


}