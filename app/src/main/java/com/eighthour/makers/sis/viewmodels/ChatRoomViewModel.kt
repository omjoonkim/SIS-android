package com.eighthour.makers.sis.viewmodels

import android.content.Context
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.BaseViewModel
import com.eighthour.makers.sis.libs.util.fromJson
import com.eighthour.makers.sis.libs.network.model.ChatItem
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.rx.neverError
import com.eighthour.makers.sis.libs.network.APIService
import com.eighthour.makers.sis.libs.util.toJson
import com.eighthour.makers.sis.libs.util.Intents
import com.eighthour.makers.sis.libs.util.getLongExtra
import com.eighthour.makers.sis.ui.ChatRoomActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.util.concurrent.TimeUnit

class ChatRoomViewModel(context: Context) : BaseViewModel(context), ChatRoomViewModelInPuts, ChatRoomViewModelOutPuts {

    val inputs: ChatRoomViewModelInPuts = this
    private val commentOfWrite = BehaviorSubject.create<String>()
    private val clickPostComment = BehaviorSubject.create<Parameter>()
    private val loadChats = PublishSubject.create<Long>()
    private val loadBeforeComment = BehaviorSubject.create<Parameter>()

    val outputs: ChatRoomViewModelOutPuts = this
    private val isEnablePostChatButton = BehaviorSubject.create<Boolean>()
    private val postComment = PublishSubject.create<String>()
    private val successLoadChats = PublishSubject.create<List<ChatItem>>()
    private val successReceiveChat = PublishSubject.create<ChatItem>()

    private val receiveChat = PublishSubject.create<ChatItem>()
    private val topicId = BehaviorSubject.create<Long>()
    private val topicName = BehaviorSubject.create<String>()

    var socket: WebSocketClient? = null

    init {
        intent.map { it.getLongExtra(Intents.TOPIC_ID) }
                .filter { it != -1L }
                .bindToLifeCycle()
                .subscribeWith(topicId)
                .map { enviorment.topicRepository.getTopic(it)?.name ?: "" }
                .bindToLifeCycle()
                .subscribe { topicName.onNext(it) }

        commentOfWrite.map { it.isNullOrEmpty().not() }
                .bindToLifeCycle()
                .subscribe(isEnablePostChatButton)

        commentOfWrite.compose<String> { clickPostComment.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .doOnNext { socket?.send(ChatRoomActivity.SendItem(payload = ChatRoomActivity.Content(enviorment.currentUser.user?.id, it)).toJson()) }
                .bindToLifeCycle()
                .subscribe(postComment)

        loadChats.flatMapMaybe { enviorment.chatRepository.loadChats(it).neverError(error) }
                .bindToLifeCycle()
                .subscribe(successLoadChats)

        receiveChat.filter { it.id != -1L }
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifeCycle()
                .subscribe(successReceiveChat)

        topicId.compose<Long> { loadBeforeComment.withLatestFrom(it, BiFunction { _, t2 -> t2 }) }
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .flatMapMaybe { enviorment.chatRepository.loadBeforeChat(it).neverError() }
                .bindToLifeCycle()
                .subscribe(successLoadChats)

    }

    override fun onResume(view: BaseActivity<BaseViewModel>) {
        super.onResume(view)
        topicId.bindToLifeCycle().doOnNext { soccketInit(it) }.subscribe(loadChats)
    }

    private fun soccketInit(topicId: Long?) {
        socket?.close()
        socket = object : WebSocketClient(URI(APIService.EndPoint.wsUrl(topicId)), Draft_17()) {
            override fun onOpen(handshakedata: ServerHandshake?) {}

            override fun onClose(code: Int, reason: String?, remote: Boolean) {}

            override fun onMessage(message: String?) {
                try {
                    JSONObject(message).getJSONObject("payload").toString().fromJson<ChatItem>()?.let { receiveChat.onNext(it) }

                } catch (ignore: Exception) {
                    ignore.printStackTrace()
                }
            }

            override fun onError(ex: Exception?) {
                ex?.printStackTrace()
            }
        }
        socket?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.close()
    }

    override fun commentOfWrite(text: String) = commentOfWrite.onNext(text)
    override fun loadBeforeComment(parameter: Parameter) = loadBeforeComment.onNext(parameter)
    override fun clickPostComment(parameter: Parameter) = clickPostComment.onNext(parameter)

    override fun isEnablePostChatButton(): Observable<Boolean> = isEnablePostChatButton
    override fun successLoadChats(): Observable<List<ChatItem>> = successLoadChats
    override fun postComment(): Observable<String> = postComment
    override fun successReceiveChat(): Observable<ChatItem> = successReceiveChat
    override fun topicName(): Observable<String> = topicName
}

interface ChatRoomViewModelInPuts {
    fun commentOfWrite(text: String)
    fun clickPostComment(parameter: Parameter)
    fun loadBeforeComment(parameter: Parameter)
}

interface ChatRoomViewModelOutPuts {
    fun isEnablePostChatButton(): Observable<Boolean>
    fun postComment(): Observable<String>
    fun successLoadChats(): Observable<List<ChatItem>>
    fun successReceiveChat(): Observable<ChatItem>
    fun topicName(): Observable<String>
}