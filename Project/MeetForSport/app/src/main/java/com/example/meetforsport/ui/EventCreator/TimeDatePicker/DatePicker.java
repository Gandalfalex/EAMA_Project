package com.example.meetforsport.ui.EventCreator.TimeDatePicker;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class DatePicker implements  View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener, View.OnClickListener{


    public EditText text;
    public String format;
    private Calendar mCalendar;

    private SimpleDateFormat mFormat;

    public DatePicker(EditText textField, String format){
        this.text = textField;
        this.format = format;
        this.text.setOnClickListener(this);
        this.text.setOnFocusChangeListener(this);
        mFormat = new SimpleDateFormat(format, Locale.getDefault());
    }


    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
        mCalendar.set(Calendar.YEAR, i);
        mCalendar.set(Calendar.MONTH, i1);
        mCalendar.set(Calendar.DAY_OF_MONTH, i2);
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
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(view.getContext(),this, year, month, day).show();

    }


}
