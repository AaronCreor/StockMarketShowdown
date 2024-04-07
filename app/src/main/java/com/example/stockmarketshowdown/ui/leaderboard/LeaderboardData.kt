package com.example.stockmarketshowdown.ui.leaderboard

import android.text.SpannableString

data class LeaderboardEntry (
    var id: String,
    var name: SpannableString,
    var score: Double,
    var tagline: SpannableString,
)
object LeaderboardData {
    val leaderboard = listOf(
        LeaderboardEntry(
            "1",
            SpannableString("Michael Scott"),
            10000.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "2",
            SpannableString("Scott Steiner"),
            9999.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "3",
            SpannableString("King Kong"),
            9998.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "4",
            SpannableString("John Halo"),
            9997.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "5",
            SpannableString("Michael Jackson"),
            9996.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "6",
            SpannableString("Scrooge McDuck"),
            9995.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "7",
            SpannableString("Daisaku Kuze"),
            9994.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "8",
            SpannableString("Kiryu Kazama"),
            9993.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "9",
            SpannableString("David Martinez"),
            9992.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "10",
            SpannableString("Biden Blast"),
            9991.00,
            SpannableString("Insert quote here")
        ),
        LeaderboardEntry(
            "11",
            SpannableString("Jerma"),
            9990.00,
            SpannableString("Insert quote here")
        ),
    )
}