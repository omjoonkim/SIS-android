package com.eighthour.makers.sis.viewmodels

import android.content.Context
import com.eighthour.makers.sis.common.BaseViewModel
import com.eighthour.makers.sis.libs.network.model.Topic
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.rx.neverError
import com.eighthour.makers.sis.libs.util.Intents
import com.eighthour.makers.sis.libs.util.getLongExtra
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.combineLatest
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class TopicCreateViewModel(context: Context) : BaseViewModel(context), TopicCreateInPuts, TopicCreateOutPuts {

    val inPuts: TopicCreateInPuts = this
    private val name = BehaviorSubject.create<String>()
    private val clickCreate = PublishSubject.create<Parameter>()
    private val image = BehaviorSubject.create<String>()

    val outPuts: TopicCreateOutPuts = this
    private val enableCreateButton = PublishSubject.create<Boolean>()
    private val successCreatedTopic = PublishSubject.create<Parameter>()
    private val parentName = BehaviorSubject.create<String>()
    private val nameLength = BehaviorSubject.create<Int>()
    private val showLoadingDialog = PublishSubject.create<Boolean>()

    init {

        val parentId = BehaviorSubject.create<Long>()

        intent.map { it.getLongExtra(Intents.TOPIC_ID) }
                .filter { it != -1L }
                .bindToLifeCycle()
                .subscribeWith(parentId)
                .map { enviorment.topicRepository.getTopic(it)?.name ?: "" }
                .bindToLifeCycle()
                .subscribe(parentName)

        name.map { 36 - it.length }
                .bindToLifeCycle()
                .subscribe(nameLength)

        val isValid = name.filter { it.isNotEmpty() }

        isValid.map { it.isNotEmpty() }.bindToLifeCycle()
                .subscribe(enableCreateButton)

        parentId.combineLatest(name, image.startWith(""))
                .compose<Topic> { clickCreate.withLatestFrom(it, BiFunction { _, (first, second, third) -> Topic(parentId = first, name = second, backgroundImage = if (third.isNullOrEmpty()) null else third) }) }
                .bindToLifeCycle()
                .doOnNext { showLoadingDialog.onNext(true) }
                .flatMapMaybe { enviorment.topicRepository.addTopic(it).neverError(error) }
                .map { Parameter.SUCCESS }
                .bindToLifeCycle()
                .subscribe(successCreatedTopic)

    }

    override fun name(name: String) = this.name.onNext(name)
    override fun clickCreate(parameter: Parameter) = this.clickCreate.onNext(parameter)
    override fun image(image: String) = this.image.onNext(image)

    override fun nameLength(): Observable<Int> = nameLength
    override fun parentName(): Observable<String> = parentName
    override fun enableCreateButton(): Observable<Boolean> = enableCreateButton
    override fun successCreatedTopic(): Observable<Parameter> = successCreatedTopic
    override fun image(): Observable<String> = image
    override fun showLoadingDialog(): Observable<Boolean> = showLoadingDialog
}

interface TopicCreateInPuts {
    fun name(name: String)
    fun clickCreate(parameter: Parameter)
    fun image(image: String)
}

interface TopicCreateOutPuts {
    fun parentName(): Observable<String>
    fun enableCreateButton(): Observable<Boolean>
    fun successCreatedTopic(): Observable<Parameter>
    fun nameLength(): Observable<Int>
    fun image(): Observable<String>
    fun showLoadingDialog(): Observable<Boolean>
}