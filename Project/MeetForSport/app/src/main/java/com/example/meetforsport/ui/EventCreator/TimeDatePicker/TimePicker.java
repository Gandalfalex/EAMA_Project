package com.example.meetforsport.ui.EventCreator.TimeDatePicker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimePicker implements  View.OnFocusChangeListener,  View.OnClickListener, TimePickerDialog.OnTimeSetListener{

    public EditText text;
    public String format;
    private Calendar mCalendar;

    private SimpleDateFormat mFormat;

    public TimePicker(EditText textField, String format){
        this.text = textField;
        this.format = format;
        this.text.setOnClickListener(this);
        this.text.setOnFocusChangeListener(this);
        mFormat = new SimpleDateFormat(format, Locale.getDefault());
    }


    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int i, int i1) {
        mCalendar.set(Calendar.HOUR, i);
        mCalendar.set(Calendar.MINUTE, i1);
        this.text.setText(mFormat.format(mCalendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        showCalender(view);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b){
            showCalender(view);
        }
    }



    public void showCalender(View view){
        if (mCalendar == null){
            mCalendar = Calendar.getInstance();
        }
        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);
        new TimePickerDialog(view.getContext(), this, hour, minute, true).show();
    }

}
