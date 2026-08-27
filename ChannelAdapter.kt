package com.iptv.mv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChannelAdapter(
    private val channels: List<Channel>,
    private val onClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    class ChannelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvChannelName)
        val tvGroup: TextView = view.findViewById(R.id.tvChannelGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.tvName.text = channel.name
        holder.tvGroup.text = channel.groupTitle.ifEmpty { "Geral" }
        holder.itemView.isFocusable = true
        holder.itemView.isFocusableInTouchMode = true
        holder.itemView.setOnClickListener { onClick(channel) }
    }

    override fun getItemCount(): Int = channels.size
}
