package com.vikmanz.shpppro.domain.usecases.account

import com.vikmanz.shpppro.data.model.User
import com.vikmanz.shpppro.data.repository.account.ShPPAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.digitalminds.fortrainerapp.data.result.ApiResult
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val accountRepository: ShPPAccountRepository
) {

    operator fun invoke(): Flow<ApiResult<User>> = flow {
        emit(ApiResult.Loading)
        emit(accountRepository.getUser())
    }
}