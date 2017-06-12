package com.eighthour.makers.sis.viewmodels

import android.content.Context
import com.eighthour.makers.sis.common.BaseViewModel
import com.eighthour.makers.sis.libs.model.User
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.rx.neverError
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class LoginViewModel(context: Context) : BaseViewModel(context), LoginViewModelInPuts, LoginViewModelOutPuts {

    val inPuts: LoginViewModelInPuts = this
    private val email = PublishSubject.create<String>()
    private val password = PublishSubject.create<String>()
    private val loginClick = PublishSubject.create<Parameter>()
    private val goRegisterClick = BehaviorSubject.create<Parameter>()

    val outPuts: LoginViewModelOutPuts = this
    private val loginSuccess = PublishSubject.create<Boolean>()
    private val goToRegister = PublishSubject.create<Parameter>()
    private val setLoginButtonIsEnabled = PublishSubject.create<Boolean>()


    init {
        val emailAndPassword: Observable<Pair<String, String>> = email.combineLatest(password)

        val isValid = emailAndPassword.map { it.first.isNotEmpty() && it.second.isNotEmpty() }

        isValid.bindToLifeCycle()
                .subscribe(setLoginButtonIsEnabled)

        emailAndPassword
                .compose<Pair<String, String>> { loginClick.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .map { User(email = it.first, password = it.second) }
                .flatMapMaybe { submit(it) }
                .bindToLifeCycle()
                .subscribe {
                    enviorment.currentUser.login(it)
                    loginSuccess.onNext(true)
                }

        goRegisterClick.bindToLifeCycle()
                .subscribe(goToRegister)
    }

    private fun submit(user: User) = enviorment.apiClient.logIn(user).neverError(error)

    override fun id(email: String) = this.email.onNext(email)
    override fun password(password: String) = this.password.onNext(password)
    override fun loginClick(paramter: Parameter) = loginClick.onNext(paramter)
    override fun goToRegisterClick(parameter: Parameter) = goRegisterClick.onNext(parameter)

    override fun loginSuccess(): Observable<Boolean> = loginSuccess
    override fun setLoginButtonIsEnabled(): Observable<Boolean> = setLoginButtonIsEnabled
    override fun goToRegister(): Observable<Parameter> = goToRegister
}

interface LoginViewModelInPuts {

    fun id(email: String)
    fun password(password: String)
    fun loginClick(paramter: Parameter)
    fun goToRegisterClick(parameter: Parameter)
}

interface LoginViewModelOutPuts {
    fun loginSuccess(): Observable<Boolean>
    fun setLoginButtonIsEnabled(): Observable<Boolean>
    fun goToRegister(): Observable<Parameter>
}

