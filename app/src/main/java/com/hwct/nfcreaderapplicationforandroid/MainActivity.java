package com.hwct.nfcreaderapplicationforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter == null){
            Toast.makeText(this, "NFC not supported by device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }
    @Override
    protected void onResume(){
        super.onResume();

        if(nfcAdapter != null){
           if(!nfcAdapter.isEnabled()){
               Toast.makeText(this, "NFC off", Toast.LENGTH_SHORT).show();
               showWirelessSettings();
               nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

           }
        }
    }

    private void showWirelessSettings(){
        Toast.makeText(this, "Please enable NFC", Toast.LENGTH_SHORT).show();
        //falls Fail, dann hier statt NFC_settings einfach Wireless_Settings
        //Hier wird User in das Einstellungsmenü geführt
        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(nfcAdapter != null){
            nfcAdapter.disableForegroundDispatch(this); //programm wird angehalten
        }

    }

    // Will be called when NFC Tag is detected
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
        || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            Parcelable [] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage [] msgs;

            if( rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];

                for(int i=0; i < msgs.length; i++){
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            }else {
                byte [] empty = new byte[0];
                byte [] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }

            displayMsgs(msgs);
        }


    }

    private void displayMsgs(NdefMessage[] msgs) {
        if(msgs == null || msgs.length == 0){
            return;
        }
        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);


    }

    private String dumpTagData(Tag tag) {

    }
}
