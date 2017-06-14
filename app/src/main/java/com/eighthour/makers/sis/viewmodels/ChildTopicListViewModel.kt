package com.eighthour.makers.sis.viewmodels

import android.content.Context
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.BaseViewModel
import com.eighthour.makers.sis.libs.network.model.Topic
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.rx.neverError
import com.eighthour.makers.sis.libs.util.Intents
import com.eighthour.makers.sis.libs.util.getLongExtra
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ChildTopicListViewModel(context: Context) : BaseViewModel(context), SubTopicListInPuts, SubTopicListOutPuts {

    val inPuts: SubTopicListInPuts = this
    private val clickGoToChat = PublishSubject.create<Parameter>()
    private val clickGoToSubTopic = PublishSubject.create<Long>()
    private val clickAddTopic = PublishSubject.create<Parameter>()
    private val clickLike = PublishSubject.create<Parameter>()

    val outPuts: SubTopicListOutPuts = this
    private val goToChat = PublishSubject.create<Long>()
    private val goToSubTopic = PublishSubject.create<Long>()
    private val goToAddTopic = PublishSubject.create<Long>()
    private val subTopicList = PublishSubject.create<List<Topic>>()
    private val topic = BehaviorSubject.create<Topic>()
    private val isFavorite = PublishSubject.create<Boolean>()

    private val parentId = BehaviorSubject.create<Long>()
    private val refresh = PublishSubject.create<Parameter>()

    init {

        parentId.compose<Long> { refresh.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .flatMapMaybe { enviorment.topicRepository.loadTopics(it).neverError() }
                .filter { it -> it.isNotEmpty() }
                .bindToLifeCycle()
                .subscribe(subTopicList)

        parentId.compose<Long> { refresh.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .map { enviorment.topicRepository.getTopic(it) }
                .bindToLifeCycle()
                .subscribe(topic)

        intent.map { it.getLongExtra(Intents.TOPIC_ID) }
                .filter { it != -1L }
                .neverError()
                .bindToLifeCycle()
                .subscribe(parentId)

        topic.compose<Topic> { clickGoToChat.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .map { it.id }
                .bindToLifeCycle()
                .subscribe(goToChat)

        clickGoToSubTopic.bindToLifeCycle()
                .subscribe(goToSubTopic)

        parentId.compose<Long> { clickLike.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .map { enviorment.topicRepository.changeFavorite(it) }
                .bindToLifeCycle()
                .subscribe(isFavorite)

        parentId.compose<Long> { clickAddTopic.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .bindToLifeCycle()
                .subscribe(goToAddTopic)
    }

    override fun onResume(view: BaseActivity<BaseViewModel>) {
        super.onResume(view)
        refresh.onNext(Parameter.EVENT)
    }


    override fun clickGoToChat(parameter: Parameter) = clickGoToChat.onNext(parameter)
    override fun clickGoToSubTopic(topicId: Long) = clickGoToSubTopic.onNext(topicId)
    override fun clickAddTopic(parameter: Parameter) = clickAddTopic.onNext(parameter)
    override fun clickLike(parameter: Parameter) = clickLike.onNext(parameter)

    override fun goToChat(): PublishSubject<Long> = goToChat
    override fun goToSubTopic(): PublishSubject<Long> = goToSubTopic
    override fun goToAddTopic(): PublishSubject<Long> = goToAddTopic
    override fun subTopicList(): Observable<List<Topic>> = subTopicList
    override fun topic(): Observable<Topic> = topic
    override fun isFavorite(): Observable<Boolean>  = isFavorite
}

interface SubTopicListInPuts {
    fun clickGoToChat(parameter: Parameter)
    fun clickGoToSubTopic(topicId: Long)
    fun clickAddTopic(parameter: Parameter)
    fun clickLike(parameter: Parameter)
}

interface SubTopicListOutPuts {
    fun goToChat(): Observable<Long>
    fun goToSubTopic(): Observable<Long>
    fun goToAddTopic(): Observable<Long>
    fun subTopicList(): Observable<List<Topic>>
    fun isFavorite() : Observable<Boolean>
    fun topic(): Observable<Topic>
}