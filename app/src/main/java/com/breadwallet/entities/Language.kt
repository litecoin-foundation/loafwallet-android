package com.breadwallet.entities


/** Litewallet
 * Created by Mohamed Barry on 7/19/21
 * email: mosadialiou@gmail.com
 * Copyright © 2021 Litecoin Foundation. All rights reserved.
 */
enum class Language(val code: String, val title: String, val desc: String) {
    DANISH("da", "Dansk", "Vælg sprog"),
    GERMAN("de", "Deutsch", "Sprache auswählen"),
    ENGLISH("en", "English", "Select language"),
    SPANISH("es", "Español", "Seleccione el idioma"),
    FRENCH("fr", "Français", "Sélectionner la langue"),
    INDONESIAN("in", "Indonesia", "Pilih bahasa"),
    ITALIAN("it", "Italiano", "Seleziona la lingua"),
    DUTCH("nl", "Nederlands", "Selecteer taal"),
    PORTUGUESE("pt", "Português", "Selecione o idioma"),
    SWEDISH("sv", "Svenska", "Välj språk"),
    TURKISH("tr", "Türkçe", "Dil Seçin"),
    RUSSIAN("ru", "Pусский", "Выберите язык"),
    KOREAN("ko", "한국어", "언어 선택"),
    JAPANESE("ja", "日本語", "言語を選択する"),
    CHINESE_SIMPLIFIED("zh-CN", "简化字", "选择语言"),
    CHINESE_TRADITIONAL("zh-TW", "繁體字", "選擇語言");

    companion object {
        fun find(code: String?): Language = values().find { it.code == code } ?: ENGLISH
    }
}
