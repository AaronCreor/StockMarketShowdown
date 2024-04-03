package musicplayer.cs371m.musicplayer

import Company
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.MainViewModel
import com.example.stockmarketshowdown.databinding.CompanyRowBinding

// Pass in a function called clickListener that takes a view and a songName
// as parameters.  Call clickListener when the row is clicked.
class RVDiffAdapter(private val viewModel: MainViewModel,
                    private val clickListener: (songIndex : Int)->Unit)
// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
    : ListAdapter<Company,
        RVDiffAdapter.ViewHolder>(Diff())
{
    companion object {
        val TAG = "RVDiffAdapter"
    }
    // ViewHolder pattern holds row binding
    inner class ViewHolder(val companyRowBinding: CompanyRowBinding)
        : RecyclerView.ViewHolder(companyRowBinding.root) {
        fun bind(company: Company) {
            companyRowBinding.companyDescription.text = company.description
            companyRowBinding.companyTicker.text = company.displaySymbol
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = CompanyRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val company = getItem(position)
        holder.bind(company)
    }


    class Diff : DiffUtil.ItemCallback<Company>() {
        // Item identity
        override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem.description == newItem.description
                    && oldItem.figi == newItem.figi
        }
    }
}

