import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object RealTimeDB {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userTransactionRef: DatabaseReference = database.getReference("users")

    fun insertTransaction(userID: String, quantity: Int, ticker: String, action: String) {
        val userRef = userTransactionRef.child(userID)
        val transactionRef = userRef.child("transaction_history").push()
        val transactionData = hashMapOf(
            "userID" to userID,
            "quantity" to quantity,
            "ticker" to ticker,
            "action" to action
        )
        transactionRef.setValue(transactionData)
            .addOnSuccessListener {
                Log.d("FBDB", "Success")
            }
            .addOnFailureListener { e ->
                Log.d("FBDB", "Failure")
            }
    }

}