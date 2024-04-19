package com.example.stockmarketshowdown.ui.globe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GlobalViewFragment : Fragment() {

    private lateinit var adapter: RVGlobalAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_global, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RVGlobalAdapter(mutableListOf())
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().reference
            .child("users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<RVGlobalAdapter.GlobalData>()
                for (userSnapshot in snapshot.children) {
                    for (transactionSnapshot in userSnapshot.child("transaction_history").children) {
                        val ticker = transactionSnapshot.child("ticker").getValue(String::class.java) ?: ""
                        val quantity = transactionSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                        val action = transactionSnapshot.child("action").getValue(String::class.java) ?: ""
                        val timestamp = transactionSnapshot.child("timestamp").getValue(Long::class.java) ?: 0
                        dataList.add(RVGlobalAdapter.GlobalData(ticker, quantity, action, timestamp))
                    }
                }
                adapter.updateData(dataList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}