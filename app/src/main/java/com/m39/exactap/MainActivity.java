package com.m39.exactap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

// TODO: Layout fix
// TODO: More operation
// TODO: Beautify, list? card view?

public class MainActivity extends AppCompatActivity {
    private static final int op_num = 9;

    private EditText current_ap_text;
    private EditText target_ap_text;
    private TextView[] count_views = new TextView[op_num];

    Drawable lock_icon;

    private int ap_difference;
    private String[] op_names;
    private String[] op_tips;
    private int[] ap_obtains = new int[]{2813, 1563, 625, 375, 313, 125, 100, 65, 10};
    private int[] op_counters = new int[op_num];
    private boolean[] op_lock = new boolean[op_num];

    private boolean have_solution = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get views
        current_ap_text = (EditText) findViewById(R.id.current_ap_input);
        target_ap_text = (EditText) findViewById(R.id.target_ap_input);

        count_views[0] = (TextView) findViewById(R.id.multi_cf_count_label);
        count_views[1] = (TextView) findViewById(R.id.cf_count_label);
        count_views[2] = (TextView) findViewById(R.id.capture_count_label);
        count_views[3] = (TextView) findViewById(R.id.complete_count_label);
        count_views[4] = (TextView) findViewById(R.id.link_count_label);
        count_views[5] = (TextView) findViewById(R.id.deploy_count_label);
        count_views[6] = (TextView) findViewById(R.id.hack_count_label);
        count_views[7] = (TextView) findViewById(R.id.upgrade_count_label);
        count_views[8] = (TextView) findViewById(R.id.recharge_count_label);

        for (int i = 0; i < op_num; i++) {
            count_views[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // get view id
                    int view_index = get_view_index(view);
                    if (view_index == -1) {
                        // TODO: Remove line below before final build
                        Log.w("View not found", "View ID: " + view.getId());
                        return true;
                    }

                    if (have_solution && op_counters[view_index] > 0) {
                        op_counters[view_index]--;
                        current_ap_text.setText(String.valueOf(Integer.parseInt(current_ap_text.getText().toString()) + ap_obtains[view_index]));
                        update_solution();
                        Toast.makeText(MainActivity.this, String.format(getString(R.string.op_consumed),
                                op_tips[view_index], ap_obtains[view_index]), Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
            });
        }
        // get drawable
        lock_icon = ContextCompat.getDrawable(this, R.drawable.ic_lock);
        lock_icon.setBounds(0, 0, lock_icon.getMinimumWidth(), lock_icon.getMinimumHeight());

        // get op names and tips
        op_names = getResources().getStringArray(R.array.operation_names);
        op_tips = getResources().getStringArray(R.array.operation_tips);

        // init UI
        fill_counters_to(-1);
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
        // add text watcher for text editing
        current_ap_text.addTextChangedListener(textWatcher);
        target_ap_text.addTextChangedListener(textWatcher);

        // TODO: clear focus when action done
//        target_ap_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    target_ap_text.clearFocus();
//                }
//                return false;
//            }
//        });

        // Tips
        if ((new Random()).nextFloat() < 0.75)
            Toast.makeText(this, R.string.help_info1, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, R.string.help_info2, Toast.LENGTH_LONG).show();
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

    public void pick_number(final View view) {
        // get view id
        final AtomicInteger view_index = new AtomicInteger(get_view_index(view));

        if (view_index.get() == -1) {
            // TODO: Remove line below before final build
            Log.w("View not found", "View ID: " + view.getId());
            return;
        }

        // create dialog
        final AppCompatDialog dialog = new AppCompatDialog(this, R.style.DialogTheme);
        dialog.setTitle(op_names[view_index.get()]);
        dialog.setContentView(R.layout.number_picker_dialog);

        // picker setup
        final NumberPicker picker = (NumberPicker) dialog.findViewById(R.id.number_picker);
        picker.setMinValue(0);
        // prevent max value < min value
        picker.setMaxValue(ap_difference > 0 ? ap_difference / ap_obtains[view_index.get()] : 0);
        picker.setValue(op_counters[view_index.get()] != -1 ? op_counters[view_index.get()] : 0);
//        picker.setWrapSelectorWheel(false);
//        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                Log.v(oldVal+"old", newVal+"new");
//            }
//        });

        // buttons setup
        Button lock_button = (Button) dialog.findViewById(R.id.pick_number_button);
        Button unlock_button = (Button) dialog.findViewById(R.id.reset_number_button);
        Button cancel_button = (Button) dialog.findViewById(R.id.cancel_pick_number_button);
        // lock button on click listener
        lock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // apply input immediately
                picker.clearFocus();
                // lock value
                op_lock[view_index.get()] = true;
                op_counters[view_index.get()] = picker.getValue();

                update_solution();
                dialog.dismiss();
            }
        });
        // show or not show unlock button
        if (!op_lock[view_index.get()])
            // remove unlock button
            ((ViewGroup) unlock_button.getParent()).removeView(unlock_button);
        else
            // unlock button on click listener
            unlock_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // unlock value
                    op_lock[view_index.get()] = false;

                    update_solution();
                    dialog.dismiss();
                }
            });
        // cancel button on click listener
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void show_tips(final View view) {
        int index;
        String tip;
        switch (view.getId()) {
            case R.id.multi_cf_label:
                index = 0;
                tip = getString(R.string.tip_multilayer);
                break;
            case R.id.cf_label:
                index = 1;
                tip = getString(R.string.tip_control_field);
                break;
            case R.id.capture_label:
                index = 2;
                tip = getString(R.string.tip_capture);
                break;
            case R.id.complete_label:
                index = 3;
                tip = getString(R.string.tip_complete);
                break;
            case R.id.link_lable:
                index = 4;
                tip = getString(R.string.tip_link);
                break;
            case R.id.deploy_label:
                index = 5;
                tip = getString(R.string.tip_deploy);
                break;
            case R.id.hack_label:
                index = 6;
                tip = getString(R.string.tip_hack);
                break;
            case R.id.upgrade_label:
                index = 7;
                tip = getString(R.string.tip_upgrade);
                break;
            case R.id.recharge_label:
                index = 8;
                tip = getString(R.string.tip_recharge);
                break;
            default:
                return;
        }

        Toast.makeText(this, String.format(getString(R.string.op_tips), tip, ap_obtains[index]), Toast.LENGTH_SHORT).show();
    }

    private void show_secret_tips(int ap) {
        String tip;
        switch (ap) {
            case 39:
                tip = getString(R.string.secret_tip_39);
                break;
            case 42:
                tip = getString(R.string.secret_tip_42);
                break;
            case 75:
                tip = getString(R.string.secret_tip_75);
                break;
            case 105:
                tip = getString(R.string.secret_tip_105);
                break;
            case 500:
                tip = getString(R.string.secret_tip_500);
                break;
            case 608:
                tip = getString(R.string.secret_tip_608);
                break;
            case 777:
                tip = getString(R.string.secret_tip_777);
                break;
            case 831:
                tip = getString(R.string.secret_tip_831);
                break;
            case 1000:
                tip = getString(R.string.secret_tip_1000);
                break;
            case 1024:
                tip = getString(R.string.secret_tip_1024);
                break;
            case 2333:
                tip = getString(R.string.secret_tip_2333);
                break;
            case 65536:
                tip = getString(R.string.secret_tip_65536);
                break;
            default:
                return;
        }
        Toast.makeText(this, tip, Toast.LENGTH_LONG).show();
    }

    private void set_text() {
        for (int i = 0; i < op_num; i++) {
            // set text to counter value or '?'
            count_views[i].setText(String.format(getString(R.string.ap_multiply), op_counters[i] != -1 ? op_counters[i] + "" : "?"));

            if (!op_lock[i])
                // remove lock drawable
                count_views[i].setCompoundDrawables(null, null, null, null);
            else
                // set lock drawable
                count_views[i].setCompoundDrawables(null, null, lock_icon, null);
        }

    }

    // update solution and UI
    private void update_solution() {
        // reset flag
        have_solution = false;

        // empty input, reset counters
        if (current_ap_text.getText().toString().length() < 1 || target_ap_text.getText().toString().length() < 1) {
            ap_difference = 0;
            fill_counters_to(-1);
            set_text();
            return;
        }

        // get ap difference
        ap_difference = Integer.parseInt(target_ap_text.getText().toString()) -
                Integer.parseInt(current_ap_text.getText().toString());
        // !@#$%^&*()_+
        show_secret_tips(ap_difference);
        // reset conters
        fill_counters_to(0);
        // get ap differece left after substract locked operations
        int ap_left = ap_difference;
        for (int i = 0; i < op_num; i++)
            if (op_lock[i])
                ap_left -= op_counters[i] * ap_obtains[i];
        // try to get a solution
        if (ap_left < 0 || !search_solution(ap_left, 0)) {
            // if faild, set counters to -1
            fill_counters_to(-1);
        }
        /*TODO: Remove this part before final build*/
        else if (!is_right_answer())
            Log.w("Warning", "Wrong answer");
        /*TODO: Remove this part before final build*/

        have_solution = true;

        // update UI
        set_text();
    }

    // search for solution
    private boolean search_solution(int ap_left, int op_index) {
        // got solution, return
        if (ap_left == 0) {
            return true;
        }

        // failed to get solution
        if (op_index == op_num - 1 && ap_left % ap_obtains[op_index] != 0) {
            return false;
        }

        // dfs to get solution
        if (op_index < op_num) {
            if (op_lock[op_index])
                return search_solution(ap_left, op_index + 1);
            for (int i = ap_left % ap_obtains[op_index]; i <= ap_left; i += ap_obtains[op_index]) {
                op_counters[op_index] = (ap_left - i) / ap_obtains[op_index];
                if (search_solution(i, op_index + 1))
                    return true;
            }
        }
        return false;
    }

    private int get_view_index(View view) {
        int view_index = -1;
        for (int i = 0; i < op_num; i++)
            if (count_views[i].getId() == view.getId()) {
                view_index = i;
                break;
            }
        return view_index;
    }

    // assign all unlocked counters with x
    private void fill_counters_to(int x) {
        for (int i = 0; i < op_num; i++)
            if (!op_lock[i])
                op_counters[i] = x;
    }

    // check answer
    private boolean is_right_answer() {
        int sum = 0;
        for (int i = 0; i < op_num; i++)
            sum += op_counters[i] * ap_obtains[i];
        return sum == ap_difference;
    }
}
