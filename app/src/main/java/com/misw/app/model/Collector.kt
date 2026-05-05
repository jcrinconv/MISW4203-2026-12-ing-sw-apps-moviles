package com.misw.app.model

data class Collector(
    val id: Int,
    val name: String,
    val telephone: String,
    val email: String,
    val comments: List<Comment>,
    val favoritePerformers: List<FavoritePerformer>,
    val collectorAlbums: List<CollectorAlbum>
)
