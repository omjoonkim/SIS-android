package com.eighthour.makers.sis.viewmodels

import android.content.Context
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.BaseViewModel
import com.eighthour.makers.sis.libs.network.model.Topic
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.rx.neverError
import io.reactivex.Observable
import io.reactivex.rxkotlin.toSingle
import io.reactivex.subjects.PublishSubject

class MainViewModel(context: Context) : BaseViewModel(context), MainViewModelInputs, MainViewModelOutputs {

    val inPuts: MainViewModelInputs = this
    private val refresh = PublishSubject.create<Parameter>()
    private val clickToTopicItem = PublishSubject.create<Long>()
    private val clickToChatRoom = PublishSubject.create<Parameter>()
    private val clickToLogout = PublishSubject.create<Parameter>()
    private val clickToLike = PublishSubject.create<Parameter>()

    val outPuts: MainViewModelOutputs = this
    private val onSuccessRefresh = PublishSubject.create<List<Topic>>()
    private val goChatRoom = PublishSubject.create<Long>()
    private val goChildTopics = PublishSubject.create<Long>()
    private val goLogin = PublishSubject.create<Parameter>()
    private val goToLikes = PublishSubject.create<Parameter>()

    init {
        refresh.flatMapMaybe { enviorment.topicRepository.loadTopics().neverError(error) }
                .filter { it.isNotEmpty() }
                .bindToLifeCycle()
                .subscribe(onSuccessRefresh)

        clickToChatRoom
                .map { 1L }
                .bindToLifeCycle()
                .subscribe(goChatRoom)

        clickToTopicItem.bindToLifeCycle()
                .subscribe(goChildTopics)

        clickToLogout.flatMapSingle{ enviorment.apiClient.logOut().neverError().toSingle()}
                .map { Parameter.EVENT }
                .bindToLifeCycle()
                .doOnNext { enviorment.currentUser.logout() }
                .subscribe (goLogin)

        clickToLike.map { Parameter.EVENT }
                .bindToLifeCycle()
                .subscribe(goToLikes)

    }

    override fun onResume(view: BaseActivity<BaseViewModel>) {
        super.onResume(view)
        refresh(Parameter.EVENT)
    }

    override fun refresh(parameter: Parameter) = refresh.onNext(parameter)
    override fun clickToTopicItem(id: Long) = clickToTopicItem.onNext(id)
    override fun clickToChat(parameter: Parameter) = clickToChatRoom.onNext(parameter)
    override fun clickToLogout(parameter: Parameter) = clickToLogout.onNext(parameter)
    override fun clickToLike(parameter: Parameter) = clickToLike.onNext(parameter)

    override fun onSuccessRefresh(): Observable<List<Topic>> = onSuccessRefresh
    override fun goChatRoom(): Observable<Long> = goChatRoom
    override fun goChildTopics(): Observable<Long> = goChildTopics
    override fun goLogin(): Observable<Parameter> = goLogin
    override fun goToLikes(): Observable<Parameter> = goToLikes
}

interface MainViewModelInputs {
    fun refresh(parameter: Parameter)
    fun clickToTopicItem(id: Long)
    fun clickToChat(parameter: Parameter)
    fun clickToLogout(parameter: Parameter)
    fun clickToLike(parameter: Parameter)
}

interface MainViewModelOutputs {
    fun onSuccessRefresh(): Observable<List<Topic>>
    fun goChatRoom(): Observable<Long>
    fun goChildTopics(): Observable<Long>
    fun goLogin(): Observable<Parameter>
    fun goToLikes(): Observable<Parameter>
}
