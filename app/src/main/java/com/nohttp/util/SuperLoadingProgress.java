package com.nohttp.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;


import java.security.KeyStore;
import java.security.PublicKey;

/**
 * @author crazychen
 * @version 1.0
 * @date 2015/12/9
 */
public class SuperLoadingProgress extends View {
    /**
     * 褰撳墠杩涘害
     */
    private int progress = 0;
    /**
     * 鏈�澶ц繘搴�
     */
    private static final int maxProgress = 100;
    /**
     * 灏忔柟鍧楁姏鍑哄姩鐢�
     */
    private ValueAnimator mRotateAnimation;
    /**
     * 灏忔柟鍧椾笅钀�
     */
    private ValueAnimator mDownAnimation;
    /**
     * 灏忔柟鍧椾笅钀�
     */
    private ValueAnimator mForkAnimation;
    /**
     * 鎵撻挬
     */
    private ValueAnimator mTickAnimation;
    /**
     * 缁樺埗鎰熷徆鍙�
     */
    private ValueAnimator mshockAnimation;
    /**
     * 缁樺埗鎰熷徆鍙烽渿鍔�
     */
    private ValueAnimator mCommaAnimation;
    private int mWidth,mHeight;
    private RectF mRectF = new RectF();
    private Paint circlePaint = new Paint();
    private Paint smallRectPaint = new Paint();
    private Paint downRectPaint = new Paint();
    private Paint commaPaint = new Paint();
    private Paint tickPaint = new Paint();
    /**
     * 鐢荤瑪瀹藉害
     */
    private int strokeWidth = 5; 
    /**
     * 
     */
    private float radius = 0;
    //0鐢诲渾,1鎶涘嚭鏂瑰潡,2涓嬭惤鍙樼矖,鎸ゅ帇鍦嗗舰,3,缁樺埗涓夊弶锛屽渾褰㈡仮澶�,4,缁胯壊鍕�,5,绾㈣壊鎰熷徆鍙峰嚭鐜帮紝6,绾㈣壊鎰熷徆鍙烽渿鍔�
    private int status = 0;
    /**
     * 娴嬮噺涓嬭惤璺緞
     */
    private PathMeasure downPathMeasure1;
    private PathMeasure downPathMeasure2;
    /**
     * 娴嬮噺鍒嗗弶
     */
    private PathMeasure forkPathMeasure1;
    private PathMeasure forkPathMeasure2;
    private PathMeasure forkPathMeasure3;
    /**
     * 娴嬮噺鎵撻挬
     */
    private PathMeasure tickPathMeasure;
    /**
     * 鎰熷徆鍙�
     */
    private PathMeasure commaPathMeasure1;
    private PathMeasure commaPathMeasure2;
    /**
     * 涓嬭惤鐧惧垎姣�
     * @param context
     */
    float downPrecent = 0;
    /**
     * 鍒嗗弶鐧惧垎姣�
     * @param context
     */
    float forkPrecent = 0;
    /**
     * 鎵撻挬鐧惧垎姣�
     * @param context
     */
    float tickPrecent = 0;
    /**
     * 鎰熷徆鍙风櫨鍒嗘瘮
     * @param context
     */
    float commaPrecent = 0;
    /**
     * 闇囧姩鐧惧垎姣�
     * @param context
     */
    int shockPrecent = 0;
    /**
     * 鏄惁loading鎴愬姛
     */
    public boolean isSuccess = false;
    /**
     * 璧峰瑙掑害
     */
    private static final float startAngle = -90;
    /**
     * 鎵繃瑙掑害
     */
    private float curSweepAngle = 0;
    /**
     * 闇囧姩瑙掑害
     */
    private int shockDir = 20;

    public SuperLoadingProgress(Context context) {
        super(context);
        init();
    }

    public SuperLoadingProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuperLoadingProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        radius = Math.min(getMeasuredWidth(),getMeasuredHeight())/4-strokeWidth;
        mRectF.set(new RectF(radius+strokeWidth, radius+strokeWidth, 3 * radius+strokeWidth, 3 * radius+strokeWidth));
        //鍒濆鍖栦笅钀借矾寰�
        Path downPath1 = new Path();
        downPath1.moveTo(2*radius+strokeWidth,strokeWidth);
        downPath1.lineTo(2 * radius+strokeWidth, radius+strokeWidth);
        Path downPath2 = new Path();
        downPath2.moveTo(2 * radius+strokeWidth, strokeWidth);
        downPath2.lineTo(2 * radius+strokeWidth, 2 * radius+strokeWidth);
        downPathMeasure1 = new PathMeasure(downPath1,false);
        downPathMeasure2 = new PathMeasure(downPath2,false);
        //鍒濆鍖栧垎鍙夎矾寰�
        Path forkpath1 = new Path();
        forkpath1.moveTo(2*radius+strokeWidth,2*radius+strokeWidth);
        forkpath1.lineTo(2 * radius+strokeWidth, 3 * radius+strokeWidth);
        float sin60 = (float) Math.sin(Math.PI/3);
        float cos60 = (float) Math.cos(Math.PI / 3);
        Path forkpath2 = new Path();
        forkpath2.moveTo(2 * radius+strokeWidth, 2 * radius+strokeWidth);
        forkpath2.lineTo(2*radius-radius*sin60+strokeWidth, 2*radius+radius*cos60+strokeWidth);
        Path forkpath3 = new Path();
        forkpath3.moveTo(2 * radius+strokeWidth, 2 * radius+strokeWidth);
        forkpath3.lineTo(2 * radius + radius * sin60+strokeWidth, 2 * radius + radius * cos60+strokeWidth);
        forkPathMeasure1 = new PathMeasure(forkpath1,false);
        forkPathMeasure2 = new PathMeasure(forkpath2,false);
        forkPathMeasure3 = new PathMeasure(forkpath3,false);
        //鍒濆鍖栨墦閽╄矾寰�
        Path tickPath = new Path();
        tickPath.moveTo(1.5f * radius+strokeWidth, 2 * radius+strokeWidth);
        tickPath.lineTo(1.5f * radius + 0.3f * radius+strokeWidth, 2 * radius + 0.3f * radius+strokeWidth);
        tickPath.lineTo(2*radius+0.5f * radius+strokeWidth,2*radius-0.3f * radius+strokeWidth);
        tickPathMeasure = new PathMeasure(tickPath,false);
        //鎰熷徆鍙疯矾寰�
        Path commaPath1 = new Path();
        Path commaPath2 = new Path();
        commaPath1.moveTo(2f * radius+strokeWidth, 1.25f * radius+strokeWidth);
        commaPath1.lineTo(2f * radius+strokeWidth, 2.25f * radius+strokeWidth);
        commaPath2.moveTo(2f * radius+strokeWidth, 2.75f * radius+strokeWidth);
        commaPath2.lineTo(2f * radius+strokeWidth, 2.5f * radius+strokeWidth);
        commaPathMeasure1 = new PathMeasure(commaPath1,false);
        commaPathMeasure2 = new PathMeasure(commaPath2,false);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSpecSize + 10 * strokeWidth, heightSpecSize + 10 * strokeWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private float endAngle;
    private void init(){
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.argb(255, 15, 178, 255));
        circlePaint.setStrokeWidth(strokeWidth);
        circlePaint.setStyle(Paint.Style.STROKE);
        
        smallRectPaint.setAntiAlias(true);
        smallRectPaint.setColor(Color.argb(255, 15, 178, 255));
        smallRectPaint.setStrokeWidth(strokeWidth/2);
        smallRectPaint.setStyle(Paint.Style.STROKE);
        
        downRectPaint.setAntiAlias(true);
        downRectPaint.setColor(Color.argb(255, 15, 178, 255));
        downRectPaint.setStrokeWidth(strokeWidth);
        downRectPaint.setStyle(Paint.Style.FILL);

        commaPaint.setAntiAlias(true);
        commaPaint.setColor(Color.argb(255, 229, 57, 53));
        commaPaint.setStrokeWidth(strokeWidth);
        commaPaint.setStyle(Paint.Style.STROKE);
        
        tickPaint.setColor(Color.argb(255, 0, 150, 136));
        tickPaint.setAntiAlias(true);
        tickPaint.setStrokeWidth(strokeWidth);
        tickPaint.setStyle(Paint.Style.STROKE);
        
        //鎶涘嚭鍔ㄧ敾
        endAngle = (float) Math.atan(4f/3);
        mRotateAnimation = ValueAnimator.ofFloat(0f, endAngle*0.9f );
        mRotateAnimation.setDuration(500);
        mRotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mRotateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curSweepAngle = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mRotateAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                curSweepAngle = 0;
                if (isSuccess) {
                    status = 2;
                    mDownAnimation.start();
                } else {
                    status = 5;
                    mCommaAnimation.start();
                }
            }
        });

        //涓嬭惤鍔ㄧ敾        
        mDownAnimation = ValueAnimator.ofFloat(0f, 1f );
        mDownAnimation.setDuration(500);
        mDownAnimation.setInterpolator(new AccelerateInterpolator());
        mDownAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                downPrecent = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mDownAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                status = 3;
                mForkAnimation.start();
            }
        });

        //鍒嗗弶鍔ㄧ敾        
        mForkAnimation = ValueAnimator.ofFloat(0f, 1f );
        mForkAnimation.setDuration(100);
        mForkAnimation.setInterpolator(new LinearInterpolator());
        mForkAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                forkPrecent = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mForkAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mTickAnimation.start();
            }
        });

        //鎵撻挬鍔ㄧ敾        
        mTickAnimation = ValueAnimator.ofFloat(0f, 1f);
        mTickAnimation.setStartDelay(1000);
        mTickAnimation.setDuration(500);
        mTickAnimation.setInterpolator(new AccelerateInterpolator());
        mTickAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tickPrecent = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mTickAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                status = 4;
            }
        });
        
        //鎰熷徆鍙峰姩鐢�        
        mCommaAnimation = ValueAnimator.ofFloat(0f, 1f);
        mCommaAnimation.setDuration(300);
        mCommaAnimation.setInterpolator(new AccelerateInterpolator());
        mCommaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                commaPrecent = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mCommaAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                status = 6;
                mshockAnimation.start();
            }
        });

        //闇囧姩鍔ㄧ敾
        mshockAnimation = ValueAnimator.ofInt(-1, 0, 1, 0, -1, 0,1,0);
        mshockAnimation.setDuration(500);
        
        mshockAnimation.setInterpolator(new LinearInterpolator());
        mshockAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shockPrecent = (Integer) animation.getAnimatedValue();
                invalidate();
            }

        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (status){
            case 0:
                float precent = 1.0f*progress/maxProgress;
                canvas.drawArc(mRectF, startAngle-270*precent, -(60 + precent*300), false, circlePaint);
                break;
            case 1:
                drawSmallRectFly(canvas);
                break;
            case 2:
                drawRectDown(canvas);
                break;
            case 3:
                drawFork(canvas);
                break;
            case 4:
                drawTick(canvas);
                break;
            case 5:
                drawComma(canvas);
                break;
            case 6:
                drawShockComma(canvas);
                break;
        }
    }

    /**
     * 鎶涘嚭灏忔柟鍧�
     * @param canvas
     */
    private void drawSmallRectFly(Canvas canvas){
        canvas.save();
        canvas.translate(radius / 2 + strokeWidth, 2 * radius + strokeWidth);//灏嗗潗鏍囩Щ鍔ㄥ埌澶у渾鍦嗗績
        float bigRadius = 5*radius/2;//澶у渾鍗婂緞
        float x1 = (float) (bigRadius*Math.cos(curSweepAngle));
        float y1 = -(float) (bigRadius*Math.sin(curSweepAngle));
        float x2 = (float) (bigRadius*Math.cos(curSweepAngle+0.05*endAngle+0.1*endAngle*(1-curSweepAngle/0.9*endAngle)));//
        float y2 = -(float) (bigRadius*Math.sin(curSweepAngle+0.05*endAngle+0.1*endAngle*(1-curSweepAngle/0.9*endAngle)));
        canvas.drawLine(x1, y1, x2, y2, smallRectPaint);
        canvas.restore();
        circlePaint.setColor(Color.argb(255, 48, 63, 159));
        canvas.drawArc(mRectF, 0, 360, false, circlePaint);
    }

    /**
     * 缁樺埗涓嬭惤杩囩▼
     * @param canvas
     */
    private void drawRectDown(Canvas canvas){
        //涓嬭惤鏂瑰潡鐨勮捣濮嬬鍧愭爣
        float pos1[] = new float[2];
        float tan1[] = new float[2];
        downPathMeasure1.getPosTan(downPrecent * downPathMeasure1.getLength(), pos1, tan1);
        //涓嬭惤鏂瑰潡鐨勬湯绔潗鏍�
        float pos2[] = new float[2];
        float tan2[] = new float[2];
        downPathMeasure2.getPosTan(downPrecent * downPathMeasure2.getLength(), pos2, tan2);
        //妞渾褰㈠尯鍩�
        Rect mRect = new Rect(Math.round(mRectF.left),Math.round(mRectF.top+mRectF.height()*0.1f*downPrecent),
                Math.round(mRectF.right),Math.round(mRectF.bottom-mRectF.height()*0.1f*downPrecent));
        
        //闈炰氦闆�
        Region region1 = new Region(Math.round(pos1[0])-strokeWidth/4,Math.round(pos1[1]),Math.round(pos2[0]+strokeWidth/4),Math.round(pos2[1]));
        region1.op(mRect, Region.Op.DIFFERENCE);
        drawRegion(canvas, region1, downRectPaint);
        
        //浜ら泦
        Region region2 = new Region(Math.round(pos1[0])-strokeWidth/2,Math.round(pos1[1]),Math.round(pos2[0]+strokeWidth/2),Math.round(pos2[1]));
        boolean isINTERSECT = region2.op(mRect, Region.Op.INTERSECT);
        drawRegion(canvas, region2, downRectPaint);

        //妞渾褰㈠尯鍩�
        if(isINTERSECT) {//濡傛灉鏈変氦闆�
            float extrusionPrecent = (pos2[1]-radius)/radius;
            RectF rectF = new RectF(mRectF.left, mRectF.top + mRectF.height() * 0.1f * extrusionPrecent, mRectF.right, mRectF.bottom - mRectF.height() * 0.1f * extrusionPrecent);
            canvas.drawArc(rectF, 0, 360, false, circlePaint);
        }else{
            canvas.drawArc(mRectF, 0, 360, false, circlePaint);
        }
    }

    /**
     * 缁樺埗鍒嗗弶
     * @param canvas
     */
    private void drawFork(Canvas canvas) {
        float pos1[] = new float[2];
        float tan1[] = new float[2];
        forkPathMeasure1.getPosTan(forkPrecent * forkPathMeasure1.getLength(), pos1, tan1);
        float pos2[] = new float[2];
        float tan2[] = new float[2];
        forkPathMeasure2.getPosTan(forkPrecent * forkPathMeasure2.getLength(), pos2, tan2);
        float pos3[] = new float[2];
        float tan3[] = new float[2];
        forkPathMeasure3.getPosTan(forkPrecent * forkPathMeasure3.getLength(), pos3, tan3);
        
        canvas.drawLine(2 * radius+strokeWidth, radius+strokeWidth, 2 * radius+strokeWidth, 2 * radius+strokeWidth, downRectPaint);
        canvas.drawLine(2 * radius+strokeWidth, 2 * radius+strokeWidth, pos1[0], pos1[1], downRectPaint);
        canvas.drawLine(2 * radius+strokeWidth, 2 * radius+strokeWidth, pos2[0], pos2[1], downRectPaint);
        canvas.drawLine(2 * radius+strokeWidth, 2 * radius+strokeWidth, pos3[0], pos3[1], downRectPaint);
        //妞渾褰㈠尯鍩�
        RectF rectF = new RectF(mRectF.left, mRectF.top + mRectF.height() * 0.1f * (1-forkPrecent), 
                mRectF.right, mRectF.bottom - mRectF.height() * 0.1f * (1-forkPrecent));
        canvas.drawArc(rectF, 0, 360, false, circlePaint);
    }

    /**
     * 缁樺埗鎵撻挬
     * @param canvas
     */
    private void drawTick(Canvas canvas) {
        Path path = new Path();
        /*
         * On KITKAT and earlier releases, the resulting path may not display on a hardware-accelerated Canvas. 
         * A simple workaround is to add a single operation to this path, such as dst.rLineTo(0, 0).
         */
        tickPathMeasure.getSegment(0, tickPrecent * tickPathMeasure.getLength(), path, true);
        path.rLineTo(0, 0);
        canvas.drawPath(path, tickPaint);
        canvas.drawArc(mRectF, 0, 360, false, tickPaint);
    }

    /**
     * 缁樺埗鎰熷徆鍙�
     */
    private void drawComma(Canvas canvas) {
        Path path1 = new Path();
        commaPathMeasure1.getSegment(0, commaPrecent * commaPathMeasure1.getLength(), path1, true);
        path1.rLineTo(0, 0);
        Path path2 = new Path();
        commaPathMeasure2.getSegment(0, commaPrecent * commaPathMeasure2.getLength(), path2, true);
        path2.rLineTo(0, 0);
        canvas.drawPath(path1, commaPaint);
        canvas.drawPath(path2, commaPaint);
        canvas.drawArc(mRectF, 0, 360, false, commaPaint);
    }

    /**
     * 缁樺埗闇囧姩鏁堟灉
     * @param canvas
     */
    private void drawShockComma(Canvas canvas) {
        Path path1 = new Path();
        commaPathMeasure1.getSegment(0, commaPathMeasure1.getLength(), path1, true);
        path1.rLineTo(0, 0);
        Path path2 = new Path();
        commaPathMeasure2.getSegment(0, commaPathMeasure2.getLength(), path2, true);
        path2.rLineTo(0, 0);
        
        if (shockPrecent!=0){
            canvas.save();
            if (shockPrecent==1)
                canvas.rotate(shockDir, 2 * radius, 2 * radius);
            else if(shockPrecent==-1)
                canvas.rotate(-shockDir, 2 * radius, 2 * radius);
        }
        canvas.drawPath(path1, commaPaint);
        canvas.drawPath(path2, commaPaint);
        canvas.drawArc(mRectF, 0, 360, false, commaPaint);
        if (shockPrecent!=0) {
            canvas.restore();
        }
    }
    
    /**
     * 缁樺埗鍖哄煙
     * @param canvas
     * @param rgn
     * @param paint
     */
    private void drawRegion(Canvas canvas,Region rgn,Paint paint) {
        RegionIterator iter = new RegionIterator(rgn);
        Rect r = new Rect();
        while (iter.next(r)) {
            canvas.drawRect(r, paint);
        }
    }

    /**
     * 寮�濮嬪畬鎴愬姩鐢�
     */
    private void start(){
        post(new Runnable() {
            @Override
            public void run() {
                mRotateAnimation.start();
            }
        });
    }

    public void setProgress(int progress) {
        this.progress = Math.min(progress,maxProgress);
        postInvalidate();
        if (progress==0){
            status = 0;
        }
    }

    public int getProgress() {
        return progress;
    }

    /**
     * loading鎴愬姛鍚庤皟鐢�
     */
    public void finishSuccess() {
        setProgress(maxProgress);
        this.isSuccess = true;
        status = 1;
        start();
    }

    /**
     * loading澶辫触鍚庤皟鐢�
     */
    public void finishFail() {
        setProgress(maxProgress);
        this.isSuccess = false;
        status = 1;
        start();
    }
}
