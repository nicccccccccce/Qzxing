package com.nio.cio.zxing;


import android.content.Context;
import android.util.AttributeSet;
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


        ImageButton flight_ib = new ImageButton(context);
        flight_ib.setBackgroundColor(0x00FFFFFF);
        flight_ib.setImageResource(R.mipmap.ic_zxing_flight_s);
        flight_ib.setScaleType(ImageView.ScaleType.FIT_CENTER);
        flight_ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton ib = (ImageButton) v;
                if (isChecked) {
                    ib.setImageResource(R.mipmap.ic_zxing_flight_s);
                    isChecked = false;
                } else {
                    ib.setImageResource(R.mipmap.ic_zxing_flight);
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
        buttonAlbums.setImageResource(R.mipmap.ic_zxing_photo);
        buttonAlbums.setScaleType(ImageView.ScaleType.CENTER);

        LinearLayout.LayoutParams Albums_lt = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Albums_lt.leftMargin = 16;
        layout.addView(buttonAlbums, Albums_lt);
        back_ib = new ImageButton(context);
        back_ib.setBackgroundColor(0x00FFFFFF);
        back_ib.setImageResource(R.mipmap.ic_zxing_delete);
        back_ib.setScaleType(ImageView.ScaleType.FIT_CENTER);
        back_ib.setPadding(16, 16, 16, 16);
        LayoutParams back_lt = new LayoutParams(
                112, 112);
        back_lt.gravity = Gravity.TOP | Gravity.RIGHT;
        back_lt.topMargin = 16;
        back_lt.rightMargin = 16;
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
