package com.example.nfcreader.parser

import android.nfc.NdefRecord
import com.google.common.base.Preconditions
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.experimental.and


class TextRecord(languageCode: String, text: String) : ParsedNdefRecord {
    /**
     * Returns the ISO/IANA language code associated with this text element.
     */
    /** ISO/IANA language code  */
    val languageCode: String
    val text: String

    override fun str(): String {
        return text
    }

    companion object {
        // TODO: deal with text fields which span multiple NdefRecords
        fun parse(record: NdefRecord): TextRecord {
            Preconditions.checkArgument(record.tnf == NdefRecord.TNF_WELL_KNOWN)
            Preconditions.checkArgument(Arrays.equals(record.type, NdefRecord.RTD_TEXT))
            return try {
                val payload = record.payload
                val textEncoding =
                    if ((payload[0] and 128.toByte()).toInt() == 0) "UTF-8" else "UTF-16"
                val languageCodeLength: Int = (payload[0] and 63).toInt()
                val languageCode = String(payload, 1, languageCodeLength, charset("US-ASCII"))
                val text = String(
                    payload, languageCodeLength + 1,
                    payload.size - languageCodeLength - 1, charset(textEncoding)
                )
                TextRecord(languageCode, text)
            } catch (e: UnsupportedEncodingException) {
                // should never happen unless we get a malformed tag.
                throw IllegalArgumentException(e)
            }
        }

        fun isText(record: NdefRecord): Boolean {
            return try {
                parse(record)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }
    }

    init {
        this.languageCode = Preconditions.checkNotNull(languageCode)
        this.text = Preconditions.checkNotNull(text)
    }
}