package com.alexandr_yurchenko.radiobeach.alarm.alarmslist;

import android.icu.text.DateFormat;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.alexandr_yurchenko.radiobeach.R;
import com.alexandr_yurchenko.radiobeach.alarm.data.Alarm;

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    private TextView alarmTime;

    private TextView alarmRecurringDays;
    private TextView alarmTitle;

    Button alarmStarted;

    private OnToggleAlarmListener listener;

    public AlarmViewHolder(@NonNull View itemView, OnToggleAlarmListener listener) {
        super(itemView);

        alarmTime = itemView.findViewById(R.id.item_alarm_time);
        alarmStarted = itemView.findViewById(R.id.item_alarm_started);

        alarmRecurringDays = itemView.findViewById(R.id.item_alarm_recurringDays);
        alarmTitle = itemView.findViewById(R.id.item_alarm_title);

        this.listener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void bind(Alarm alarm) {
        String alarmText = String.format("%02d:%02d", alarm.getHour(), alarm.getMinute());

        alarmTime.setText(alarmText);
        alarmStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.isStarted();
            }
        });

        if (alarm.isRecurring()) {
            alarmRecurringDays.setText(alarm.getRecurringDaysText());
        } else {
            alarmRecurringDays.setText("Once Off");
        }

        String created = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(alarm.getCreated());

        if (alarm.getTitle().length() != 0) {
            alarmTitle.setText(String.format("%s | %d | %s", alarm.getTitle(), alarm.getAlarmId(), created));
        } else {
            alarmTitle.setText(String.format("%s | %d | %s", "Alarm", alarm.getAlarmId(), created));
        }

        alarmStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onToggle(alarm);
            }
        });
    }
}
