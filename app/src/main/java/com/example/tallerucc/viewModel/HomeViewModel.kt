package com.example.tallerucc.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _posts = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val posts: StateFlow<List<Map<String, Any>>> = _posts

    private val _communities = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val communities: StateFlow<List<Map<String, Any>>> = _communities

    private val _likedPosts = MutableStateFlow<List<String>>(emptyList())
    val likedPosts: StateFlow<List<String>> = _likedPosts

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                viewModelScope.launch {
                    loadLikedPosts()
                    loadPostsForFeed()
                    loadCommunities()
                }
            }
        }
    }

    // Cargar comunidades
    fun loadCommunities() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("communities").get().await()
                val communityList = snapshot.documents.mapNotNull { document ->
                    document.data?.toMutableMap()?.apply {
                        this["id"] = document.id
                    }
                }
                _communities.value = communityList
            } catch (e: Exception) {
                println("Error loading communities: ${e.message}")
            }
        }
    }

    // Verificar si una comunidad es oficial
    fun isCommunityOfficial(communityName: String): Boolean {
        val community = _communities.value.find { it["name"] == communityName }
        return community?.get("isOfficial") as? Boolean ?: false
    }

    fun loadPostsForFeed() {
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(500) // Retraso para esperar la inicialización
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId.isNullOrEmpty()) {
                    println("Error: userId is null or empty.")
                    return@launch
                }

                val userSnapshot = db.collection("users").document(userId ?: "").get().await()
                val followedCommunities = userSnapshot.get("followedCommunities") as? List<String> ?: emptyList()
                val createdCommunities = userSnapshot.get("communitiesCreated") as? List<String> ?: emptyList()

                // Obtener todas las comunidades
                val communitiesSnapshot = db.collection("communities").get().await()
                val communities = communitiesSnapshot.documents.associateBy { it.id }
                val officialCommunities = communities.filterValues { it.getBoolean("isOfficial") == true }

                // Filtrar las 5 comunidades con más miembros
                val topCommunities = communities.values
                    .sortedByDescending { it.get("members")?.let { members -> (members as? List<*>)?.size } ?: 0 }
                    .take(5)
                    .mapNotNull { it.getString("name") }

                // Obtener todos los posts
                val postsSnapshot = db.collection("posts").get().await()
                val allPosts = postsSnapshot.documents.mapNotNull { document ->
                    document.data?.toMutableMap()?.apply {
                        this["postId"] = document.id // Agregar ID como campo
                    }
                }

                // Ordenar los posts
                val sortedPosts = allPosts.sortedWith(
                    compareByDescending<Map<String, Any>> {
                        it["isOfficial"] == true // Posts oficiales
                    }.thenByDescending {
                        val communityName = it["communityName"] as? String
                        officialCommunities.containsKey(communityName) // Posts de comunidades oficiales
                    }.thenByDescending {
                        it["likesCount"] as? Long ?: 0 // Más likes
                    }.thenByDescending {
                        val communityName = it["communityName"] as? String
                        topCommunities.contains(communityName) // Más likes en las 5 comunidades con más miembros
                    }.thenByDescending {
                        val communityName = it["communityName"] as? String
                        createdCommunities.contains(communityName) // Posts de comunidades creadas por el usuario
                    }.thenByDescending {
                        val communityName = it["communityName"] as? String
                        followedCommunities.contains(communityName) // Posts de comunidades seguidas por el usuario
                    }.thenByDescending {
                        it["createdAt"] as? com.google.firebase.Timestamp // Más recientes
                    }
                )

                _posts.value = sortedPosts
            } catch (e: Exception) {
                println("Error loading posts for feed: ${e.message}")
            }
        }
    }


    fun toggleLike(postId: String, isLiked: Boolean) {
        val userId = currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)
        val postRef = db.collection("posts").document(postId)

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val likedPosts = userSnapshot.get("likedPosts") as? List<String> ?: emptyList()
            val postSnapshot = transaction.get(postRef)
            val currentLikes = postSnapshot.getLong("likesCount") ?: 0

            if (isLiked) {
                transaction.update(userRef, "likedPosts", FieldValue.arrayUnion(postId))
                transaction.update(postRef, "likesCount", currentLikes + 1)
                _likedPosts.value = _likedPosts.value + postId
            } else {
                transaction.update(userRef, "likedPosts", FieldValue.arrayRemove(postId))
                transaction.update(postRef, "likesCount", currentLikes - 1)
                _likedPosts.value = _likedPosts.value - postId
            }
        }.addOnSuccessListener {
            // Actualizar el estado de los posts localmente
            _posts.value = _posts.value.map { post ->
                if (post["postId"] == postId) {
                    post.toMutableMap().apply {
                        this["likesCount"] = (this["likesCount"] as? Long ?: 0) + if (isLiked) 1 else -1
                    }
                } else {
                    post
                }
            }
            println("Like toggled successfully for post: $postId")
        }.addOnFailureListener { e ->
            println("Error toggling like for post: $postId - ${e.message}")
        }
    }



    fun loadLikedPosts() {
        val userId = currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val likedPosts = document.get("likedPosts") as? List<String> ?: emptyList()
                _likedPosts.value = likedPosts

                // Actualizar el estado de los posts con el estado de los likes
                _posts.value = _posts.value.map { post ->
                    post.toMutableMap().apply {
                        this["isLiked"] = likedPosts.contains(this["postId"] as? String)
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error loading liked posts: ${e.message}")
            }
    }



}

