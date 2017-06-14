package com.eighthour.makers.sis.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.CurrentUser
import com.eighthour.makers.sis.common.RequiresActivityViewModel
import com.eighthour.makers.sis.libs.util.addTextWatcher
import com.eighthour.makers.sis.libs.network.model.ChatItem
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.util.toDp
import com.eighthour.makers.sis.viewmodels.ChatRoomViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_chat_room.*
import org.jetbrains.anko.collections.forEachReversedByIndex
import org.jetbrains.anko.onClick
import javax.inject.Inject


@RequiresActivityViewModel(value = ChatRoomViewModel::class)
class ChatRoomActivity : BaseActivity<ChatRoomViewModel>() {

    @Inject
    lateinit var currentUser: CurrentUser

    private val adapter: ChatAdapter by lazy { ChatAdapter(currentUser) }

    private var isAutoScrollLast = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        (applicationContext as? App)?.component?.inject(this)
        actionbarInit(toolbar, titleColor = R.color.color4a4a4a, naviColor = R.color.colorPrimary)
        recyclerViewInit()

        editText_comment.addTextWatcher({ viewModel.inputs.commentOfWrite(it) })
        button_postChat.onClick { viewModel.inputs.clickPostComment(Parameter.CLICK) }

        viewModel.outputs.isEnablePostChatButton().bindToLifecycle(this)
                .subscribe { button_postChat.isEnabled = it }

        viewModel.outputs.postComment().bindToLifecycle(this)
                .subscribe { editText_comment.setText("") }

        viewModel.outputs.topicName().bindToLifecycle(this)
                .subscribe { toolbar.title = it }

        viewModel.outputs.successLoadChats().bindToLifecycle(this)
                .subscribe {
                    val isFirst = adapter.datas.isEmpty()
                    adapter.datas.addAll(0, it)
                    adapter.notifyItemRangeInserted(0, it.size)
                    if (isFirst)
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                }

        viewModel.outputs.successReceiveChat().bindToLifecycle(this)
                .subscribe {
                    adapter.datas.add(it)
                    adapter.notifyItemInserted(adapter.datas.size)
                    if (isAutoScrollLast || it.user?.id == currentUser.id)
                        recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
    }

    private fun recyclerViewInit() =
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@ChatRoomActivity)
                adapter = this@ChatRoomActivity.adapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        (recyclerView?.layoutManager as? LinearLayoutManager)?.let {
                            isAutoScrollLast = it.findLastVisibleItemPosition() == adapter.itemCount - 1
                            if (it.findFirstVisibleItemPosition() == 0)
                                viewModel.inputs.loadBeforeComment(Parameter.EVENT)
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) = super.onScrollStateChanged(recyclerView, newState)
                })
                addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                    if (bottom < oldBottom)
                        recyclerView.postDelayed({
                            if (recyclerView.adapter.itemCount != 0)
                                recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount - 1)
                        }, 100)
                }
            }

    private class ChatAdapter(val user: CurrentUser) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

        private enum class VIEWTYPE {all, top, middle, bottom, other_all, other_top, other_middle, other_bottom }

        var datas: MutableList<ChatItem> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatAdapter.ViewHolder =
                ViewHolder(LayoutInflater.from(parent?.context)
                        .inflate(
                                if (viewType > 3)
                                    R.layout.viewholder_other_comment
                                else
                                    R.layout.viewholder_comment, parent
                                , false)
                        , viewType)

        override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) = holder.bindData(datas[position])

        override fun getItemCount(): Int = datas.size

        override fun getItemViewType(position: Int): Int {
            val current = datas[position]
            val isMe = current.user?.id == user.id
            var prev: ChatItem? = null
            var next: ChatItem? = null

            datas.subList(0, position).forEachReversedByIndex {
                if (it.user?.id == current.user?.id)
                    prev = it
                else
                    return@forEachReversedByIndex
            }
            datas.subList(position + 1, datas.size).forEach {
                if (it.user?.id == current.user?.id)
                    next = it
                else
                    return@forEach
            }

            val viewType =
                    if (prev == null)
                        if (next == null)
                            if (isMe)
                                VIEWTYPE.all
                            else
                                VIEWTYPE.other_all
                        else
                            if (isMe)
                                VIEWTYPE.top
                            else
                                VIEWTYPE.other_top
                    else
                        if (next == null)
                            if (isMe)
                                VIEWTYPE.bottom
                            else
                                VIEWTYPE.other_bottom
                        else
                            if (isMe)
                                VIEWTYPE.middle
                            else
                                VIEWTYPE.other_middle
            return viewType.ordinal
        }


        private class ViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

            private val viewType = VIEWTYPE.values()[viewType]
            private val textView_Content = itemView.findViewById(R.id.textView_content) as? TextView
            private val textView_senderName = itemView.findViewById(R.id.textView_senderName) as? TextView
            private val textView_time = itemView.findViewById(R.id.textView_time) as? TextView

            fun bindData(item: ChatItem) {
                with(item) {
                    textView_Content?.text = content

                    textView_time?.visibility = View.GONE
                    textView_senderName?.visibility = View.GONE

                    if (viewType == VIEWTYPE.all || viewType == VIEWTYPE.bottom
                            || viewType == VIEWTYPE.other_bottom || viewType == VIEWTYPE.other_all) {
                        textView_time?.text = dateTime
                        textView_time?.visibility = View.VISIBLE
                    }

                    if (viewType == VIEWTYPE.other_all || viewType == VIEWTYPE.other_top) {
                        textView_senderName?.visibility = View.VISIBLE
                        textView_senderName?.text = user?.username
                    }

                    if (viewType == VIEWTYPE.other_top || viewType == VIEWTYPE.top
                            || viewType == VIEWTYPE.other_all || viewType == VIEWTYPE.all)
                        with(itemView) { setPadding(paddingLeft, 23.toDp().toInt(), paddingRight, paddingBottom) }
                    else
                        with(itemView) { setPadding(paddingLeft, 3.toDp().toInt(), paddingRight, paddingBottom) }
                }
            }
        }

    }

    data class Content(val user_id: Long?, val content: String)
    data class SendItem(val action: String = "new_chat_send", val payload: Content)

}
