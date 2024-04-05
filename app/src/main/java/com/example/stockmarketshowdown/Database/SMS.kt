package com.example.stockmarketshowdown.Database;

import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

private const val JDBC_URL = "jdbc:mysql://stockmarketshowdown.cz2uyc2wg0ss.us-east-2.rds.amazonaws.com:3306/SMS"
private const val USER = "SMS"
private const val PASSWORD = "gr3atViolet66"
public class SMS {
    // Method to establish a database connection
    fun getConnection(): Connection {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD)
    }

    fun insertUser(userID: String, displayName: String?, email: String?, biography: String?, tagline: String?, cash: BigDecimal?) {
        var connection: Connection? = null
        try {
            connection = getConnection()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

}
