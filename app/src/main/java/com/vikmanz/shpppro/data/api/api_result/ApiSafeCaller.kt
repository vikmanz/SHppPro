package com.vikmanz.shpppro.data.api.api_result

import com.google.gson.Gson
import com.vikmanz.shpppro.utils.extensions.log
import com.vikmanz.shpppro.data.dto.ErrorServerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ua.digitalminds.fortrainerapp.data.result.ApiResult
import com.vikmanz.shpppro.data.api.api_result.ApiSafeCallerError.UNKNOWN_EXCEPTION
import java.io.IOException

private const val DEBUG = true

object ApiSafeCaller {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ApiResult<T> =

        withContext(Dispatchers.IO) {
            try {
                log("try...", DEBUG)
                ApiResult.Success(apiCall.invoke())
            } catch (throwable: Throwable) {

                log("Catch!", DEBUG)

                when (throwable) {

                    is IOException -> {     // Network error
                        log("IO Exception!", DEBUG)
                        ApiResult.NetworkError
                    }

                    is HttpException -> handleHttpError(throwable)  // Http error

                    else -> {
                        log("Other Exception!", DEBUG)
                        ApiResult.ServerError(UNKNOWN_EXCEPTION)    // Unknown error
                    }

                }
            }
        }


    private fun handleHttpError(throwable: HttpException): ApiResult.ServerError {
        log("HttpException Exception!", DEBUG)
        val code = throwable.code()
        val json = throwable.response()?.errorBody()?.string()
        return try {
            log("try convert first", DEBUG)
            val errorResponse = convertErrorBody(json, ErrorServerResponse::class.java)
            responseToServerError(errorResponse)
        } catch (e: Exception) {
            ApiResult.ServerError(UNKNOWN_EXCEPTION)
        }
    }

    private fun <T> convertErrorBody(
        jsonString: String?,
        classOfError: Class<T>
    ): T? {
        return jsonString?.let {
            log("Try convert error to class [${classOfError.simpleName}]", DEBUG)
            log("Error json [$it]", DEBUG)
            val errorResponse = Gson().fromJson(it, classOfError)
            log("error response: $errorResponse", DEBUG)
            errorResponse
        }
    }

    private fun responseToServerError(errorResponse: ErrorServerResponse?): ApiResult.ServerError {
        val error = ApiSafeCallerError from errorResponse?.message
        return if (error != null) {
            log("Error is not null", DEBUG)
            ApiResult.ServerError(error)
        } else {
            log("!!!Error is null!!!", DEBUG)
            ApiResult.ServerError(ApiSafeCallerError.UNKNOWN_HTTP_EXCEPTION)
        }
    }
}