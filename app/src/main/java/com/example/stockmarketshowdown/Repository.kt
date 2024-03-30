package musicplayer.cs371m.musicplayer

// This is the model in MVVM
data class Company(val name: String, val ticker: String)
class Repository {
    // This is long enough that it has to scroll
    // I have a limited number of song files, so I use them twice
    // Entries are distinguished with a different time value.
    // To make list manipulation work, all entries must be distinct
    private var stockResources = listOf(
        Company(
            "Apple",
            "AAPL"
        )

    )

}