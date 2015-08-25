package com.nil.exactap;

import android.os.Debug;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private EditText editAP;
    private TextView multiCFCount;
    private TextView CFCount;
    private TextView captureCount;
    private TextView completeCount;
    private TextView linkCount;
    private TextView resmodCount;
    private TextView hackCount;
    private TextView upgradeCount;
    private TextView rechargeCount;

    private int AP;
    private int multiCF;
    private int CF;
    private int capture;
    private int complete;
    private int link;
    private int resmod;
    private int hack;
    private int upgrade;
    private int recharge;
    private int[] Price = new int[]{2813, 1563, 625, 375, 313, 125, 100, 65, 10};
    private int[] Count = new int[9];
    private boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editAP = (EditText) findViewById(R.id.editAP);
        multiCFCount = (TextView) findViewById(R.id.countMultiCF);
        CFCount = (TextView) findViewById(R.id.countCF);
        captureCount = (TextView) findViewById(R.id.countCapture);
        completeCount = (TextView) findViewById(R.id.countComplete);
        linkCount = (TextView) findViewById(R.id.countLink);
        resmodCount = (TextView) findViewById(R.id.countResMod);
        hackCount = (TextView) findViewById(R.id.countHack);
        upgradeCount = (TextView) findViewById(R.id.countUpgrade);
        rechargeCount = (TextView) findViewById(R.id.countRecharge);

        setText();

        editAP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (editAP.getText().toString().length() < 1) {
                    Count = new int[9];
                    setText();
                    return;
                }

                AP = Integer.parseInt(editAP.getText().toString());
                found = false;
                if (AP >= 0) {
                    Count = new int[9];
                    getSolution(AP, 0);
                }

                if (!found) {
                    for (int i = 0; i < Count.length; i++)
                        Count[i] = -1;
                }

                //for (int i = 0; i < Count.length; i++)
                    //Log.v("C", Count[i] + "");

                setText();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void setText() {
        multiCFCount.setText(Price[0] + "AP x " + Count[0]);
        CFCount.setText(Price[1] + "AP x " + Count[1]);
        captureCount.setText(Price[2] + "AP x " + Count[2]);
        completeCount.setText(Price[3] + "AP x " + Count[3]);
        linkCount.setText(Price[4] + "AP x " + Count[4]);
        resmodCount.setText(Price[5] + "AP x " + Count[5]);
        hackCount.setText(Price[6] + "AP x " + Count[6]);
        upgradeCount.setText(Price[7] + "AP x " + Count[7]);
        rechargeCount.setText(Price[8] + "AP x " + Count[8]);
    }

    private void getSolution(int left, int pos) {
        if (found)
            return;
        if (left == 0) {
            found = true;
            //Log.v("Right?", isRight() + "");
            //for (int i = 0; i < Count.length; i++)
                //Log.v("C", Count[i] + "");
            return;
        }

        if (pos == 8 && left % Price[pos] != 0) {
            return;
        }

        if (pos <= 8) {
            for (int i = left % Price[pos]; i <= left && !found; i += Price[pos]) {
                Count[pos] = (left - i) / Price[pos];
                getSolution(i, pos + 1);
            }
        }
    }

    private boolean isRight() {
        int sum = 0;
        for (int i = 0; i < Count.length; i++)
            sum += Count[i] * Price[i];
        //Log.v("Count", Count.toString());
        if (sum == AP)
            return true;
        else
            return false;
    }
}
