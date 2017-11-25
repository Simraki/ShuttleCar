package ru.shuttlecar.shuttlecar.models;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import ru.shuttlecar.shuttlecar.R;

public class TimePicker implements View.OnClickListener {
    private int mMin;
    private boolean bool = false;
    private Context context;

    private ImageButton btn_done;

    private TextView tv_o_h, tv_t_h, tv_min;
    private TextView tv_00, tv_05, tv_10,
            tv_15, tv_20, tv_25,
            tv_30, tv_35, tv_40,
            tv_45, tv_50, tv_55;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tp_tv_one_hour:
            case R.id.tp_tv_two_hour:
                if (tv_o_h != null && tv_t_h != null) {
                    if (bool) {
                        tv_t_h.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_hour_noact));
                        tv_o_h.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                        bool = false;
                    } else {
                        tv_o_h.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_hour_noact));
                        tv_t_h.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                        bool = true;
                    }
                    if (String.valueOf(mMin).length() == 1) {
                        tv_min.setText("0" + String.valueOf(mMin));
                    } else {
                        tv_min.setText(String.valueOf(mMin));
                    }
                    refresh_pick_min();
                }
                break;
            case R.id.tp_btn_min:
                Integer m1 = Integer.parseInt((String) tv_min.getText());
                if (bool && m1 > 0) {
                    m1--;
                    if (m1.toString().length() == 1) {
                        tv_min.setText("0" + String.valueOf(m1));
                    } else {
                        tv_min.setText(String.valueOf(m1));
                    }
                } else if (!bool && m1 > mMin) {
                    m1--;
                    if (m1.toString().length() == 1) {
                        tv_min.setText("0" + String.valueOf(m1));
                    } else {
                        tv_min.setText(String.valueOf(m1));
                    }
                }
                break;
            case R.id.tp_btn_plus:
                Integer m2 = Integer.parseInt((String) tv_min.getText());
                if (bool && m2 < mMin) {
                    m2++;
                    if (m2.toString().length() == 1) {
                        tv_min.setText("0" + String.valueOf(m2));
                    } else {
                        tv_min.setText(String.valueOf(m2));
                    }
                } else if (!bool && m2 < 59) {
                    m2++;
                    if (m2.toString().length() == 1) {
                        tv_min.setText("0" + String.valueOf(m2));
                    } else {
                        tv_min.setText(String.valueOf(m2));
                    }
                }
                break;
            case R.id.tp_bt_00:
            case R.id.tp_bt_05:
            case R.id.tp_bt_10:
            case R.id.tp_bt_15:
            case R.id.tp_bt_20:
            case R.id.tp_bt_25:
            case R.id.tp_bt_30:
            case R.id.tp_bt_35:
            case R.id.tp_bt_40:
            case R.id.tp_bt_45:
            case R.id.tp_bt_50:
            case R.id.tp_bt_55:
                tv_min.setText(((TextView) v).getText());
                break;
        }
    }

    private void refresh_pick_min() {
        if (!bool) {
            if (mMin > 55) {
                tv_55.setClickable(false);
                tv_55.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_55.setClickable(true);
                tv_55.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 50) {
                tv_50.setClickable(false);
                tv_50.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_50.setClickable(true);
                tv_50.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 45) {
                tv_45.setClickable(false);
                tv_45.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_45.setClickable(true);
                tv_45.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 40) {
                tv_40.setClickable(false);
                tv_40.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_40.setClickable(true);
                tv_40.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 35) {
                tv_35.setClickable(false);
                tv_35.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_35.setClickable(true);
                tv_35.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 30) {
                tv_30.setClickable(false);
                tv_30.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_30.setClickable(true);
                tv_30.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 25) {
                tv_25.setClickable(false);
                tv_25.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_25.setClickable(true);
                tv_25.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 20) {
                tv_20.setClickable(false);
                tv_20.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_20.setClickable(true);
                tv_20.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 15) {
                tv_15.setClickable(false);
                tv_15.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_15.setClickable(true);
                tv_15.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 10) {
                tv_10.setClickable(false);
                tv_10.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_10.setClickable(true);
                tv_10.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 5) {
                tv_05.setClickable(false);
                tv_05.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_05.setClickable(true);
                tv_05.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin > 0) {
                tv_00.setClickable(false);
                tv_00.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_00.setClickable(true);
                tv_00.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
        } else {
            if (mMin < 55) {
                tv_55.setClickable(false);
                tv_55.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_55.setClickable(true);
                tv_55.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 50) {
                tv_50.setClickable(false);
                tv_50.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_50.setClickable(true);
                tv_50.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 45) {
                tv_45.setClickable(false);
                tv_45.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_45.setClickable(true);
                tv_45.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 40) {
                tv_40.setClickable(false);
                tv_40.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_40.setClickable(true);
                tv_40.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 35) {
                tv_35.setClickable(false);
                tv_35.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_35.setClickable(true);
                tv_35.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 30) {
                tv_30.setClickable(false);
                tv_30.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_30.setClickable(true);
                tv_30.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 25) {
                tv_25.setClickable(false);
                tv_25.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_25.setClickable(true);
                tv_25.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 20) {
                tv_20.setClickable(false);
                tv_20.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_20.setClickable(true);
                tv_20.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 15) {
                tv_15.setClickable(false);
                tv_15.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_15.setClickable(true);
                tv_15.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 10) {
                tv_10.setClickable(false);
                tv_10.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_10.setClickable(true);
                tv_10.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 5) {
                tv_05.setClickable(false);
                tv_05.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_05.setClickable(true);
                tv_05.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }
            if (mMin < 0) {
                tv_00.setClickable(false);
                tv_00.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            } else {
                tv_00.setClickable(true);
                tv_00.setTextColor(ContextCompat.getColor(context, R.color.time_picker_color_text_min_act));
            }

        }
    }

    private void refresh_hm() {
        Calendar cal = Calendar.getInstance();

        mMin = cal.get(Calendar.MINUTE);

        if (String.valueOf(mMin).length() == 1) {
            tv_min.setText("0" + String.valueOf(mMin));
        } else {
            tv_min.setText(String.valueOf(cal.get(Calendar.MINUTE)));
        }

        tv_o_h.setText(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
        cal.add(Calendar.HOUR_OF_DAY, 1);
        tv_t_h.setText(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
    }


    public Dialog get(Context context, LayoutInflater inflater) {
        this.context = context;
        final Handler handler = new Handler();

        View view = inflater.inflate(R.layout.dialog_time_picker, null);

        Dialog dialog = new Dialog(context, R.style.TimePickerDialog);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        tv_o_h = (TextView) view.findViewById(R.id.tp_tv_one_hour);
        tv_t_h = (TextView) view.findViewById(R.id.tp_tv_two_hour);
        tv_min = (TextView) view.findViewById(R.id.tp_tv_min);

        refresh_hm();

        ImageButton btn_m = (ImageButton) view.findViewById(R.id.tp_btn_min);
        ImageButton btn_p = (ImageButton) view.findViewById(R.id.tp_btn_plus);
        btn_done = (ImageButton) view.findViewById(R.id.tp_btn_done);


        tv_t_h.setOnClickListener(this);
        tv_o_h.setOnClickListener(this);
        btn_m.setOnClickListener(this);
        btn_p.setOnClickListener(this);

        tv_00 = (TextView) view.findViewById(R.id.tp_bt_00);
        tv_05 = (TextView) view.findViewById(R.id.tp_bt_05);
        tv_10 = (TextView) view.findViewById(R.id.tp_bt_10);
        tv_15 = (TextView) view.findViewById(R.id.tp_bt_15);
        tv_20 = (TextView) view.findViewById(R.id.tp_bt_20);
        tv_25 = (TextView) view.findViewById(R.id.tp_bt_25);
        tv_30 = (TextView) view.findViewById(R.id.tp_bt_30);
        tv_35 = (TextView) view.findViewById(R.id.tp_bt_35);
        tv_40 = (TextView) view.findViewById(R.id.tp_bt_40);
        tv_45 = (TextView) view.findViewById(R.id.tp_bt_45);
        tv_50 = (TextView) view.findViewById(R.id.tp_bt_50);
        tv_55 = (TextView) view.findViewById(R.id.tp_bt_55);

        tv_00.setOnClickListener(this);
        tv_05.setOnClickListener(this);
        tv_10.setOnClickListener(this);
        tv_15.setOnClickListener(this);
        tv_20.setOnClickListener(this);
        tv_25.setOnClickListener(this);
        tv_30.setOnClickListener(this);
        tv_35.setOnClickListener(this);
        tv_40.setOnClickListener(this);
        tv_45.setOnClickListener(this);
        tv_50.setOnClickListener(this);
        tv_55.setOnClickListener(this);

        refresh_pick_min();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                refresh_hm();
                handler.postDelayed(this, 150000);
            }
        };
        handler.postDelayed(runnable, 150000);
        return dialog;
    }

    public String getTime() {
        String str;
        if (bool) {
            str = tv_t_h.getText().toString();
        } else {
            str = tv_o_h.getText().toString();
        }
        str = str + ":" + tv_min.getText().toString();
        return str;
    }

    public void setListener(View.OnClickListener listener) {
        btn_done.setOnClickListener(listener);
    }
}
