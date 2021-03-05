package com.example.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val string =
            "010200801E62DA2171D056490931B443EE71D78FF12A9C6321E820AE7778C5557ABFA71B884B6AF13A2F84FD5A1F92E1E910D22815D2E80821F6DFEF7FFEED67AF5AA80499C2926396569CDF0264C69EB1E530F8F96E278EC1A57E7570F853F7575440C93960329FD66BE4DA08DDC8BE290B26D09B82E414C9D0079162A66504FF384F34800025687474703A2F2F70696C65742E65652F6372742F33303836343930302D303030312E637274"
        val ndefRecord = NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            hexStringToByteArray("536967"),
            null,
            hexStringToByteArray(string)
        )
        Log.println(Log.DEBUG, "NdefMessageRecord", ndefRecord.toString())
        Log.println(Log.DEBUG, "NdefMessageRecord", ndefRecord.tnf.toString())
        Log.println(Log.DEBUG, "NdefMessageRecord", String(ndefRecord.type))
        Log.println(Log.DEBUG, "NdefMessageRecord", String(ndefRecord.id))
        Log.println(Log.DEBUG, "NdefMessageRecord", String(ndefRecord.payload))
        Log.println(
            Log.DEBUG,
            "NdefMessageRecord",
            com.example.nfcreader.other.getText(ndefRecord.payload).toString()
        )

//        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
//
//        if (nfcAdapter == null) {
//            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//
//        pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
//            0
//        )
    }

    private fun hexStringToByteArray(s: String): ByteArray? {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    override fun onResume() {
        super.onResume()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    override fun onPause() {
        super.onPause()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val action = getIntent().action
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val messages = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (messages != null) {
                for (eachMessage in messages) {
                    val message = eachMessage as NdefMessage
                    val records = message.records
                    if (records != null) {
                        for (record in records) {
                            val payload = String(record.payload)
                            Log.d("NDEFPAYLOAD", payload)
                        }
                    }
                }
            }
        }

        val tag: Tag? = intent!!.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?
        Toast.makeText(this, tag.toString(), Toast.LENGTH_LONG).show()
        val msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        Log.println(Log.DEBUG, "NdefMessageSize", msgs?.size.toString())
        val nmsgs = arrayOfNulls<NdefMessage>(
            msgs!!.size
        )
        for (i in msgs.indices) {
            nmsgs[i] = msgs[i] as NdefMessage
        }
        for (nm in nmsgs) {
            nm!!.records.forEach {
                Log.println(
                    Log.DEBUG, "NdefMessageRecord", String(
                        it.payload.copyOfRange(
                            1,
                            it.payload.size
                        ), charset("UTF-8")
                    )
                )

            }
        }
    }
}