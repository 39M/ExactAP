package com.m39.exactap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private EditText current_ap_text;
    private EditText target_ap_text;
    private TextView multi_cf_count;
    private TextView cf_count;
    private TextView capture_count;
    private TextView complete_count;
    private TextView link_count;
    private TextView deploy_count;
    private TextView hack_count;
    private TextView upgrade_count;
    private TextView recharge_count;

    private int ap_difference;
    private int[] ap_obtains = new int[]{2813, 1563, 625, 375, 313, 125, 100, 65, 10};
    private int[] op_counters = new int[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get views
        current_ap_text = (EditText) findViewById(R.id.current_ap_input);
        target_ap_text = (EditText) findViewById(R.id.target_ap_input);

        multi_cf_count = (TextView) findViewById(R.id.multi_cf_count_label);
        cf_count = (TextView) findViewById(R.id.cf_count_label);
        capture_count = (TextView) findViewById(R.id.capture_count_label);
        complete_count = (TextView) findViewById(R.id.complete_count_label);
        link_count = (TextView) findViewById(R.id.link_count_label);
        deploy_count = (TextView) findViewById(R.id.deploy_count_label);
        hack_count = (TextView) findViewById(R.id.hack_count_label);
        upgrade_count = (TextView) findViewById(R.id.upgrade_count_label);
        recharge_count = (TextView) findViewById(R.id.recharge_count_label);

        // init UI
        set_text();

        // create text watcher
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                update_solution();
            }
        };
        // add text watcher for edit text
        current_ap_text.addTextChangedListener(textWatcher);
        target_ap_text.addTextChangedListener(textWatcher);

        // clear focus when action done
//        target_ap_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    target_ap_text.clearFocus();
//                }
//                return false;
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // when press back button, move app to background
        moveTaskToBack(true);
    }

    private void update_solution() {
        // empty input, reset counters
        if (current_ap_text.getText().toString().length() < 1 || target_ap_text.getText().toString().length() < 1) {
            Arrays.fill(op_counters, 0);
            set_text();
            return;
        }

        // get ap difference
        ap_difference = Integer.parseInt(target_ap_text.getText().toString()) -
                Integer.parseInt(current_ap_text.getText().toString());
        // reset conters
        Arrays.fill(op_counters, 0);
        // try to get a solution
        if (ap_difference < 0 || !get_solution(ap_difference, 0)) {
            // if faild, set counters to -1
            Arrays.fill(op_counters, -1);
        }
        /*********/
        else if (!is_right_answer())
            Log.w("Warning", "Wrong answer");
        /*********/

        // update UI
        set_text();
    }

    private void set_text() {
        multi_cf_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[0], op_counters[0]));
        cf_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[1], op_counters[1]));
        capture_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[2], op_counters[2]));
        complete_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[3], op_counters[3]));
        link_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[4], op_counters[4]));
        deploy_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[5], op_counters[5]));
        hack_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[6], op_counters[6]));
        upgrade_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[7], op_counters[7]));
        recharge_count.setText(String.format(getString(R.string.ap_multiply), ap_obtains[8], op_counters[8]));
    }

    private boolean get_solution(int ap_left, int pos) {
        // got solution, return
        if (ap_left == 0) {
            return true;
        }

        // failed to get solution
        if (pos == ap_obtains.length - 1 && ap_left % ap_obtains[pos] != 0) {
            return false;
        }

        // dfs to get solution
        if (pos <= 8) {
            for (int i = ap_left % ap_obtains[pos]; i <= ap_left; i += ap_obtains[pos]) {
                op_counters[pos] = (ap_left - i) / ap_obtains[pos];
                if (get_solution(i, pos + 1))
                    return true;
            }
        }
        return false;
    }

    private boolean is_right_answer() {
        int sum = 0;
        // check answer
        for (int i = 0; i < op_counters.length; i++)
            sum += op_counters[i] * ap_obtains[i];
        return sum == ap_difference;
    }
}
