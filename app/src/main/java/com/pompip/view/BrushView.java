package com.pompip.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BrushView extends View {
    private Paint brush = new Paint();
    private List<PathList> list = new ArrayList<>();
    private int top = -1;

    class PathList {
        /* access modifiers changed from: private */
        public Paint brush;
        /* access modifiers changed from: private */
        public Path path;

        public PathList(Path path2, Paint paint) {
            this.path = path2;
            this.brush = paint;
        }
    }

    public BrushView(Context context) {
        super(context);
        this.brush.setAntiAlias(true);
        this.brush.setColor(-16776961);
        this.brush.setStyle(Style.STROKE);
        this.brush.setStrokeJoin(Join.ROUND);
        this.brush.setStrokeWidth(10.0f);
        setBackgroundColor(-1);
    }

    public BrushView(Context context, @Nullable AttributeSet attrs) {
        this(context);
    }

    public BrushView(Context context, @Nullable AttributeSet attrs, Paint brush) {
        this(context);
    }

    public BrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, Paint brush) {
        this(context);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                top++;
                list.add(new PathList(new Path(), new Paint(brush)));
                list.get(top).path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_UP:
                return false;
            case MotionEvent.ACTION_MOVE:
                list.get(top).path.lineTo(pointX, pointY);
                postInvalidate();
                return false;
            default:
                Toast.makeText(getContext(), "action: "+event.getAction()+" x:"+pointX+" y:"+pointY, Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (int i = 0; i < this.list.size(); i++) {
            canvas.drawPath(list.get(i).path, list.get(i).brush);
        }
    }

    public void setBrushColor(int color) {
        this.brush.setColor(color);
    }

    public void setBrushSize(float size) {
        this.brush.setStrokeWidth(size);
    }

    public float getCurrentBrushSize() {
        return this.brush.getStrokeWidth();
    }

    public int getCurrentBrushColor() {
        return this.brush.getColor();
    }

    public void restorePreference(int color, float size) {
        this.brush.setColor(color);
        this.brush.setStrokeWidth(size);
    }

    public void clearAll() {
        this.list.clear();
        this.top = -1;
        postInvalidate();
    }

    public List<PathList> getPath() {
        return this.list;
    }

    public void setPath(List<PathList> paths) {
        this.list = paths;
        this.top = this.list.size() - 1;
        postInvalidate();
    }

    public void pop() {
        if (this.top >= 0) {
            List<PathList> list2 = this.list;
            int i = this.top;
            this.top = i - 1;
            list2.remove(i);
            postInvalidate();
        }
    }

    public Bitmap createViewBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        draw(new Canvas(bitmap));
        return bitmap;
    }
}