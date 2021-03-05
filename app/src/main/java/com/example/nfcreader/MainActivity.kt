package com.example.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nfcreader.parser.NdefMessageParser.parse


class MainActivity : AppCompatActivity() {

    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish()
        }
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )
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
        val tag: Tag? = intent!!.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?
        Toast.makeText(this, tag.toString(), Toast.LENGTH_LONG).show()
        val msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        val nmsgs = arrayOfNulls<NdefMessage>(
            msgs!!.size
        )
        for (i in msgs.indices) {
            nmsgs[i] = msgs[i] as NdefMessage
            Log.println(Log.DEBUG, "NdefMessage", nmsgs[i].toString())
        }
    }
}