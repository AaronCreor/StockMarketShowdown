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
class RVDiffAdapter(
    private val viewModel: MainViewModel,
    private val onItemClick: (position: Int) -> Unit
)
    : ListAdapter<Company,
        RVDiffAdapter.ViewHolder>(Diff())
{
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
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
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

