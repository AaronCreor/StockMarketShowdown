package com.example.stockmarketshowdown.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.stockmarketshowdown.MainActivity
import com.example.stockmarketshowdown.R

class ImageUploadFragment : DialogFragment() {
    object EditDialogContract {
        const val REQUEST_KEY  = "edit_dialog_request"
        const val RESPONSE_KEY = "edit_dialog_response"
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_dialog, null)
        val editText = view.findViewById<EditText>(R.id.editText)
        val mainActivity = (requireActivity() as MainActivity)

        builder.setView(view)
            .setTitle("Edit URL")
            .setPositiveButton("OK") { _, _ ->
                val url = editText.text.toString()
                setFragmentResult(EditDialogContract.REQUEST_KEY, bundleOf(EditDialogContract.RESPONSE_KEY to url))
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }

    companion object {
        const val TAG = "ImageUploadFragment"
    }
}