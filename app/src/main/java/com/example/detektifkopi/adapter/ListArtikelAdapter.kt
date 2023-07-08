package com.example.detektifkopi.adapter

import android.text.DynamicLayout
import android.text.Layout
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.detektifkopi.R
import com.example.detektifkopi.data.Artikel

class ListArtikelAdapter(private val listArtikel: List<Artikel>) : RecyclerView.Adapter<ListArtikelAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_artikel)
        val tvJudul: TextView = itemView.findViewById(R.id.judul_artikel)
        val tvSub: TextView = itemView.findViewById(R.id.sub_artikel)

        fun bind(item: Artikel) {
            imgPhoto.setImageResource(item.photo)
            tvJudul.text = limitTextLength(item.judul, 30)
            tvSub.text = limitTextLengthWithTwoLines(item.sub_judul, 64)
        }

        private fun limitTextLengthWithTwoLines(text: String, maxLength: Int): String {
            val truncatedText = if (text.length > maxLength) {
                text.substring(0, maxLength - 3) + "..."
            } else {
                text
            }

            tvSub.maxLines = 2
            tvSub.text = truncatedText
            return truncatedText
        }

        private fun limitTextLength(text: String, maxLength: Int): String {
            return if (text.length > maxLength) {
                text.substring(0, maxLength - 3) + "..."
            } else {
                text
            }
        }

    }

    fun setOnItemClickCallback(callback: OnItemClickCallback) {
        onItemClickCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_artikel, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listArtikel.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = listArtikel[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(item)
        }
    }

    private var itemClickListener: AdapterView.OnItemClickListener? = null


    interface OnItemClickCallback {
        fun onItemClicked(data: Artikel)
    }
}
