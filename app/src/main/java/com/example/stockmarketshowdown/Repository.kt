import com.google.gson.Gson
import java.net.URL

data class Company(val description: String, val displaySymbol: String)

class Repository {
    // This function fetches the list of publicly traded US companies from the specified API
    private fun fetchCompanyList(): List<Company> {
        val url = URL("https://finnhub.io/api/v1/stock/symbol?exchange=US&mic=XNYS&token=co3lv89r01qj6vn86qd0co3lv89r01qj6vn86qdg") // API endpoint
        val json = url.readText()

        // Assuming the API response is a JSON array where each object contains description and displaySymbol fields
        return Gson().fromJson(json, Array<Company>::class.java).toList()
    }

    // This is where you would initialize the repository with the fetched company list
    private val stockResources: List<Company> = fetchCompanyList()

    // You may expose a method to retrieve the list of companies if needed
    fun getCompanies(): List<Company> {
        return stockResources
    }
}