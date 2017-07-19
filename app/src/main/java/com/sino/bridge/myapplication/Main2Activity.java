package com.sino.bridge.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.nio.cio.zxing.ZxingUtils;

public class Main2Activity extends AppCompatActivity {
    private TextInputEditText mTextInputEditText;
    private ImageView mImageView;
    private AppCompatButton mAppCompatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextInputEditText = (TextInputEditText) findViewById(R.id.tv_editext);
        mImageView = (ImageView) findViewById(R.id.imageView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mAppCompatButton = (AppCompatButton) findViewById(R.id.c_button);
        mAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ti = mTextInputEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(ti)) {
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_pay  );
                    Bitmap BmNew = null;//ZxingUtils.createQRAndLogo(ti, bm, 0xFF445566);
                    try {
                        BmNew = ZxingUtils.createLogoQRCode(ti, bm);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    if (BmNew != null)
                        mImageView.setImageBitmap(BmNew);
                }
            }
        });
    }

}
