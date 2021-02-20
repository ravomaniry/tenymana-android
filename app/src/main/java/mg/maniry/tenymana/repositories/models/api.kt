package mg.maniry.tenymana.repositories.models

data class ApiJourneyListItem(
    val id: String,
    val title: String,
    val description: String,
    val key: String
)

data class ApiJourneyListResult(
    val list: List<ApiJourneyListItem>,
    val hasNext: Boolean
)
