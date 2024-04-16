import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmarketshowdown.MainViewModel
import com.example.stockmarketshowdown.databinding.CompanyRowBinding

class RVDiffAdapter(
    private val viewModel: MainViewModel,
    private var onItemClick: (company: Company) -> Unit
) : ListAdapter<Company, RVDiffAdapter.ViewHolder>(Diff()) {

    private var companyListFull: List<Company> = ArrayList()

    inner class ViewHolder(val companyRowBinding: CompanyRowBinding) :
        RecyclerView.ViewHolder(companyRowBinding.root) {

        fun bind(company: Company) {
            companyRowBinding.companyDescription.text = company.description
            companyRowBinding.companyTicker.text = company.displaySymbol
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = CompanyRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val company = getItem(position)
        holder.bind(company)
        holder.itemView.setOnClickListener {
            onItemClick(company) // Pass company directly
        }
    }
    // Function to filter the list based on search query
    fun filter(query: String) {
        val filteredList = mutableListOf<Company>()
        for (company in companyListFull) {
            if (company.description.contains(query, ignoreCase = true) ||
                company.displaySymbol.contains(query, ignoreCase = true)
            ) {
                filteredList.add(company)
            }
        }
        submitList(filteredList)
    }

    // Function to set the full company list
    fun setCompanyListFull(list: List<Company>) {
        companyListFull = list
        submitList(list)
    }

    fun setOnItemClickListener(listener: (company: Company) -> Unit) {
        onItemClick = listener
    }




    class Diff : DiffUtil.ItemCallback<Company>() {
        override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem.description == newItem.description &&
                    oldItem.figi == newItem.figi
        }
    }
}