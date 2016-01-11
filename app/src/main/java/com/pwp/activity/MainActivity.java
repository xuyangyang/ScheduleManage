package com.pwp.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static GridView mGridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGridView = new GridView(getApplicationContext());
        MAdapter adapter = new MAdapter(mGridView.getContext(), new String[]{
                "你好1","你好2","你好3","你好4","你好5","你好6","你好7","你好8","你好9","你好10",
                "你好11","你好12","你好13","你好14","你好15","你好16","你好17","你好18","你好19","你好20",
                "你好21","你好22","你好23","你好24","你好25","你好26","你好27","你好28","你好29","你好30"
        });
        LinearLayout headLayout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, 300);
        headLayout.setLayoutParams(params);
        headLayout.setBackgroundColor(Color.RED);
        mGridView.setAdapter(adapter);
        mGridView.setNumColumns(2);
        setContentView(mGridView);
    }

    static class MAdapter extends BaseAdapter{

        private String[] items;
        private Context context;
        public MAdapter(Context context, String[] items) {
            this.items = items;
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = null;
            if (position %4 == 0) {
                textView = new MyText(context);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.FILL_PARENT, 100);
                textView.setLayoutParams(params);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                textView.setText(items[position]);
                textView.setBackgroundColor(0x88FF0000);
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else {
                textView = new TextView(context);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.FILL_PARENT, 200);
                textView.setLayoutParams(params);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                textView.setText(items[position]);
                textView.setVisibility(View.VISIBLE);
                if (position %4 == 1) {        // GridView的行高由该行最后一个元素的高度决定
                    AbsListView.LayoutParams params2 = new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.FILL_PARENT, 100);
                    textView.setLayoutParams(params2);
                    textView.setVisibility(View.INVISIBLE);
                }
                textView.setGravity(Gravity.CENTER);
            }
            return textView;
        }

        class MyText extends TextView {
            public MyText(Context context) {
                super(context);
            }
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int targetWidth = mGridView.getMeasuredWidth()
                        - mGridView.getPaddingLeft()
                        - mGridView.getPaddingRight();
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth,
                        MeasureSpec.getMode(widthMeasureSpec));
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }
}
