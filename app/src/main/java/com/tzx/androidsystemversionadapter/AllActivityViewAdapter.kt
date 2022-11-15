package com.tzx.androidsystemversionadapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tzx.androidq.ScopedStorageActivity
import com.tzx.androidq.StorageAccessFrameworkActivity

/**
 * Created by Tanzhenxing
 * Date: 2019-10-22 09:05
 * Description: 主页面recyclerview的适配器
 */
class AllActivityViewAdapter(private val mContext: Context) : RecyclerView.Adapter<AllActivityViewAdapter.NormalTextViewHolder?>() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    private val activityModels: Array<ActivityModel> = arrayOf(
        ActivityModel("StorageAccessFramework", StorageAccessFrameworkActivity::class.java),
        ActivityModel("ScopedStorage", ScopedStorageActivity::class.java)
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalTextViewHolder {
        return NormalTextViewHolder(mLayoutInflater.inflate(R.layout.main_item_text, parent, false))
    }

    override fun onBindViewHolder(holder: NormalTextViewHolder, position: Int) {
        val activityModel = activityModels[position]
        val title = if (TextUtils.isDigitsOnly(activityModel.title)) activityModel.activityClass.simpleName else activityModel.title
        holder.mTextView.text = title
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, activityModels[position].activityClass)
            mContext.startActivity(intent)
        }
    }

    class NormalTextViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var mTextView: TextView = view.findViewById(R.id.text_view)

    }

    class ActivityModel(val title: String?, val activityClass: Class<out Activity?>)

    override fun getItemCount() = activityModels.size
}