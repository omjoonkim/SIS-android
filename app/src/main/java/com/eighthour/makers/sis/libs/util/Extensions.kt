package com.eighthour.makers.sis.libs.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.TypedValue
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eighthour.makers.sis.R
import com.eighthour.makers.sis.common.App
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun Context.showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.showToast(resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()

fun EditText.addTextWatcher(afterTextChanged: (String) -> Unit = { },
                            beforeTextChanged: (CharSequence?, Int, Int, Int) -> Unit = { _, _, _, _ -> },
                            onTextChanged: (CharSequence?, Int, Int, Int) -> Unit = { _, _, _, _ -> }) =
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = afterTextChanged(s.toString())

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = beforeTextChanged(s, start, count, after)

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = onTextChanged(s, start, before, count)
        })

@SuppressLint("SimpleDateFormat")
fun String.toDate(format: String = "yyy-MM-dd HH:mm:ss"): Date? =
        try {
            SimpleDateFormat(format).parse(this)
        } catch (e: Exception) {
            null
        }

fun ImageView.setImageWithGlide(url: String?) =
        url?.let {
            try {
                App.instance.component.enviorment().requestManager.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().error(R.mipmap.ic_launcher_round).into(this)
            } catch (ignore: Exception) {
            }
        } ?: setImageResource(0)

val NowInMillis get() = System.currentTimeMillis()

@SuppressLint("SimpleDateFormat")
fun Long.toTimeText(): String =
        ((NowInMillis - this) / 1000).let { diff ->
            if (diff < 60)
                "방금 전"
            else if (diff < 60 * 60)
                "${diff / 60}분 전"
            else if (diff < 60 * 60 * 24)
                "${diff / 60 / 60}시간 전"
            else if (diff < 60 * 60 * 24 * 7)
                "${diff / 60 / 60 / 24}일 전"
            else
                SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분").format(Date(this))
        }

fun Date.toTimeText(): String = time.toTimeText()

fun Int.toDp(): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), App.instance.resources.displayMetrics)

fun String.toFile(): File? = File(this)

fun File.toBase64(): String? = "data:image/${name.split(",").lastOrNull()};base64, ${kotlin.text.String(Base64.encode(readBytes(), Base64.DEFAULT))}"

inline fun <T> Iterable<T>.findWithIndex(predicate: (T) -> Boolean): Pair<T, Int>? {
    for ((count, element) in this.withIndex()) {
        if (predicate(element))
            return element to count
    }
    return null
}

inline fun <reified T> String.fromJson(): T? = App.instance.component.enviorment().gson.fromJson(this, T::class.java)

fun Any.toJson(): String = App.instance.component.enviorment().gson.toJson(this)
