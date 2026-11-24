package top.trumeet.ui.theme

import android.content.Context

enum class UiStyle(val key: String, val label: String) {
    MIUIX("miuix", "Miuix"),
    MATERIAL("material", "Material");

    companion object {
        private const val PREF = "ui_prefs"
        private const val KEY = "UiStyle"

        fun fromKey(key: String?): UiStyle =
            values().firstOrNull { it.key == key } ?: MIUIX

        fun current(context: Context): UiStyle {
            val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            return fromKey(sp.getString(KEY, MIUIX.key))
        }

        fun save(context: Context, style: UiStyle) {
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY, style.key)
                .apply()
        }
    }
}
