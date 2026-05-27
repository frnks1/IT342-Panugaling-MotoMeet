package com.motomeet.mobile.data.network

import com.motomeet.mobile.BuildConfig
import com.motomeet.mobile.MotoMeetApp
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val cookieJar = object : CookieJar {
        private val cookieStore = HashMap<String, List<Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: ArrayList()
        }
    }

    private val authInterceptor = Interceptor { chain ->
        val token = TokenManager.getInstance(MotoMeetApp.instance).getToken()
        val request = chain.request().newBuilder().apply {
            addHeader("Accept", "application/json")
            addHeader("X-Requested-With", "XMLHttpRequest")
            
            // SECURITY FIX: Do not send the token if it's just the old JSON response string
            // or if it's null/empty. This prevents the backend from rejecting malformed headers.
            if (!token.isNullOrBlank() && !token.contains("{") && !token.contains("\"") && !token.contains(" ")) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        
        val response = chain.proceed(request)
        
        // If we get a 302 to login or a 401, it means the token/session is invalid
        if (response.code == 302 || response.code == 401) {
            val location = response.header("Location")
            if (location?.contains("/login") == true || response.code == 401) {
                // Potential session expiry - TokenManager.getInstance(MotoMeetApp.instance).clearToken()
                // We don't clear here to avoid race conditions, but it's a sign of auth failure.
            }
        }
        
        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(false) // Keep as false so we can catch 302 redirects to login
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val mobileApi: MobileApi = retrofit.create(MobileApi::class.java)
}
