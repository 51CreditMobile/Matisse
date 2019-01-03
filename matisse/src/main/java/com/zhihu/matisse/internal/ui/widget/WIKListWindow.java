package com.zhihu.matisse.internal.ui.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.utils.Platform;

/**
 * @Description:
 * @Author: tmy
 * @Date: 2019/1/3
 * @Version: 1.0
 * @需求列表： 只能追加，不可删除，便于查看历史记录。状态符：+：添加新需求，* 修改需求 —删除内容
 * <p>格式：状态符 + 序号 + 需求简述 + 版本号 + 修改日期 + （add by 修改人）</p>
 */
public class WIKListWindow extends PopupWindow {
    
    private static final int MAX_SHOWN_COUNT = 6;
    private Context context;
    
    private ListView listView;
    private TextView mSelected;//路径选择
    private View mAnchorView;
    private Drawable mDrawableUp;
    private  Drawable mDrawableDown;
    private LayoutInflater mInflater;
    
    private CursorAdapter mAdapter;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    
    public WIKListWindow(Context context){
        super(ViewGroup.LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT );
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        initView();
    }
    
    private void initView () {
    
        // 设置收起图标
        mDrawableUp = context.getResources().getDrawable(
            R.drawable.icon_picture_selector_up);
        mDrawableUp.setBounds(0, 0, dip2px(context, 12),
            dip2px(context, 7));
        // 设置展开图标
        mDrawableDown = context.getResources().getDrawable(
            R.drawable.icon_picture_selector_down);
        mDrawableDown.setBounds(0, 0, dip2px(context, 12),
            dip2px(context, 7));
        
        View mContentView = mInflater.inflate(R.layout.view_list_popupwindow_layout, null);
        listView = mContentView.findViewById(R.id.listview_popupwindow);
        listView.setDividerHeight(0);
        setContentView(mContentView);
        setFocusable(true);
        setOutsideTouchable(true);
    
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
               
                return false;
            }
        });
        
        setOnDismissListener(new OnDismissListener() {
    
            @Override
            public void onDismiss () {
                mSelected.setCompoundDrawables(null, null, mDrawableDown, null);
            }
        });
    
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected(parent.getContext(), position);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(parent, view, position, id);
                }
            }
        });
     
        
    }
    
   
    
    public void setSelection(Context context, int position) {
        listView.setSelection(position);
        onItemSelected(context, position);
    }
    
    private void onItemSelected(Context context, int position) {
        dismiss();
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        Album album = Album.valueOf(cursor);
        String displayName = album.getDisplayName(context);
        if (mSelected.getVisibility() == View.VISIBLE) {
            mSelected.setText(displayName);
        } else {
            if (Platform.hasICS()) {
                mSelected.setVisibility(View.VISIBLE);
                mSelected.setText(displayName);
                
            } else {
                mSelected.setVisibility(View.VISIBLE);
                mSelected.setText(displayName);
            }
            
        }
    }
    
    
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }
    
    public void setAdapter(CursorAdapter adapter) {
        listView.setAdapter(adapter);
        mAdapter = adapter;
        
    }
    
    public void setSelectedTextView(TextView textView) {
        mSelected = textView;
        mSelected.setVisibility(View.GONE);
        mSelected.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mSelected.setCompoundDrawables(null, null,mDrawableUp , null);
                showPop();
            }
        });
        //mSelected.setOnTouchListener(mListPopupWindow.createDragToOpenListener(mSelected));
    }
    
    public void setPopupAnchorView(View view) {
        mAnchorView=view;
    }
    
    public void showPop(){
        if(mAnchorView==null){
            return;
        }
        int[] location = new int[2];
        mAnchorView.getLocationOnScreen(location);
        final int lacationY = location[1] + mAnchorView.getHeight() + 1;
        setHeight(getScreenHeight(context) - lacationY);
    
        showAtLocation(mAnchorView,Gravity.TOP | Gravity.LEFT, 0,lacationY);
    }
    
    
   
    public  int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    
   
    public  int getScreenHeight (Context context) {
        return ((WindowManager) context.getSystemService(
            Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }
}
