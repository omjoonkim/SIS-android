package com.eighthour.makers.sis.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import com.eighthour.makers.sis.common.BaseActivity
import com.eighthour.makers.sis.common.RequiresActivityViewModel
import com.eighthour.makers.sis.libs.model.Topic
import com.eighthour.makers.sis.libs.rx.Parameter
import com.eighthour.makers.sis.libs.util.setImageWithGlide
import com.eighthour.makers.sis.libs.util.Intents
import com.eighthour.makers.sis.viewmodels.ChildTopicListViewModel
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_child_topic_list.*
import kotlinx.android.synthetic.main.viewholder_child_topic.view.*
import org.jetbrains.anko.onClick

@RequiresActivityViewModel(ChildTopicListViewModel::class)
class ChildTopicListActivity : BaseActivity<ChildTopicListViewModel>() {

    private val adapter by lazy { RecyclerAdapter().apply { onClickItem = { viewModel.inPuts.clickGoToSubTopic(it) } } }

    private var menuItem_Like: MenuItem? = null

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_topic_list)
        (applicationContext as? App)?.component?.inject(this)
        actionbarInit(toolbar, naviColor = R.color.colorAccent)
        recyclerViewInit()

        fab.onClick { viewModel.inPuts.clickAddTopic(Parameter.CLICK) }

        button_goToChat.onClick { viewModel.inPuts.clickGoToChat(Parameter.CLICK) }

        clickOptionItems.filter { it.itemId == R.id.action_like }
                .bindToLifecycle(this)
                .subscribe { viewModel.inPuts.clickLike(Parameter.CLICK) }

        viewModel.outPuts.isFavorite()
                .bindToLifecycle(this)
                .subscribe { menuItem_Like?.setIcon(if (it) R.drawable.ic_like_filled else R.drawable.ic_like) }

        viewModel.outPuts.subTopicList()
                .bindToLifecycle(this)
                .subscribe {
                    adapter.datas.clear()
                    adapter.datas.addAll(it)
                    adapter.notifyDataSetChanged()
                }

        viewModel.outPuts.goToChat()
                .bindToLifecycle(this)
                .subscribe { startActivity(Intent(this, ChatRoomActivity::class.java).putExtra(Intents.TOPIC_ID, it)) }

        viewModel.outPuts.topic()
                .bindToLifecycle(this)
                .subscribe {
                    with(it) {
                        imageView_background.setImageWithGlide(it.backgroundImage)
                        textView_countOfOnChatPeople.text = "${it.memberNum} 명 대화중."
                        supportActionBar?.title = name
                        this@ChildTopicListActivity.isFavorite = isFavorite
                        menuItem_Like?.setIcon(if (isFavorite) R.drawable.ic_like_filled else R.drawable.ic_like)
                    }
                }

        viewModel.outPuts.goToSubTopic()
                .bindToLifecycle(this)
                .subscribe { startActivity(Intent(this, ChildTopicListActivity::class.java).putExtra(Intents.TOPIC_ID, it)) }

        viewModel.outPuts.goToAddTopic()
                .bindToLifecycle(this)
                .subscribe { startActivity(Intent(this, TopicCreateActivity::class.java).putExtra(Intents.TOPIC_ID, it)) }

    }

    private fun recyclerViewInit() =
            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@ChildTopicListActivity)
                adapter = this@ChildTopicListActivity.adapter
            }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
            super.onCreateOptionsMenu(menu.apply {
                menuInflater.inflate(R.menu.menu_child_topic, this)
                menuItem_Like = this?.findItem(R.id.action_like)
                menuItem_Like?.setIcon(if (this@ChildTopicListActivity.isFavorite) R.drawable.ic_like_filled else R.drawable.ic_like)
            })

    private class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

        val datas = mutableListOf<Topic>()

        var onClickItem: (Long) -> Unit = {}

        override fun getItemCount(): Int = datas.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerAdapter.ViewHolder =
                ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.viewholder_child_topic, parent, false))

        override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
            with(holder.itemView) {
                val data = datas[position]
                title.text = data.name
                subTitle.text = "${data.subtopic_num}개 아메바"
                image.setImageWithGlide(data.backgroundImage)
                onClick { onClickItem(data.id) }
            }
        }

        private class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}