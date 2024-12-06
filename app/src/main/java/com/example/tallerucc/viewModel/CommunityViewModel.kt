package com.example.tallerucc.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerucc.model.Community
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

import kotlinx.coroutines.flow.update

class CommunityViewModel : ViewModel() {
    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities

    private val _followedCommunities = MutableStateFlow<List<String>>(emptyList())
    val followedCommunities: StateFlow<List<String>> = _followedCommunities

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    init {
        loadCommunities()
        loadFollowedCommunities()
    }

    private fun loadCommunities() {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val currentUserEmail = currentUser?.email

                // Obtener comunidades desde Firestore
                val snapshot = db.collection("communities").get().await()
                val communitiesList = snapshot.documents.mapNotNull { document ->
                    val members = document.get("members") as? List<String> ?: emptyList()
                    Community(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        iconUrl = document.getString("iconUrl"),
                        bannerUrl = document.getString("bannerUrl"),
                        tags = (document["tags"] as? List<String>) ?: emptyList(),
                        postsCount = (document.get("postsCount") as? Long)?.toInt() ?: 0,
                        createdAt = (document.get("createdAt") as? com.google.firebase.Timestamp)?.seconds,
                        members = members,
                        membersCount = members.size,
                        createdBy = document.getString("createdBy"),
                        isOfficial = document.getBoolean("isOfficial") ?: false
                    )
                }

                // Ordenar las comunidades
                val sortedCommunities = communitiesList.sortedWith(
                    compareByDescending<Community> {
                        it.createdBy == currentUserEmail && it.isOfficial // Primero creadas por el usuario y oficiales
                    }.thenByDescending {
                        it.createdBy == currentUserEmail // Luego creadas por el usuario
                    }.thenByDescending {
                        _followedCommunities.value.contains(it.name) && it.isOfficial // Seguidas por el usuario y oficiales
                    }.thenByDescending {
                        _followedCommunities.value.contains(it.name) // Seguidas por el usuario
                    }.thenByDescending {
                        it.isOfficial // Comunidades oficiales restantes
                    }.thenByDescending {
                        it.membersCount // Finalmente, ordenar por número de miembros
                    }
                )

                // Actualizar el estado de las comunidades
                _communities.value = sortedCommunities
            } catch (e: Exception) {
                println("Error loading communities: ${e.message}")
            }
        }
    }


    private fun loadFollowedCommunities() {
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val followed = document.get("followedCommunities") as? List<String> ?: emptyList()
                    _followedCommunities.value = followed
                }
                .addOnFailureListener { e ->
                    println("Error loading followed communities: ${e.message}")
                }
        }
    }

    fun toggleCommunityFollow(communityName: String) {
        if (currentUser != null) {
            val userId = currentUser.uid
            val isFollowing = _followedCommunities.value.contains(communityName)

            val updateAction = if (isFollowing) {
                FieldValue.arrayRemove(communityName)
            } else {
                FieldValue.arrayUnion(communityName)
            }

            db.collection("users").document(userId)
                .update("followedCommunities", updateAction)
                .addOnSuccessListener {
                    _followedCommunities.update { followed ->
                        if (isFollowing) followed - communityName
                        else followed + communityName
                    }
                }
                .addOnFailureListener { e ->
                    println("Error updating followed communities: ${e.message}")
                }
        }
    }

    fun getCommunityDetails(communityId: String, onComplete: (Community?) -> Unit) {
        db.collection("communities").document(communityId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val members = document.get("members") as? List<String> ?: emptyList()
                    val community = Community(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        iconUrl = document.getString("iconUrl"),
                        bannerUrl = document.getString("bannerUrl"),
                        tags = (document["tags"] as? List<String>) ?: emptyList(),
                        postsCount = (document.get("postsCount") as? Long)?.toInt() ?: 0,
                        createdAt = (document.get("createdAt") as? com.google.firebase.Timestamp)?.seconds,
                        members = members,
                        membersCount = members.size,
                        createdBy = document.getString("createdBy"),
                        isOfficial = document.getBoolean("isOfficial") ?: false
                    )
                    onComplete(community)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun getCommunityPosts(communityName: String, onComplete: (List<Map<String, Any>>) -> Unit) {
        db.collection("posts").whereEqualTo("communityName", communityName).get()
            .addOnSuccessListener { querySnapshot ->
                val posts = querySnapshot.documents.map { document ->
                    document.data?.toMutableMap()?.apply {
                        this["postId"] = document.id // Agregar el ID del documento como "postId"
                    } ?: emptyMap()
                }

                // Ordenar los posts: primero por fecha (`createdAt`), luego por likes (`likesCount`)
                val sortedPosts = posts.sortedWith(
                    compareByDescending<Map<String, Any>> { it["createdAt"] as? Long ?: 0L }
                        .thenByDescending { it["likesCount"] as? Long ?: 0L }
                )

                println("Posts retrieved and sorted for community $communityName: $sortedPosts")
                onComplete(sortedPosts)
            }
            .addOnFailureListener { e ->
                println("Error retrieving posts for community $communityName: ${e.message}")
                onComplete(emptyList())
            }
    }




    fun toggleLike(postId: String, userId: String, isLiked: Boolean) {
        val userRef = db.collection("users").document(userId)
        val postRef = db.collection("posts").document(postId)

        db.runTransaction { transaction ->
            // Obtener datos actuales del usuario
            val userSnapshot = transaction.get(userRef)
            val likedPosts = userSnapshot.get("likedPosts") as? List<String> ?: emptyList()

            // Obtener datos actuales del post
            val postSnapshot = transaction.get(postRef)
            val currentLikes = postSnapshot.getLong("likesCount") ?: 0

            if (isLiked) {
                // Agregar el like
                println("Adding like to post: $postId by user: $userId")
                transaction.update(userRef, "likedPosts", FieldValue.arrayUnion(postId))
                transaction.update(postRef, "likesCount", currentLikes + 1)
            } else {
                // Quitar el like
                println("Removing like from post: $postId by user: $userId")
                transaction.update(userRef, "likedPosts", FieldValue.arrayRemove(postId))
                transaction.update(postRef, "likesCount", currentLikes - 1)
            }
        }.addOnSuccessListener {
            println("Like toggled successfully for post: $postId")
        }.addOnFailureListener { e ->
            println("Error toggling like for post: $postId - ${e.message}")
        }
    }

    fun loadLikedPosts(userId: String, onComplete: (List<String>) -> Unit) {
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { snapshot ->
                val likedPosts = snapshot.get("likedPosts") as? List<String> ?: emptyList()
                println("Loaded liked posts for user $userId: $likedPosts")
                onComplete(likedPosts)
            }
            .addOnFailureListener { e ->
                println("Error loading liked posts for user $userId: ${e.message}")
                onComplete(emptyList())
            }
    }

}

//


