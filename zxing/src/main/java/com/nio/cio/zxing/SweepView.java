package com.nio.cio.zxing;


import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SweepView extends FrameLayout {
    private SurfaceView sv;
    private ViewfinderView finder;
    private ImageButton buttonAlbums;
    private boolean isChecked;
    private ImageButton back_ib;

    public SweepView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public SweepView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public SweepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    private void init(final Context context) {
        setFitsSystemWindows(true);
        sv = new SurfaceView(context);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(params);
        addView(sv);
        finder = new ViewfinderView(context);
        finder.setLayoutParams(params);
        finder.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        addView(finder);
        LinearLayout layout = new LinearLayout(context);
        LayoutParams lt = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lt.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lt.bottomMargin = 32;
        addView(layout, lt);

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

        ImageButton flight_ib = new ImageButton(context);
        flight_ib.setBackgroundColor(0x00FFFFFF);
        flight_ib.setImageResource(R.drawable.ic_baseline_flash_off_24);
        flight_ib.setScaleType(ImageView.ScaleType.CENTER);
        flight_ib.setPadding(margin, margin, margin, margin);
        flight_ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton ib = (ImageButton) v;
                if (isChecked) {
                    ib.setImageResource(R.drawable.ic_baseline_flash_off_24);
                    isChecked = false;
                } else {
                    ib.setImageResource(R.drawable.ic_baseline_flash_on_24);
                    isChecked = true;
                }
                CameraManager.get().flashHandler();

            }
        });

        LinearLayout.LayoutParams check_lt = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addView(flight_ib, check_lt);
        buttonAlbums = new ImageButton(context);
        buttonAlbums.setBackgroundColor(0x00FFFFFF);
        buttonAlbums.setImageResource(R.drawable.ic_baseline_collections_24);
        buttonAlbums.setScaleType(ImageView.ScaleType.CENTER);
        buttonAlbums.setPadding(margin, margin, margin, margin);

        LinearLayout.LayoutParams Albums_lt = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Albums_lt.leftMargin = margin;
        layout.addView(buttonAlbums, Albums_lt);
        back_ib = new ImageButton(context);
        back_ib.setBackgroundColor(0x00FFFFFF);
        back_ib.setImageResource(R.drawable.ic_baseline_close_24);
        back_ib.setScaleType(ImageView.ScaleType.FIT_XY);
        back_ib.setPadding(margin, margin, margin, margin);
        LayoutParams back_lt = new LayoutParams(
                size, size);
        back_lt.gravity = Gravity.TOP | Gravity.RIGHT;
        back_lt.topMargin = margin;
        back_lt.rightMargin = margin;
        addView(back_ib, back_lt);
    }

    public ImageButton getButtonAlbums() {
        return buttonAlbums;
    }

    public ImageButton getBackIB() {
        return back_ib;
    }

    public SurfaceView getSurfaceView() {
        if (null != sv)
            return sv;
        return null;
    }

    public ViewfinderView getViewfinderView() {
        if (null != finder)
            return finder;
        return null;
    }

}
