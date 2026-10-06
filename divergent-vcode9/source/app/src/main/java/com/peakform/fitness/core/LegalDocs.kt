package com.peakform.fitness.core

import android.content.Context
import com.peakform.fitness.ProLog
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Legal documents — legacy P4_LEGAL_DOCS verbatim (13 docs, sections/paragraphs/lists). */
object LegalDocs {
    data class Section(val h: String, val p: List<String>, val ul: List<String>)
    data class Doc(val id: String, val short: String, val title: String, val sections: List<Section>)

    private var docs: List<Doc> = emptyList()
    private var loaded = false

    fun load(context: Context): List<Doc> {
        if (loaded) return docs
        try {
            val raw = context.assets.open("data/legal.json").bufferedReader().use { it.readText() }
            val arr = Json.parseToJsonElement(raw).jsonArray
            docs = arr.map { el ->
                val o = el.jsonObject
                Doc(
                    id = o["id"]!!.jsonPrimitive.content,
                    short = o["short"]?.jsonPrimitive?.contentOrNull ?: o["id"]!!.jsonPrimitive.content,
                    title = o["title"]!!.jsonPrimitive.content,
                    sections = o["sections"]!!.jsonArray.map { s ->
                        val so = s.jsonObject
                        Section(
                            h = so["h"]?.jsonPrimitive?.contentOrNull ?: "",
                            p = so["p"]?.jsonArray?.mapNotNull { (it as? kotlinx.serialization.json.JsonPrimitive)?.contentOrNull } ?: emptyList(),
                            ul = so["ul"]?.jsonArray?.mapNotNull { (it as? kotlinx.serialization.json.JsonPrimitive)?.contentOrNull } ?: emptyList(),
                        )
                    },
                )
            }
        } catch (e: Exception) {
            ProLog.e("LEGAL", "load failed: ${e.message}")
        }
        loaded = true
        return docs
    }

    fun count(context: Context): Int = load(context).size
}
