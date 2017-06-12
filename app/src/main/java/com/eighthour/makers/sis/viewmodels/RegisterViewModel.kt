package com.eighthour.makers.sis.viewmodels

import android.content.Context
import com.eighthour.makers.sis.common.BaseViewModel
import com.eighthour.makers.sis.libs.model.User
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.rx.combineLatest
import com.eighthour.makers.sis.libs.rx.neverError
import com.eighthour.makers.sis.libs.util.Quardruple
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class RegisterViewModel(context: Context) : BaseViewModel(context), RegisterViewModelOutPuts, RegisterViewModelInPuts {

    val outPuts: RegisterViewModelOutPuts = this
    private val registerSuccess = PublishSubject.create<Parameter>()
    private val setRegisterButtonIsEnabled = PublishSubject.create<Boolean>()

    val inPuts: RegisterViewModelInPuts = this
    private val id = PublishSubject.create<String>()
    private val name = PublishSubject.create<String>()
    private val password = PublishSubject.create<String>()
    private val passwordConfirm = PublishSubject.create<String>()
    private val registerClick = PublishSubject.create<Parameter>()

    init {
        val requiredDatasOfRegister: Observable<Quardruple<String, String, String, String>> =
                id.combineLatest(name, password, passwordConfirm)

        val isValid = requiredDatasOfRegister.map { isValid(it) }

        isValid.bindToLifeCycle()
                .subscribe(setRegisterButtonIsEnabled)

        requiredDatasOfRegister
                .compose<Quardruple<String, String, String, String>> { registerClick.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .map { User(email = it.first, username = it.second, password = it.third) }
                .toFlowable(BackpressureStrategy.DROP)
                .flatMapMaybe { register(it) }
                .bindToLifeCycle()
                .subscribe {
                    enviorment.currentUser.login(it)
                    registerSuccess.onNext(Parameter.SUCCESS)
                }
    }

    private fun register(it: User) = enviorment.apiClient.register(it).neverError(error)

    private fun isValid(data: Quardruple<String, String, String, String>): Boolean =
            data.first.isNotEmpty() && data.second.isNotEmpty() && data.third.isNotEmpty() && data.third == data.fourth

    override fun id(id: String) = this.id.onNext(id)
    override fun name(name: String) = this.name.onNext(name)
    override fun password(password: String) = this.password.onNext(password)
    override fun passwordConfirm(passwordConfirm: String) = this.passwordConfirm.onNext(passwordConfirm)
    override fun registerClick(parameter: Parameter) = registerClick.onNext(parameter)

    override fun registerSuccess(): Observable<Parameter> = registerSuccess
    override fun setRegisterButtonIsEnabled(): Observable<Boolean> = setRegisterButtonIsEnabled
}

interface RegisterViewModelOutPuts {
    fun registerSuccess(): Observable<Parameter>
    fun setRegisterButtonIsEnabled(): Observable<Boolean>
}

interface RegisterViewModelInPuts {
    fun id(id: String)
    fun name(name: String)
    fun password(password: String)
    fun passwordConfirm(passwordConfirm: String)
    fun registerClick(parameter: Parameter)
}
