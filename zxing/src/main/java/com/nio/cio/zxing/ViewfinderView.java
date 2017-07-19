package com.nio.cio.zxing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

public final class ViewfinderView extends View {

    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    private static final int CORNER_WIDTH = 16;

    private static final int MIDDLE_LINE_PADDING = 3;

    private static final int SPEEN_DISTANCE = 5;

    private static float density;

    private static final int TEXT_SIZE = 13;

    private static final int TEXT_PADDING_TOP = 30;

    private final Paint paint;

    private Bitmap resultBitmap;


    private final int transparent;

    private final int translucence;

    private final int resultPointColor;

    private Collection<ResultPoint> possibleResultPoints;

    private Collection<ResultPoint> lastPossibleResultPoints;

    private int slideTop;

    private int slideBottom;
    private boolean isFirst;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        paint = new Paint();
        Resources resources = getResources();
        transparent = resources.getColor(R.color.transparent);
        translucence = resources.getColor(R.color.translucence);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);

    }

    public ViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        density = context.getResources().getDisplayMetrics().density;
        paint = new Paint();
        Resources resources = getResources();
        transparent = resources.getColor(R.color.transparent);
        translucence = resources.getColor(R.color.translucence);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);
    }

    public ViewfinderView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        density = context.getResources().getDisplayMetrics().density;
        paint = new Paint();
        Resources resources = getResources();
        transparent = resources.getColor(R.color.transparent);
        translucence = resources.getColor(R.color.translucence);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);
    }

    @Override
    public void onDraw(Canvas canvas) {

        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }

        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top + CORNER_WIDTH;
            slideBottom = frame.bottom - CORNER_WIDTH;
        }


        int width = canvas.getWidth();
        int height = canvas.getHeight();


        paint.setColor(translucence);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left + 2, frame.bottom + 1, paint);
        canvas.drawRect(frame.right - 4, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
        paint.setColor(translucence);
        if (resultBitmap != null) {
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            Rect bigRect = new Rect();
            bigRect.left = frame.left;
            bigRect.right = frame.right;
            bigRect.top = frame.top;
            bigRect.bottom = frame.bottom;
            Drawable drawable = getResources().getDrawable(
                    R.mipmap.scanning_frame);
            BitmapDrawable b = (BitmapDrawable) drawable;
            canvas.drawBitmap(b.getBitmap(), null, bigRect, paint);


            slideTop += SPEEN_DISTANCE;
            if (slideTop >= slideBottom) {
                slideTop = frame.top + CORNER_WIDTH;
            }


            Paint paint1 = new Paint();
            paint1.setDither(true);
            paint1.setStyle(Style.FILL);
            paint1.setAntiAlias(true);
            int themeColor = getColorPrimary();
            int[] shadows_colors = null;
            if (themeColor == 0)
                shadows_colors = getShadowColors(0xFF0000FF);
            shadows_colors = getShadowColors(themeColor);
            Shader mShader = new LinearGradient(frame.left, slideTop,
                    frame.right, slideTop, shadows_colors, null,
                    Shader.TileMode.REPEAT);
            paint1.setShader(mShader);
            paint1.setStrokeWidth(MIDDLE_LINE_PADDING);
            canvas.drawLine(frame.left, slideTop, frame.right, slideTop, paint1);


            paint.setColor(Color.WHITE);
            paint.setTextSize(TEXT_SIZE * density);
            paint.setTextAlign(Align.CENTER);
            paint.setAlpha(0x80);
            paint.setTypeface(Typeface.create("System", Typeface.BOLD));
            canvas.drawText(getResources().getString(R.string.scanning_hint),
                    width / 2, frame.bottom + TEXT_PADDING_TOP * density, paint);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }


            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
        }
    }

    private int[] getShadowColors(int themeColor) {
        return new int[]{getAlphaColor(themeColor, 0.0f), getAlphaColor(themeColor, 0.6f),
                getAlphaColor(themeColor, 1.0f), getAlphaColor(themeColor, 0.6f), getAlphaColor(themeColor, 0.0f)};
    }

    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    private int getAlphaColor(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
