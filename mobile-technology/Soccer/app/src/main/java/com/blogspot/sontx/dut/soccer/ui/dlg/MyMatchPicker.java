package com.blogspot.sontx.dut.soccer.ui.dlg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.ArrayAdapter;

import com.blogspot.sontx.dut.soccer.App;
import com.blogspot.sontx.dut.soccer.bean.Field;
import com.blogspot.sontx.dut.soccer.bean.Match;
import com.blogspot.sontx.dut.soccer.bo.DatabaseManager;
import com.blogspot.sontx.dut.soccer.utils.DateTime;

import java.util.List;

/**
 * Copyright NoEm 2016
 * Created by Noem on 14/5/2016.
 */
public class MyMatchPicker implements DialogInterface.OnClickListener {
    private AlertDialog.Builder builder;
    private ArrayAdapter<Match> matchAdapter;
    private OnSendSMSListener mOnSendSMSListener = null;

    public void setOnSendSMSListener(OnSendSMSListener listener) {
        mOnSendSMSListener = listener;
    }

    public MyMatchPicker(Context context) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Select your match to send");
        builder.setNegativeButton("Cancel", this);
        loadMatches();
    }

    public void show() {
        builder.show();
    }

    private void loadMatches() {
        matchAdapter = new ArrayAdapter<>(builder.getContext(), android.R.layout.select_dialog_singlechoice);
        List<Match> matches = DatabaseManager.getInstance().getMatchesByAccountId(App.getInstance().getCurrentAccountId());
        for (Match match : matches) {
            matchAdapter.add(match);
        }
        builder.setAdapter(matchAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Match match = matchAdapter.getItem(which);
                dialog.dismiss();
                sendSMS(match);
            }
        });
    }

    private void sendSMS(Match match) {
        Uri uri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        Field field = DatabaseManager.getInstance().getField(match.getFieldId());
        String content = String.format("I just created a match at %s at %s, come join with me :D", field.getName(), DateTime.getFriendlyString(match.getStartTime()));
        intent.putExtra("sms_body", content);
        if (mOnSendSMSListener != null)
            mOnSendSMSListener.onSendSMS(intent);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    public interface OnSendSMSListener {
        void onSendSMS(Intent intent);
    }
}
