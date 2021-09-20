package android.mohamed.pexelapiproject.dataModels

data class PhotoResponse(
    val next_page: String,
    val page: Int,
    val per_page: Int,
    val photos: MutableList<Photo>,
    val total_results: Int
)