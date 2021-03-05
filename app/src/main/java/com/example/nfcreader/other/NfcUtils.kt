package com.example.nfcreader.other

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import java.util.*
import kotlin.experimental.and

enum class NFCType {
    UNKNOWN,
    TEXT,
    URI,
    SMART_POSTER,
    ABSOLUTE_URI
}

fun getTagType(msg: NdefMessage?): NFCType? {
    if (msg == null) return null
    for (record in msg.records) {
        if (record.tnf == NdefRecord.TNF_WELL_KNOWN) {
            if (Arrays.equals(record.type, NdefRecord.RTD_TEXT)) {
                return NFCType.TEXT
            }
            if (Arrays.equals(record.type, NdefRecord.RTD_URI)) {
                return NFCType.URI
            }
            if (Arrays.equals(record.type, NdefRecord.RTD_SMART_POSTER)) {
                return NFCType.SMART_POSTER
            }
        } else if (record.tnf == NdefRecord.TNF_ABSOLUTE_URI) {
            return NFCType.ABSOLUTE_URI
        }
    }
    return null
}

/**
 * the First Byte of the payload contains the "Status Byte Encodings" field,
 * per the NFC Forum "Text Record Type Definition" section 3.2.1.
 *
 * Bit_7 is the Text Encoding Field.
 * * if Bit_7 == 0 the the text is encoded in UTF-8
 * * else if Bit_7 == 1 then the text is encoded in UTF16
 * Bit_6 is currently always 0 (reserved for future use)
 * Bits 5 to 0 are the length of the IANA language code.
 */
fun getText(payload: ByteArray): String? {
    if (payload == null) return null
    try {
        val textEncoding = if ((payload[0] and 128.toByte()).toInt() == 0) "UTF-8" else "UTF-16"
        val languageCodeLength = (payload[0] and 63).toInt()
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            charset(textEncoding)
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

/**
 * NFC Forum "URI Record Type Definition"
 *
 *  Conversion of prefix based on section 3.2.2 of the NFC Forum URI Record
 *  Type Definition document.
 */
fun convertUriPrefix(prefix: Byte): String? {
    when (prefix) {
        0x00.toByte() -> return ""
        0x01.toByte() -> return "http://www."
        0x02.toByte() -> return "https://www."
        0x03.toByte() -> return "http://"
        0x04.toByte() -> return "https://"
        0x05.toByte() -> return "tel:"
        0x06.toByte() -> return "mailto:"
        0x07.toByte() -> return "ftp://anonymous:anonymous@"
        0x08.toByte() -> return "ftp://ftp."
        0x09.toByte() -> return "ftps://"
        0x0A.toByte() -> return "sftp://"
        0x0B.toByte() -> return "smb://"
        0x0C.toByte() -> return "nfs://"
        0x0D.toByte() -> return "ftp://"
        0x0E.toByte() -> return "dav://"
        0x0F.toByte() -> return "news:"
        0x10.toByte() -> return "telnet://"
        0x11.toByte() -> return "imap:"
        0x12.toByte() -> return "rtsp://"
        0x13.toByte() -> return "urn:"
        0x14.toByte() -> return "pop:"
        0x15.toByte() -> return "sip:"
        0x16.toByte() -> return "sips:"
        0x17.toByte() -> return "tftp:"
        0x18.toByte() -> return "btspp://"
        0x19.toByte() -> return "btl2cap://"
        0x1A.toByte() -> return "btgoep://"
        0x1B.toByte() -> return "tcpobex://"
        0x1C.toByte() -> return "irdaobex://"
        0x1D.toByte() -> return "file://"
        0x1E.toByte() -> return "urn:epc:id:"
        0x1F.toByte() -> return "urn:epc:tag:"
        0x20.toByte() -> return "urn:epc:pat:"
        0x21.toByte() -> return "urn:epc:raw:"
        0x22.toByte() -> return "urn:epc:"
        0x23.toByte() -> return "urn:nfc:"
        else -> return null
    }
}

