package com.pwp.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.pwp.borderText.BorderText;
import com.pwp.dao.ScheduleDAO;
import com.pwp.view.GridViewForScrollView;
import com.pwp.view.ListViewForScrollView;
import com.pwp.vo.ScheduleVO;

/**
 * 日历显示activity
 * @author jack_peng
 *
 */
@SuppressLint("ResourceAsColor") 
public class CalendarActivity extends Activity implements OnGestureListener,OnItemClickListener,OnItemLongClickListener {

	private ViewFlipper flipper = null;
	private GestureDetector gestureDetector = null;
	private CalendarViewAdapter calV = null;
	private GridViewForScrollView gridView = null;
	private BorderText topText = null;
	private ListViewForScrollView lV_schedule;
	List< Map<String, Object>> itemList;
	private Drawable draw = null;
	private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private ScheduleDAO dao = null;
	private ScheduleVO scheduleVO;
	private String[] scheduleIDs;
	private  ArrayList<String> scheduleDate;
	private Dialog builder;
	private int gvFlag = 0;
	static View ONCLICK_VIEW = null;
	static boolean HAS_SCHEDULE = false;
	private SimpleAdapter mAdapter;
    private int clickPosition = -1;
    private int iScheduleDay = -1;
    private int nodataSig = 0;
    private int dataSig = 1;
    private Message message;
    private int lvIndext;// 标记上次滑动位置
	public CalendarActivity() {

		Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
    	currentDate = sdf.format(date);  //当期日期
    	year_c = Integer.parseInt(currentDate.split("-")[0]);
    	month_c = Integer.parseInt(currentDate.split("-")[1]);
    	day_c = Integer.parseInt(currentDate.split("-")[2]);
    	
    	dao = new ScheduleDAO(this);
	}

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rili);
		gestureDetector = new GestureDetector(this);
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        topText = (BorderText) findViewById(R.id.toptext);
        lV_schedule = (ListViewForScrollView)findViewById(R.id.lv_schedule);
        lV_schedule.setOnItemLongClickListener(this);

        flipper.removeAllViews();
        calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        addGridView();
        gridView.setAdapter(calV);
        //flipper.addView(gridView);
        flipper.addView(gridView, 0);

        addTextToTopTextView(topText);
		itemList = new ArrayList<Map<String,Object>>();
		mAdapter = new SimpleAdapter(this, itemList, R.layout.item_detail, new String[]{"id","time","shec"}, new int[]{R.id.tv_id,R.id.tv_time,R.id.tv_shecudul});
		lV_schedule.setAdapter(mAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override

            public void onScrollStateChanged(AbsListView view, int scrollState) {

                switch (scrollState){
                    //滚动前，手还在屏幕上 记录滚动前的下标
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        lvIndext = view.getLastVisiblePosition();
                        break;
                    //滚动停止
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int scrolled = view.getLastVisiblePosition();
                        if(scrolled>=lvIndext){ //下滚
                            gridView.setNumColumns(7);
                        }else{
                            gridView.setNumColumns(1);
                        }
                        break;
                }

            }

            @Override

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


            }});
	}

    @Override
    protected void onResume() {
        super.onResume();
		initDate();

    }

	public void initDate() {
		 itemList.clear();
        if(clickPosition == -1){
            scheduleIDs = dao.getScheduleByTagDate(year_c, month_c, day_c);
        }else{

            String scheduleDay = calV.getDateByClickItem(clickPosition).split("\\.")[0];
            String scheduleYear = calV.getShowYear();
            String scheduleMonth = calV.getShowMonth();
            scheduleIDs = dao.getScheduleByTagDate(Integer.parseInt(scheduleYear), Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
        }

         if(scheduleIDs != null && scheduleIDs.length > 0){
       	  HAS_SCHEDULE = true;
       	  ScheduleDAO dao=new ScheduleDAO(CalendarActivity.this);
    		 for (int i = 0; i < scheduleIDs.length; i++) {
    			
           	scheduleVO=dao.getScheduleByID(CalendarActivity.this,Integer.parseInt(scheduleIDs[i]));
    			Map<String, Object> map = new HashMap<String, Object>();
    			//System.out.println("------------test"+scheduleVO.getTime()+"-------"+scheduleVO.getScheduleContent());
                map.put("id", scheduleVO.getScheduleID());
    			map.put("time", scheduleVO.getTime());
    			map.put("shec", scheduleVO.getScheduleTtile());
    			itemList.add(map);
    		}
         }

	}



    //加载日程列表
    public synchronized void getScheduleDateList(final int year,final int month,final int date){
        new Thread(new Runnable() {
            @Override
            public void run() {
                message = new Message();
                if (!Thread.currentThread().isInterrupted()) {
                    Looper.prepare();
                    Bundle b = new Bundle();
                    scheduleIDs = dao.getScheduleByTagDate(year, month, date);
                    if (scheduleIDs != null && scheduleIDs.length > 0) {
                        HAS_SCHEDULE = true;
                        message.what = dataSig;

                    } else {
                        HAS_SCHEDULE = false;
                        message.what = nodataSig;

                    }
                    message.setData(b);
                }

                mHandler.sendMessage(message);
            }
        }).start();

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == dataSig)
            {
                itemList.clear();
                ScheduleDAO dao = new ScheduleDAO(CalendarActivity.this);
                for (int i = 0; i < scheduleIDs.length; i++) {

                    scheduleVO = dao.getScheduleByID(CalendarActivity.this, Integer.parseInt(scheduleIDs[i]));
                    Map<String, Object> map = new HashMap<String, Object>();
                    //System.out.println("------------test"+scheduleVO.getTime()+"-------"+scheduleVO.getScheduleContent());
                    map.put("id", scheduleVO.getScheduleID());
                    map.put("time", scheduleVO.getTime());
                    map.put("shec", scheduleVO.getScheduleTtile());
                    itemList.add(map);
                }
                mAdapter.notifyDataSetChanged();
            }else {
                itemList.clear();
                mAdapter.notifyDataSetChanged();
            }

            super.handleMessage(msg);
        }
    };


    public void previous_year(View view) {
		addGridView();   //添加一个gridView
		jumpMonth--;     //上一个月
		int gvFlag = 0;
		calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        gridView.setAdapter(calV);
        gvFlag++;
        addTextToTopTextView(topText);
        //flipper.addView(gridView);
        flipper.addView(gridView,gvFlag);
        
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
		this.flipper.showPrevious();
		flipper.removeViewAt(0);
	}
	
	
	public void next_year(View view) {
		addGridView();   //添加一个gridView
		jumpMonth++;     //下一个月
		int gvFlag = 0;
		calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        gridView.setAdapter(calV);
        //flipper.addView(gridView);
        addTextToTopTextView(topText);
        gvFlag++;
        flipper.addView(gridView, gvFlag);
		this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
		this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
		this.flipper.showNext();
		flipper.removeViewAt(0);
	}
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int gvFlag = 0;//每次添加gridview到viewflipper中时给的标记
		if (e1.getX() - e2.getX() > 50) {
            //像左滑动
			addGridView();   //添加一个gridView
			jumpMonth++;     //下一个月
			if(iScheduleDay==-1){
                calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
            }else{
                calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,iScheduleDay);
            }
			gridView.setAdapter(calV);
	        //flipper.addView(gridView);
	        addTextToTopTextView(topText);
	        gvFlag++;
	        flipper.addView(gridView, gvFlag);
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
			this.flipper.showNext();
			flipper.removeViewAt(0);
			return true;
		} else if (e1.getX() - e2.getX() < -50) {
            //向右滑动
			addGridView();   //添加一个gridView
			jumpMonth--;     //上一个月
            if(iScheduleDay==-1){
                calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
            }else{
                calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,iScheduleDay);
            }
	        gridView.setAdapter(calV);
	        gvFlag++;
	        addTextToTopTextView(topText);
	        //flipper.addView(gridView);
	        flipper.addView(gridView,gvFlag);
	        
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
			this.flipper.showPrevious();
			flipper.removeViewAt(0);
			return true;
		}
		return false;
	}
	
	/**
	 * 创建菜单
	 */
	
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, menu.FIRST, menu.FIRST, "今天");
		menu.add(0, menu.FIRST+1, menu.FIRST+1, "跳转");
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 选择菜单
	 */
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()){
        case Menu.FIRST:
        	//跳转到今天
        	int xMonth = jumpMonth;
        	int xYear = jumpYear;
        	int gvFlag =0;
        	jumpMonth = 0;
        	jumpYear = 0;
        	addGridView();   //添加一个gridView
        	year_c = Integer.parseInt(currentDate.split("-")[0]);
        	month_c = Integer.parseInt(currentDate.split("-")[1]);
        	day_c = Integer.parseInt(currentDate.split("-")[2]);
        	calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        addTextToTopTextView(topText);
	        gvFlag++;
	        flipper.addView(gridView,gvFlag);
	        if(xMonth == 0 && xYear == 0){
	        	//nothing to do
	        }else if((xYear == 0 && xMonth >0) || xYear >0){
	        	this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
				this.flipper.showNext();
	        }else{
	        	this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_out));
				this.flipper.showPrevious();
	        }
			flipper.removeViewAt(0);
        	break;
        case Menu.FIRST+1:
        	
        	new DatePickerDialog(this, new OnDateSetListener() {
				
				
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					//1901-1-1 ----> 2049-12-31
					if(year < 1901 || year > 2049){
						//不在查询范围内
						new AlertDialog.Builder(CalendarActivity.this).setTitle("错误日期").setMessage("跳转日期范围(1901/1/1-2049/12/31)").setPositiveButton("确认", null).show();
					}else{
						int gvFlag = 0;
						addGridView();   //添加一个gridView
			        	calV = new CalendarViewAdapter(CalendarActivity.this, CalendarActivity.this.getResources(),year,monthOfYear+1,dayOfMonth);
				        gridView.setAdapter(calV);
				        addTextToTopTextView(topText);
				        gvFlag++;
				        flipper.addView(gridView,gvFlag);
				        if(year == year_c && monthOfYear+1 == month_c){
				        	//nothing to do
				        }
				        if((year == year_c && monthOfYear+1 > month_c) || year > year_c ){
				        	CalendarActivity.this.flipper.setInAnimation(AnimationUtils.loadAnimation(CalendarActivity.this,R.anim.push_left_in));
				        	CalendarActivity.this.flipper.setOutAnimation(AnimationUtils.loadAnimation(CalendarActivity.this,R.anim.push_left_out));
				        	CalendarActivity.this.flipper.showNext();
				        }else{
				        	CalendarActivity.this.flipper.setInAnimation(AnimationUtils.loadAnimation(CalendarActivity.this,R.anim.push_right_in));
				        	CalendarActivity.this.flipper.setOutAnimation(AnimationUtils.loadAnimation(CalendarActivity.this,R.anim.push_right_out));
				        	CalendarActivity.this.flipper.showPrevious();
				        }
				        flipper.removeViewAt(0);
				        //跳转之后将跳转之后的日期设置为当期日期
				        /*year_c = year;
						month_c = monthOfYear+1;
						day_c = dayOfMonth;*/
						jumpMonth = 0;
						jumpYear = 0;

					}
				}
			},year_c, month_c-1, day_c).show();
			break;
        }
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	public boolean onTouchEvent(MotionEvent event) {

		return this.gestureDetector.onTouchEvent(event);
	}

	
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//添加头部的年份 闰哪月等信息
	@SuppressLint("NewApi")
	public void addTextToTopTextView(TextView view){
		StringBuffer textDate = new StringBuffer();
		//draw = getResources().getDrawable(R.drawable.item);
		//view.setBackground(draw);
		view.setBackgroundResource(R.color.white);
		textDate.append(calV.getShowYear()).append("  年   ").append(
				calV.getShowMonth()).append("  月").append("\t");
		/*if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("闰").append(calV.getLeapMonth()).append("月")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("年").append("(").append(
				calV.getCyclical()).append("年)");*/
		view.setText(textDate);
		view.setTextColor(Color.BLACK);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	//添加gridview
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		//取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth(); 
        int Height = display.getHeight();
        
		gridView = new GridViewForScrollView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(46);
	//	gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		if(Width == 480 && Height == 800){
			gridView.setColumnWidth(69);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
        gridView.setBackgroundResource(R.color.white);
		gridView.setOnTouchListener(new OnTouchListener() {
            //将gridview中的触摸事件回传给gestureDetector
			
			public boolean onTouch(View v, MotionEvent event) {
//                mHandler.removeMessages(SCUSSCE,message);
				return CalendarActivity.this.gestureDetector
						.onTouchEvent(event);

			}
		});

		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				 
				  //点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				  int startPosition = calV.getStartPositon();
				  int endPosition = calV.getEndPosition();
				  if(startPosition <= position  && position <= endPosition){

					  String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
					  //String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //这一天的阴历
	                  String scheduleYear = calV.getShowYear();
	                  String scheduleMonth = calV.getShowMonth();
	                  selectedGridView(scheduleDay, scheduleYear, scheduleMonth, view, position);

				  }
			}
		});
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
                TextView tv = (TextView)view.findViewById(R.id.tvtext);
                if(tv.getCurrentTextColor()==Color.GRAY){
                    return false;
                }
				getDateClick(arg2);
				Intent intent = new Intent(CalendarActivity.this, ScheduleViewActivity.class);
				intent.putStringArrayListExtra("scheduleDate", scheduleDate);
				startActivity(intent);
				return false;
			}
		});
		gridView.setLayoutParams(params);
	}


	//gridview选中效果
    public void selectedGridView(String scheduleDay,String scheduleYear,String scheduleMonth,View view,int position){
		clickPosition = position;
        iScheduleDay = Integer.parseInt(scheduleDay);
        if (ONCLICK_VIEW==null) {
            if (scheduleDay.equals(String.valueOf(day_c))&&scheduleMonth.equals(String.valueOf(month_c))&&scheduleYear.equals(String.valueOf(year_c))) {
                ONCLICK_VIEW = null;
            }else {
                ONCLICK_VIEW = view;
                ONCLICK_VIEW.findViewById(R.id.tvtext).setBackgroundResource(R.drawable.select_bg);
            }

        }else {
            if (HAS_SCHEDULE) {
                ONCLICK_VIEW.findViewById(R.id.bluepoint).setVisibility(View.VISIBLE);
				ONCLICK_VIEW.findViewById(R.id.tvtext).setBackgroundResource(R.color.white);
            }else {
                ONCLICK_VIEW.findViewById(R.id.tvtext).setBackgroundResource(R.color.white);
            }
            if (scheduleDay.equals(String.valueOf(day_c))&&scheduleMonth.equals(String.valueOf(month_c))&&scheduleYear.equals(String.valueOf(year_c))) {
                ONCLICK_VIEW = null;
            }else {
                ONCLICK_VIEW = view;
                ONCLICK_VIEW.findViewById(R.id.tvtext).setBackgroundResource(R.drawable.select_bg);
            }
        }

		//通过日期查询这一天是否被标记，如果标记了日程就查询出这天的所有日程信息
		getScheduleDateList(Integer.parseInt(scheduleYear), Integer.parseInt(scheduleMonth), Integer.parseInt(scheduleDay));
    }
	
	protected void getDateClick(int position){
		 //点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
		  int startPosition = calV.getStartPositon();
		  int endPosition = calV.getEndPosition();
		  if(startPosition <= position  && position <= endPosition){
			  String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
			  //String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //这一天的阴历
            String scheduleYear = calV.getShowYear();
            String scheduleMonth = calV.getShowMonth();
            String week = "";
            switch(position%7){
            case 0:
          	  week = "Mo";
          	  break;
            case 1:
          	  week = "Tu";
          	  break;
            case 2:
          	  week = "We";
          	  break;
            case 3:
          	  week = "Th";
          	  break;
            case 4:
          	  week = "Fr";
          	  break;
            case 5:
          	  week = "Sa";
          	  break;
            case 6:
          	  week = "Su";
          	  break;
            }
            scheduleDate = new ArrayList<String>();
            scheduleDate.add(scheduleYear);
            scheduleDate.add(scheduleMonth);
            scheduleDate.add(scheduleDay);
            scheduleDate.add(week);
		  }
	}
	@Override
	protected void onRestart() {
		/*int xMonth = jumpMonth;
    	int xYear = jumpYear;
    	int gvFlag =0;
    	jumpMonth = 0;
    	jumpYear = 0;
    	addGridView();   //添加一个gridView
    	year_c = Integer.parseInt(currentDate.split("-")[0]);
    	month_c = Integer.parseInt(currentDate.split("-")[1]);
    	day_c = Integer.parseInt(currentDate.split("-")[2]);
    	calV = new CalendarViewAdapter(this, getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        gridView.setAdapter(calV);
        addTextToTopTextView(topText);
        gvFlag++;
        flipper.addView(gridView,gvFlag);
		flipper.removeViewAt(0);*/
		super.onRestart();
	}


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView,final View view, int i, long l) {

        Dialog alertDialog = new AlertDialog.Builder(CalendarActivity.this).
                setMessage("删除日程信息？").
                setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        TextView id_tv=  (TextView) view.findViewById(R.id.tv_id);
                        int scheduleID = Integer.parseInt(id_tv.getText().toString());
                        dao.delete(scheduleID);
                        mAdapter.notifyDataSetChanged();
                        ScheduleViewActivity.setAlart(CalendarActivity.this);
                        if(builder!=null&&builder.isShowing()){
                            builder.dismiss();
                        }
                    }

                }).
                setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).
                create();
        alertDialog.show();

        return false;
    }
}