package com.android.zcomponent.views;


import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;

import com.android.component.zframework.R;
import com.android.zcomponent.util.CustomDialog;
import com.android.zcomponent.util.MyLayoutAdapter;


public class AlertPopupWindow extends CustomDialog
{

    private Context mContext;

    private View rootView;

    private SlidingDrawer mDrawer;

    private RelativeLayout mrlayoutParent;

    private Button mbtnAlert1;

    private Button mbtnAlert2;

    private Button mbtnCancel;

    private Button mbtnAlert3;

    public AlertPopupWindow(Context context)
    {
        super(context, R.layout.dialog_slider_alert_layout, false, false);
        this.mContext = context;
        initOptionWindow();
    }

    private void initOptionWindow()
    {
        setDimAmount(0.1f);
        bindWidget();
        setClickListener();
    }

    private void bindWidget()
    {
        mrlayoutParent = (RelativeLayout) findViewById(R.id.select_rlayout_parent);
        mDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);

        mbtnAlert1 = (Button) findViewById(R.id.confim_dialog_btn_1);
        mbtnAlert2 = (Button) findViewById(R.id.confim_dialog_btn_2);
        mbtnAlert3 = (Button) findViewById(R.id.confim_dialog_btn_3);
        mbtnAlert1.setVisibility(View.GONE);
        mbtnAlert2.setVisibility(View.GONE);
        mbtnAlert3.setVisibility(View.GONE);
        mbtnCancel = (Button) findViewById(R.id.confim_dialog_cancel);

    }

    /**
     * <p>
     * Description: 设置按钮标题
     * <p>
     * @date 2015-7-7 
     * @author wei
     * @param title 标题名称，长度最大为三个
     */
    public void setAlertTitles(String[] title)
    {
        if (null == title)
        {
            return;
        }

        if (title.length > 3)
        {
            throw new IllegalArgumentException("按钮数据需小于等于3个");
        }

        if (title.length < 3)
        {
            mDrawer.getLayoutParams().height = (int) ((70 * (title.length + 1) + 30) * MyLayoutAdapter
                    .getInstance().getDensityRatio());
        }

        for (int i = 0; i < title.length; i++)
        {
            if (i == 0)
            {
                mbtnAlert1.setText(title[i]);
                mbtnAlert1.setVisibility(View.VISIBLE);
                mbtnAlert1.setOnClickListener(new AlertBtnClickListener(i));
            }
            if (i == 1)
            {
                mbtnAlert2.setText(title[i]);
                mbtnAlert2.setVisibility(View.VISIBLE);
                mbtnAlert2.setOnClickListener(new AlertBtnClickListener(i));

            }
            if (i == 2)
            {
                mbtnAlert3.setText(title[i]);
                mbtnAlert3.setVisibility(View.VISIBLE);
                mbtnAlert3.setOnClickListener(new AlertBtnClickListener(i));
            }
        }
    }

    private class AlertBtnClickListener implements View.OnClickListener
    {

        private int position;

        public AlertBtnClickListener(int position)
        {
            this.position = position;
        }

        @Override
        public void onClick(View view)
        {
            mDrawer.animateClose();

            if (null != clickListener)
            {
                clickListener.onAlertItemClick(position, view);
            }
        }
    }

    private OnAlertItemClickListener clickListener;

    public void setOnAlertItemClickListener(OnAlertItemClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public interface OnAlertItemClickListener
    {

        public void onAlertItemClick(int position, View view);
    }

    private void setClickListener()
    {
        mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

            @Override
            public void onDrawerClosed()
            {
                dismiss();
            }
        });

        this.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0)
            {
                mDrawer.close();
            }
        });

        mrlayoutParent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                mDrawer.animateClose();
            }
        });

        mbtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                mDrawer.animateClose();
            }
        });
    }

    @Override
    public void show()
    {
        super.show();
        mDrawer.animateOpen();
    }
}
