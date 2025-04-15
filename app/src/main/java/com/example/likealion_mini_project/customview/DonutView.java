package com.example.likealion_mini_project.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.likealion_mini_project.R;

public class DonutView extends View {
    Context context;
    int viewColor;

    int score;


    public DonutView(Context context) {
        super(context);

        this.context = context;

        init(null);
    }

    public DonutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        init(attrs);
    }

    public DonutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DonutView);
            viewColor = typedArray.getColor(R.styleable.DonutView_customColor, Color.BLACK);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        Paint circlePaint = new Paint();
        Paint arcPaint = new Paint();

        float strokeWidth = context.getResources().getDisplayMetrics().density * 5f;

        float cx = (float) getWidth() / 2;
        float cy = (float) getHeight() / 2;

        float radius = (float) getWidth() / 4;

        RectF rectF = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
        float sweepAngle = 360f * (score / 100f);

        circlePaint.setColor(Color.GRAY);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(strokeWidth);

        arcPaint.setColor(viewColor);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(strokeWidth);

        canvas.drawCircle(cx, cy, radius, circlePaint);
        canvas.drawArc(rectF, -90f, sweepAngle, false, arcPaint);
    }

    public void setScore(int score) {
        this.score = score;
        invalidate();
    }
}
