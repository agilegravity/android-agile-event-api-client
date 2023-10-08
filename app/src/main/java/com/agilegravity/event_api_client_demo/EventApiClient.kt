package com.agilegravity.event_api_client_demo
import com.squareup.moshi.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import android.util.Log
class APIClient(private val channelId: String, private val apiSecret: String) {

    private val BASE_URL = "https://dev-agile-v3-agile-bai-event.agilegravity.com"
    private var accessToken: String? = null
    private var userId: String? = null
    private var topicId: String? = null

    private val client = OkHttpClient()
    private val moshi = Moshi.Builder().build()

    private fun ensureLogin(completion: () -> Unit) {
        if (accessToken != null) {
            completion()
            return
        }

        loginAPICall { token, error ->
            if (error != null || token == null) {
                println("Error sending request: ${error?.localizedMessage ?: "Unknown error"}")
                return@loginAPICall
            }

            accessToken = token.access_token
            userId = token.userID
            completion()
        }
    }

    private fun loginAPICall(completion: (Token?, Exception?) -> Unit) {
        val url = "$BASE_URL/api/v1/channels/$channelId/login"
        val post = Login(isAnonym = true)
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaType(), moshi.adapter(Login::class.java).toJson(post)))
            .addHeader("Content-Type", "application/json")
            .addHeader("ApiSecret", apiSecret)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                completion(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val token = moshi.adapter(Token::class.java).fromJson(body)
                        completion(token, null)
                    } catch (e: JsonDataException) {
                        completion(null, e)
                    }
                } else {
                    completion(null, Exception("Request not successful"))
                }
            }
        })
    }

    // Your other methods follow the same pattern as above. You will basically need to:
    // - Replace `URLSession.shared.dataTask` with OkHttp’s `client.newCall().enqueue()`.
    // - Replace `JSONEncoder().encode()` and `JSONDecoder().decode()` with Moshi’s `adapter().toJson()` and `adapter().fromJson()`.
    // - Adjust the property names and data types as per the conversion from Swift to Kotlin.

    @JsonClass(generateAdapter = true)
    data class Login(val isAnonym: Boolean)

    @JsonClass(generateAdapter = true)
    data class Token(
        @Json(name = "access_token") val access_token: String,
        @Json(name = "token_type") val token_type: String,
        @Json(name = "expires_in") val expires_in: Double,
        @Json(name = "userID") val userID: String?,
        @Json(name = "channelId") val channelId: String
    )

    private fun ensureSession(completion: () -> Unit) {
        if (topicId != null) {
            completion()
            return
        }

        sessionAPICall { sessionResponse, error ->
            if (error != null || sessionResponse == null) {
                println("Error sending request: ${error?.localizedMessage ?: "Unknown error"}")
                return@sessionAPICall
            }

            topicId = sessionResponse.topicId
            completion()
        }
    }

    // ... (Keep previous methods)

    private fun sessionAPICall(completion: (SessionResponse?, Exception?) -> Unit) {
        ensureLogin {
            val url = "$BASE_URL/api/v1/channels/$channelId/session"
            val post = Session(userId = userId!!)
            val request = Request.Builder()
                .url(url)
                .post(RequestBody.create("application/json".toMediaType(), moshi.adapter(Session::class.java).toJson(post)))
                .addHeader("Content-Type", "application/json")
                .addHeader("ApiSecret", apiSecret)
                .addHeader("Authorization", accessToken!!)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    completion(null, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    Log.d("EventApiClinet", "session respone "+  body)
                    if (response.isSuccessful && body != null) {
                        try {
                            val sessionResponse = moshi.adapter(SessionResponse::class.java).fromJson(body)
                            completion(sessionResponse, null)
                        } catch (e: JsonDataException) {
                            completion(null, e)
                        }
                    } else {
                        completion(null, Exception("Request not successful"))
                    }
                }
            })
        }
    }

    fun eventAPICall(event: List<EventBody>, completion: (List<Brick>?, Exception?) -> Unit) {
        ensureSession {
            val url = "$BASE_URL/api/v1/channels/$channelId/users/$userId/topics/$topicId/events"
            val request = Request.Builder()
                .url(url)
                .post(RequestBody.create("application/json".toMediaType(), moshi.adapter(List::class.java).toJson(event)))
                .addHeader("Content-Type", "application/json")
                .addHeader("ApiSecret", apiSecret)
                .addHeader("Authorization", accessToken!!)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    completion(null, e)
                }

                override fun onResponse(call: Call, response: Response) {

                    val body = response.body?.string()
                    Log.d("EventApiClinet", "bricks respone "+  body)
                    if (response.isSuccessful && body != null) {
                        try {
                            val type = Types.newParameterizedType(List::class.java, Brick::class.java)
                            val adapter: JsonAdapter<List<Brick>> = moshi.adapter(type)
                            val brickResponse = adapter.fromJson(body)
                            completion(brickResponse, null)
                        } catch (e: JsonDataException) {
                            completion(null, e)
                        }
                    } else {
                        completion(null, Exception("Request not successful"))
                    }
                }
            })
        }
    }

    @JsonClass(generateAdapter = true)
    data class Session(val userId: String)

    @JsonClass(generateAdapter = true)
    data class SessionResponse(val userId: String, val topicId: String)

    @JsonClass(generateAdapter = true)
    data class EventBody(val name: String, val text: String, val payload: EventPayload?)

    @JsonClass(generateAdapter = true)
    data class EventPayload(val eventCategory: String, val eventAction: String, val source: String)

    @JsonClass(generateAdapter = true)
    data class Brick(
        val creator: String,
        val content: Content,
        val eventsource: String,
        val trackingUrlParams: List<String>,
        val owner: String,
        val topic: String,
        val channel: String,
        val appliedDsgvoDataPrivacyRules: List<String>,
        @Json(name = "_id") val id: String,
        @Json(name = "__v") val v: Int,
        val createdAt: String,
        val updatedAt: String
    )

    @JsonClass(generateAdapter = true)
    data class Content(val schemaRef: String, val data: Map<String, Any>)

}
