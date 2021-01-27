# Qzxing
二维码直接扫码或者扫描本地存储的二维码图片、二维码生成、闪光灯，用到读写相机权限需要自己加
>
调用
>ZxingUtils.IntentSweepSend(MainActivity.this);
>
回调
>ZxingUtils.onSweepResult(requestCode, resultCode, data, new ZxingUtils.OnSweepResultListener() {
>               @Override
>              public void onResult(String result) {
>                   if (result != null)
>                       mTextMessage.setText(result);
>               }
>          });
>       
<img src="https://github.com/nicccccccccce/documents/blob/master/Screenshot_20210127-133806_Qzxing.jpg" height="50%" width="50%" />
<img src="https://github.com/nicccccccccce/documents/blob/master/zxing-001.gif" height="50%" width="50%" />
