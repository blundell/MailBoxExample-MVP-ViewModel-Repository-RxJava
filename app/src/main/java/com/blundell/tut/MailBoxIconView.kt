package com.blundell.tut

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.mail_box_icon_view.view.*

class MailBoxIconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.mail_box_icon_view, this)
    }

    fun showMailCount(count: String) {
        mailbox_text_mail_count.visibility = View.VISIBLE
        mailbox_text_mail_count.text = count
    }

    fun hideMailCount() {
        mailbox_text_mail_count.visibility = View.INVISIBLE
        mailbox_text_mail_count.text = ""
    }

    fun showFlamingMailIcon() {
        mailbox_image_background.setImageResource(R.drawable.ic_mail_not_empty)
        mailbox_image_overload.visibility = View.VISIBLE
    }

    fun showGotMailIcon() {
        mailbox_image_background.setImageResource(R.drawable.ic_mail_not_empty)
        mailbox_image_overload.visibility = View.INVISIBLE
    }

    fun showEmptyMailIcon() {
        mailbox_image_background.setImageResource(R.drawable.ic_mail_empty)
        mailbox_image_overload.visibility = View.INVISIBLE
    }
}
