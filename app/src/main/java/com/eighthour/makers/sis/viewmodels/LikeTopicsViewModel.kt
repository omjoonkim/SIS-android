package com.eighthour.makers.sis.viewmodels

import android.content.Context
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.BaseViewModel
import com.eighthour.makers.sis.libs.model.Topic
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.rx.neverError
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Omjoon on 2017. 6. 10..
 */
class LikeTopicsViewModel(context: Context) : BaseViewModel(context), LikeTopicsInPuts, LikeTopicsOutPuts {

    val inPuts: LikeTopicsInPuts = this
    private val refresh = PublishSubject.create<Parameter>()
    private val clickTopic = PublishSubject.create<Long>()
    private val likeTopic = PublishSubject.create<Long>()

    val outPuts: LikeTopicsOutPuts = this
    private val onLoadTopics = PublishSubject.create<List<Topic>>()
    private val goToTopic = PublishSubject.create<Long>()
    private val onLikeTopic = PublishSubject.create<Pair<Long, Boolean>>()

    init {
        refresh.flatMapMaybe { enviorment.topicRepository.loadTopicOfLike().neverError() }
                .bindToLifeCycle()
                .subscribe(onLoadTopics)

        clickTopic.bindToLifeCycle()
                .subscribe(goToTopic)

        likeTopic.map { it to enviorment.topicRepository.changeFavorite(it) }
                .bindToLifeCycle()
                .subscribe(onLikeTopic)
    }

    override fun onResume(view: BaseActivity<BaseViewModel>) {
        super.onResume(view)
        refresh.onNext(Parameter.EVENT)
    }

    override fun clickTopic(topicId: Long) = clickTopic.onNext(topicId)
    override fun likeTopic(topicId: Long) = likeTopic.onNext(topicId)

    override fun onLoadTopics(): Observable<List<Topic>> = onLoadTopics
    override fun goToTopic(): Observable<Long> = goToTopic
    override fun onLikeTopic(): Observable<Pair<Long, Boolean>> = onLikeTopic
}

interface LikeTopicsInPuts {
    fun clickTopic(topicId: Long)
    fun likeTopic(topicId: Long)
}

interface LikeTopicsOutPuts {
    fun onLoadTopics(): Observable<List<Topic>>
    fun goToTopic(): Observable<Long>
    fun onLikeTopic(): Observable<Pair<Long, Boolean>>
}