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
    // This function fetches the list of publicly traded US companies from the specified API
    private var companyResources = listOf(
        Company  (
                "USD",
                "REPUBLIC SERVICES INC",
                "RSG",
                "BBG000BPXVJ6",
                null,
                "XNYS",
                "BBG001S9DL33",
                "RSG",
                "",
                "Common Stock"
        )
    )
}