package itmo.alk.womplist.data.network.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain

object ApolloClientProvider {
    private const val BASE_URL = "https://shikimori.one/api/graphql"

    fun getClient(accessToken: String? = null): ApolloClient {
        val builder = ApolloClient.Builder()
            .serverUrl(BASE_URL)

        if (accessToken != null) {
            builder.addHttpInterceptor(AuthorizationInterceptor(accessToken))
        }

        return builder.build()
    }
}

class AuthorizationInterceptor(
    private val accessToken: String
) : HttpInterceptor {
    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }
}