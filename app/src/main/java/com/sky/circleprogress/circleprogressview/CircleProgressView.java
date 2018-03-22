package com.sky.circleprogress.circleprogressview;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
/**
 * Created by bluesky on 2018/3/21.
 */

public class CircleProgressView extends View {

    private int mWidth;  //宽度
    private int mHeight; //高度

    private Paint circleInnerPaint; // 最里层实心圆画笔
    private int circleColor; // 实心圆颜色

    private Paint progressPaint; // 中间显示进度圆环画笔
    private float progressWidth; // 进度圆环宽度
    private int progressColor; // 进度圆环颜色

    private Paint circleOuterPaint; // 最外层圆环画笔
    private float sectorWidth; // 外层圆环宽度
    private int sectorColor; // 外层圆环颜色

    private Paint proTextPaint;
    private float proTextSize;
    private int proTextColor;


    private int currProgress; // 当前进度
    private int maxProgress=100; // 最大进度

    private boolean isShow;
    private boolean isAbove;
    private boolean isScroll;


    private int mCenterX; //中心X坐标
    private int mCenterY; //中心Y坐标
    private float mRadius;//圆的半径

    private ValueAnimator animator;
    private float nowPro;

    private int mStartAngle=0; //初始角度

    private int circleInnerPaintColor;
    private int circleOuterPaintColor;
    private int progressPaintColor;
    private float paintWidth;
    private float progressTextSize;
    private int progressTextColor;
    private static final int DEFAULT_PAINTWIDTH = 20;
    private static final int DEFAULT_PROGRESS_TEXT_SIZE = 50;
    private static final int DEFAULT_RADIUS = 400;
    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init( context, attrs);
        initPaint();
    }
    //获取配置的参数值
    private void init(Context context,  AttributeSet attrs){
        TypedArray typedArray  =context.obtainStyledAttributes(attrs,R.styleable.CircleProgressView);
        circleInnerPaintColor =  typedArray.getColor(R.styleable.CircleProgressView_circleInnerPaintColor, Color.BLUE);
        circleOuterPaintColor =  typedArray.getColor(R.styleable.CircleProgressView_circleOuterPaintColor, Color.YELLOW);
        progressPaintColor =  typedArray.getColor(R.styleable.CircleProgressView_progressPaintColor, Color.RED);
        paintWidth= typedArray.getDimension(R.styleable.CircleProgressView_paintWidth,DEFAULT_PAINTWIDTH);
        progressTextSize =typedArray.getDimension(R.styleable.CircleProgressView_progressTextSize,DEFAULT_PROGRESS_TEXT_SIZE);
        progressTextColor= typedArray.getColor(R.styleable.CircleProgressView_progressTextColor,Color.WHITE);
        mRadius =typedArray.getDimension(R.styleable.CircleProgressView_circleRadius,DEFAULT_RADIUS);
    }

    //配置各种画笔
    private void initPaint() {
        // 圆形画笔
        circleInnerPaint = new Paint();
        circleInnerPaint.setColor(circleInnerPaintColor);
        circleInnerPaint.setAntiAlias(true);
        circleInnerPaint.setStyle(Paint.Style.FILL);
        // 进度圆环画笔
        progressPaint = new Paint();
        progressPaint.setStrokeWidth(paintWidth);
        progressPaint.setColor(progressPaintColor);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        // 最外层圆环画笔
        circleOuterPaint = new Paint();
        circleOuterPaint.setStrokeWidth(paintWidth);
        circleOuterPaint.setColor(circleOuterPaintColor);
        circleOuterPaint.setAntiAlias(true);
        circleOuterPaint.setStyle(Paint.Style.STROKE);
        // 进度文字画笔
        proTextPaint = new Paint();
        proTextPaint.setTextSize(progressTextSize);
        proTextPaint.setColor(progressTextColor);
        proTextPaint.setAntiAlias(true);
        proTextPaint.setStyle(Paint.Style.FILL);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize= MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);

        int hSize= MeasureSpec.getSize(heightMeasureSpec);
        int hMode= MeasureSpec.getMode(heightMeasureSpec);

        if (wMode==MeasureSpec.EXACTLY){
              mWidth=wSize;
        }else{
            //如果为wrap_content，宽度为半径大小乘以2,注意padding
            mWidth = (int) (mRadius * 2) + getPaddingLeft() + getPaddingRight();
        }

        if (hMode==MeasureSpec.EXACTLY){
             mHeight=hSize;
        }else{
            //如果为wrap_content，宽度为半径大小乘以2,注意padding
            mHeight = (int) (mRadius * 2) + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(mWidth,mHeight);


    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        //1.计算圆心位置
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
        //2.计算半径。注意处理padding
        mRadius = (mWidth - getPaddingLeft() - getPaddingRight()) / 2;
        //3.画最里层实心圆
        canvas.drawCircle(mCenterX, mCenterY, mRadius, circleInnerPaint);
        final RectF  mRectF = new RectF();
        //注意处理padding
        mRectF.left = getPaddingLeft()+paintWidth/2;
        mRectF.right = mWidth - getPaddingRight()-paintWidth/2;
        mRectF.top = getPaddingTop()+paintWidth/2;
        mRectF.bottom = mHeight - getPaddingBottom()-paintWidth/2;

        //4.画最里层外边的圆弧
        canvas.drawArc(mRectF, mStartAngle, 360, false, circleOuterPaint);


        //5.计算显示的进度
        String currProgressStr=(int)nowPro+"%";
        //5.1测试文字宽度
        float textWidth = proTextPaint.measureText(currProgressStr);
        //5.2获取文字高度
        Rect rect = new Rect();
        proTextPaint.getTextBounds(currProgressStr, 0, currProgressStr.length(), rect);
        //5.3绘制进度的文字
        canvas.drawText(currProgressStr,mCenterX-textWidth/2,mCenterY+rect.height()/2,proTextPaint);

        //6.绘制进度环的展示
        canvas.drawArc(mRectF,mStartAngle,nowPro/100*360,false,progressPaint);

    }
    /**
     * 设置初始角度
     */
    public synchronized void setStartAngle(int startAngle) {
        if (startAngle < -360) {
            throw new IllegalArgumentException("the angle can not less than -360");
        }
        if (startAngle > 360) {
            throw new IllegalArgumentException("the angle can not larger than 360");
        }
        this.mStartAngle = startAngle;
    }

    /**
     * 设置进度
     * @param currProgress
     */
    public void setCurrProgress(int currProgress) {
        this.currProgress = currProgress;
        //使用动画在指定时间内绘制出动画
        animator = ValueAnimator.ofFloat(0, currProgress);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                nowPro = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }
}
