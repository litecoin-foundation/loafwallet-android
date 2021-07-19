package com.breadwallet.presenter.language

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.breadwallet.R
import com.breadwallet.entities.Language
import com.breadwallet.presenter.activities.intro.IntroActivity
import com.breadwallet.presenter.spend.RoundedBottomSheetDialogFragment
import com.breadwallet.tools.util.LocaleHelper
import kotlinx.android.synthetic.main.change_language_bottom_sheet.*


/** Litewallet
 * Created by Mohamed Barry on 7/19/21
 * email: mosadialiou@gmail.com
 * Copyright © 2021 Litecoin Foundation. All rights reserved.
 */
class ChangeLanguageBottomSheet : RoundedBottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_language_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener { dismiss() }

        val currentLanguage = LocaleHelper.instance.currentLocale
        toolbar.title = currentLanguage.desc

        val adapter = LanguageAdapter(Language.values()).apply {
            selectedPosition = currentLanguage.ordinal
            onLanguageChecked = { toolbar.title = it.desc }
        }
        recycler_view.adapter = adapter

        ok_button.setOnClickListener {
            dismiss()
            if (LocaleHelper.instance.setLocaleIfNeeded(adapter.selectedLanguage())) {
                val intent = Intent(requireContext(), IntroActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}