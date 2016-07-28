package com.vrv.sdk.library.common.view;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.vrv.sdk.library.R;
import com.vrv.sdk.library.utils.DateTimeUtils;


/**
 * Created by Yang on 2015/11/12 012.
 */
public class DateTimeView extends LinearLayout {
    private Context context;
    private boolean showTime;
    private long time;
    private DatePicker.OnDateChangedListener dateChangedListener;
    private TimePicker.OnTimeChangedListener timeChangedListener;

    private DatePicker datePicker;
    private TimePicker timePicker;

    public DateTimeView(Context context, long time, DatePicker.OnDateChangedListener dateChangedListener) {
        super(context);
        this.context = context;
        this.time = time;
        showTime = false;
        this.dateChangedListener = dateChangedListener;
        loadView();
    }

    public DateTimeView(Context context, long time, DatePicker.OnDateChangedListener dateChangedListener, TimePicker.OnTimeChangedListener timeChangedListener) {
        super(context);
        this.context = context;
        this.time = time;
        this.showTime = true;
        this.dateChangedListener = dateChangedListener;
        this.timeChangedListener = timeChangedListener;
        loadView();
    }

    private void loadView() {
        View view = View.inflate(context, R.layout.vim_view_datetime, this);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.setCalendarViewShown(false);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        setView();
    }

    private void setView() {
        int[] ymdhms = DateTimeUtils.getYMDHMS(time);
        if (showTime) {
            timePicker.setVisibility(VISIBLE);
//            timePicker.setHour(ymdhms[3]);
//            timePicker.setMinute(ymdhms[4]);
            if (timeChangedListener!=null){
                timePicker.setOnTimeChangedListener(timeChangedListener);
            }
        } else {
            timePicker.setVisibility(GONE);
        }
        if (dateChangedListener == null){
            dateChangedListener = new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                }
            };
        }
        datePicker.init(ymdhms[0], ymdhms[1], ymdhms[2], dateChangedListener);
    }
}
