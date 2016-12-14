package com.example.ytz.progressbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

/**
 * Created by admin on 2016/12/12.
 */

public class ProgressButton extends Button {
    private Paint mPaint;//画笔
    private int textColor;//文本颜色
    private Drawable nomalBack;//普通背景
    private Drawable progressBack;//显示进度的背景
    private Drawable completeBack;//进度完全背景
    public static final int STATE_NOMAL = 110;
    public static final int STATE_PROGRESS = 120;
    public static final int STATE_COMPLETE = 130;
    public static final int STATE_PAUSE = 140;
    public static final int STATE_ERROR = 150;
    private String normalText;
    private String progressText;
    private String completeText;
    private String errorText;

    //当前状态
    private int stateNow = STATE_NOMAL;

    //当前进度百分比
    private int precentNow = 0;

    public int getPrecentNow() {
        return precentNow;
    }

    private OnProgressButtonClickListener mOnProgressButtonClickListener;

    public ProgressButton(Context context) {
        this(context,null);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
        //获取地应以属性
        TypedArray attributes = context.obtainStyledAttributes(attrs,R.styleable.ProgressButton,defStyleAttr,0);
        //获取背景
        nomalBack = attributes.getDrawable(R.styleable.ProgressButton_normalBackground);
        progressBack = attributes.getDrawable(R.styleable.ProgressButton_progressBackground);
        completeBack = attributes.getDrawable(R.styleable.ProgressButton_completeBackground);
        normalText = attributes.getString(R.styleable.ProgressButton_nomalText);
        completeText = attributes.getString(R.styleable.ProgressButton_completeText);
        textColor = attributes.getColor(R.styleable.ProgressButton_textColor,0);
        errorText = attributes.getString(R.styleable.ProgressButton_errorText);

         //设置button本身的文字为透明以免干扰我们自己绘制上去的文字
        setTextColor(getResources().getColor(R.color.text_al));
        System.out.println(textColor);
        mPaint = new Paint();
        mPaint.setTextSize(getTextSize());
        mPaint.setColor(textColor);
        mPaint.setAntiAlias(true);


        //点击事件的实现
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnProgressButtonClickListener.onProgressButtonClick(view,stateNow);
            }
        });
    }

    //1、测量:一个view的宽高尺寸，只有在测量之后才能得到，也就是measure方法被调用之后
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("ProgressButton.onMeasure");
        //获取spec模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //获取实际宽高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //控件尺寸
        int width = 0;
        int height = 0;
        //计算文本显示所需的宽高
        Rect textBound = new Rect();
        String tip = getResources().getString(R.string.test_text);
        mPaint.getTextBounds("完成",0,tip.length(),textBound);

        //判断宽的spec模式是否为父控件给的具体值
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize+getPaddingLeft()+getPaddingRight();
        }else {
            width = textBound.width()+getPaddingLeft()+getPaddingRight();
        }
        //判断高的spec模式是否为父控件给的具体值
        if (heightMode== MeasureSpec.EXACTLY) {
            height = heightSize+getPaddingTop()+getPaddingBottom();
        }else {
            height = textBound.height()+getPaddingBottom()+getPaddingTop();
        }
        //设置bt尺寸
        setMeasuredDimension(width,height);
    }
    //2、定位：
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        System.out.println("ProgressButton.onLayout");

    }
    //3、画：该方法后才可见
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("ProgressButton.onDraw");
        String tip = "";
        switch (stateNow) {
            case STATE_NOMAL:
                tip = normalText;
                setBackgroundDrawable(nomalBack);
                precentNow = 0;
                break;

            case STATE_COMPLETE:
                tip = completeText;
                setBackgroundDrawable(completeBack);
                break;
            case STATE_PAUSE:
            case STATE_PROGRESS:
                tip = precentNow+"%";
                //计算进度
                int proWidth = (int) (getMeasuredWidth()*((double)precentNow/100));
                Rect rect = new Rect(0,0,proWidth,getMeasuredHeight());
                progressBack.setBounds(rect);
                progressBack.draw(canvas);
                break;
            case STATE_ERROR:
                tip = errorText;
                break;
        }
        //绘制提示文本
        Rect rect = new Rect();
        System.out.println("tip--->"+tip);
        mPaint.getTextBounds(tip,0,tip.length(),rect);
        canvas.drawText(tip,(getMeasuredWidth()-rect.width())/2,
                (getMeasuredHeight()+rect.height())/2,mPaint);
    }

    /**
     * 设置当期button状态
     * @param state
     */
    public void setState(int state){
        this.stateNow = state;
        postInvalidate();//主线程中的重绘
    }

    /**
     * 设置当前进度
     * @param precent 进度：百分比
     */
    public void setProgress(int precent){
        this.precentNow = precent;
        postInvalidate();
    }

    //定义点击监听回调
    public interface OnProgressButtonClickListener{
        void onProgressButtonClick(View view,int state);
    }
    //提供set方法，设置监听
    public void setOnProgressButtonClickListener(OnProgressButtonClickListener onProgressButtonClickListener) {
        mOnProgressButtonClickListener = onProgressButtonClickListener;
    }
}
