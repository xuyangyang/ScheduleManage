/*
 * Copyright (C) 2015 Warnier-zhang. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pwp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class is a calendar widget for displaying lastest seven dates in
 * a single line. The start of date supported by this CalendarView is
 * configurable.
 */
public class CalendarView extends View {
    private static final int zh_CN = 86;
    private static final int en_US = 1;

    private Context mContext;
    private Paint mTextPaint;
    private Paint mRectPaint;
    private Paint mCirclePaint;
    private int mWidthMeasureSpec;
    private float mCellMeasureSpec;
    private String[][] mDataSet = new String[2][7];
    private Calendar mCalendar;
    private int mDayOfMonth;
    private int mDayOfWeek;

    public CalendarView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CalendarView(Context context,int dayOfMonth,int dayOfWeek) {
        super(context);
        mContext = context;
        mDayOfMonth = dayOfMonth;
        mDayOfWeek = dayOfWeek;
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(24);
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setStyle(Paint.Style.FILL);
        mRectPaint.setColor(Color.parseColor("#4285F4"));
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.parseColor("#4285F4"));
        initDate();
    }

    private void initDate() {
        mCalendar = new GregorianCalendar();
        if(mDayOfMonth == -1){
            mDayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        }
        if(mDayOfWeek == -1){
            mDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        }

        mDataSet[0] = getWeekdayNames(zh_CN);
        mDataSet[1] = fillDataSet(mDayOfMonth, mDayOfWeek);
        if (mDataSet[1] == null) {
            throw new IllegalStateException("日期数据异常！");
        }
    }

    private String[] getWeekdayNames(int locale) {
        if (locale == en_US) {
            return new String[]{"S", "M", "T", "W", "S", "F", "S"};
        } else if (locale == zh_CN) {
            return new String[]{"日", "一", "二", "三", "四", "五", "六"};
        } else {
            throw new IllegalStateException("日期格式异常！");
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidthMeasureSpec = w;
        mCellMeasureSpec = mWidthMeasureSpec / 7.0f;
        mTextPaint.setTextSize(mCellMeasureSpec / 3.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mWidthMeasureSpec, mCellMeasureSpec, 0, 0, mRectPaint);
        for (int i = 0; i < 2; i++) {
            setPaintColor(i);
            for (int j = 0; j < 7; j++) {
                float x = (float) ((j + 0.5) * mCellMeasureSpec - mTextPaint.measureText(mDataSet[i][j]) / 2);
                float y = (float) ((i + 0.8) * mCellMeasureSpec - mTextPaint.measureText(mDataSet[i][j], 0, 1) / 2);
                /**
                 * 用“圆形”标注当前日期；
                 */
                if (mDataSet[i][j].equals(String.valueOf(mDayOfMonth))) {
                    float cx = x + mTextPaint.measureText(mDataSet[i][j]) * 0.5f;
                    float cy = y - mTextPaint.measureText(mDataSet[i][j]) * 0.42f;
                    canvas.drawCircle(cx, cy, mCellMeasureSpec / 3.0f, mCirclePaint);
                    setPaintColor(0);
                    canvas.drawText(mDataSet[i][j], x, y, mTextPaint);
                    setPaintColor(1);
                } else {
                    canvas.drawText(mDataSet[i][j], x, y, mTextPaint);
                }
            }
        }
    }

    private void setPaintColor(int rowSpec) {
        if (rowSpec == 0) {
            mTextPaint.setColor(Color.parseColor("#FFFFFF"));
        } else {
            mTextPaint.setColor(Color.parseColor("#565656"));
        }
    }

    private String[] fillDataSet(int dayOfMonth, int dayOfWeek) {
        int index = 0;
        String[] date = {
                "1", "2", "3", "4", "5", "6", "7"
        };

        for (int i = 0; i < date.length; i++) {
            if (date[i].equals("" + dayOfWeek)) {
                date[i] = "" + dayOfMonth;
                index = i;
            }
        }
        for (int i = index - 1; i >= 0; i--) {
            mCalendar.add(Calendar.DAY_OF_MONTH, -1);
            date[i] = mCalendar.get(Calendar.DAY_OF_MONTH) + "";
        }
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        for (int i = index + 1; i < date.length; i++) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            date[i] = mCalendar.get(Calendar.DAY_OF_MONTH) + "";
        }

        return date;
    }
}
