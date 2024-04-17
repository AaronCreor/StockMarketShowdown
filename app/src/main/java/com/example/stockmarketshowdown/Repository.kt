import android.content.Context
import com.google.gson.Gson
import java.io.File
data class Company(
    val currency: String,
    val description: String,
    val displaySymbol: String,
    val figi: String?,
    val isin: String?,
    val mic: String,
    val shareClassFIGI: String,
    val symbol: String,
    val symbol2: String,
    val type: String)

class Repository {
    fun loadCompaniesFromAssets(context: Context): List<Company> {
        val gson = Gson()
        val inputStream = context.assets.open("companyData.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val allCompanies = gson.fromJson(jsonString, Array<Company>::class.java).toList()

        // Filter out companies with "." in their displaySymbol
        return allCompanies.filter { !it.displaySymbol.contains(".") }
    }
}