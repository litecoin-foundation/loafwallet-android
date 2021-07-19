package com.breadwallet.presenter.language

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.breadwallet.R
import com.breadwallet.entities.Language
import kotlinx.android.synthetic.main.change_language_item.view.*


/** Litewallet
 * Created by Mohamed Barry on 7/19/21
 * email: mosadialiou@gmail.com
 * Copyright © 2021 Litecoin Foundation. All rights reserved.
 */
class LanguageAdapter(val languages: Array<Language>) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    var selectedPosition = 0

    var onLanguageChecked: ((Language) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.change_language_item, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = languages[position]
        with(holder.lang) {
            text = language.title
            isChecked = selectedPosition == position
            setOnCheckedChangeListener { radioButton, isChecked ->
                if (isChecked) {
                    onLanguageChecked?.invoke(language)
                    radioButton.post {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = position
                    }
                }
            }
        }
    }

    override fun getItemCount() = languages.size

    fun selectedLanguage() = languages[selectedPosition]

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lang: RadioButton = view.lang_radio_button
    }
}