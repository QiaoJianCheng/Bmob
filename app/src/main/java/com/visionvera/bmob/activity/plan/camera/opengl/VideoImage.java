package com.visionvera.bmob.activity.plan.camera.opengl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;

import com.visionvera.bmob.utils.LogUtil;

/***
 * 视频显示控件封装
 *
 * @author gavin
 *
 */
public class VideoImage extends GLSurfaceView implements SurfaceHolder.Callback, ISimplePlayer {

    private GLFrameRenderer _renderer = null;

    private SurfaceHolder _sh = null;
    private boolean _ScaleChanged = false;
    private int _width;
    private int _height;
    public Bitmap bmp = null;

    private static Matrix _matrix = new Matrix();
    public ScaleMode Scale = ScaleMode.Fit;
    public boolean OpenGLMode = true;// 该值为true时，视频通话捕获视频会受影响,直播暂没有发现有影响

    public VideoImage(Context context) {
        super(context);
        Init();
    }

    public VideoImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    private void Init() {

        BindHolder(getHolder());
        if (OpenGLMode) {
            this.setEGLContextClientVersion(2);
            DisplayMetrics dm = new DisplayMetrics();
            dm.widthPixels = 100;
            dm.heightPixels = 100;
            _renderer = new GLFrameRenderer(this, this, dm);
            this.setRenderer(_renderer);
        }
    }

    public void OpenGLStatus(boolean on) {
        if (on) {
            if (!OpenGLMode) {
                this.setEGLContextClientVersion(2);
                DisplayMetrics dm = new DisplayMetrics();
                dm.widthPixels = 100;
                dm.heightPixels = 100;
                _renderer = new GLFrameRenderer(this, this, dm);
                this.setRenderer(_renderer);
            }
            OpenGLMode = true;
        } else {
            OpenGLMode = false;
        }

    }

    @Override
    public SurfaceHolder getHolder() {

        if (_sh == null)
            return super.getHolder();
        else {
            return _sh;
        }

    }

    private void BindHolder(SurfaceHolder sh) {
        _sh = sh;
        sh.addCallback(this);
        // sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void SetVideoSize(int width, int height) {
        _width = width;
        _height = height;

    }

    // public void ScreenSize(int width, int height) {
    // mScreenWidth = width;
    // mScreenHeight = height;
    // }
    public void Play(Bitmap bmp) {
        this.bmp = bmp;
        drawImg(bmp);
    }

    public void Play(VideoDisplayFrame vdFrame) {
        if (OpenGLMode) {
            if (_width != vdFrame.Width || _height != vdFrame.Height) {
                _width = vdFrame.Width;
                _height = vdFrame.Height;
                _renderer.ScreenSize(this.getMeasuredWidth(), this.getMeasuredHeight());
                _renderer.update(_width, _height);
            }
            _renderer.update(vdFrame.YUV, vdFrame.YUVOffset, vdFrame.YUVLength);
        } else {
            Play(vdFrame.BMP);
        }
    }

    public void UpdateSize() {
        if (OpenGLMode) {
            _renderer.ScreenSize(this.getMeasuredWidth(), this.getMeasuredHeight());
            _renderer.update(_width, _height);
        }
    }

    public void SetScaleMode(ScaleMode mode) {

        this.Scale = mode;
        _ScaleChanged = true;

        if (OpenGLMode) {

            _renderer.IsFullScreen = mode == ScaleMode.FullScreen;
            UpdateSize();
        }
    }

    public ScaleMode GetScaleMode() {

        return this.Scale;
    }

    // 画图方法
    @SuppressLint("NewApi")
    protected void drawImg(final Bitmap bmp) {
        if (_sh == null)
            return;
        Canvas canvas = _sh.lockCanvas();
        if (canvas == null) {
            this.post(new Runnable() {
                public void run() {
                    VideoImage.this.setBackground(new BitmapDrawable(bmp));
                }
            });
            return;
        } else {
            try {
                if (bmp != null) {
                    // 生成合适的图像
                    // canvas.drawBitmap(bmp, null, null);
                    // Rect rect = new Rect(0, 0, 352, 288);
                    // canvas.drawBitmap(bmp, null, rect, null);
                    if (this.Scale == ScaleMode.FullScreen)
                        canvas.drawBitmap(bmp, null, canvas.getClipBounds(), null);
                    else if (this.Scale == ScaleMode.Fit) {
                        Rect rect = canvas.getClipBounds();
                        rect = ScaleSize(rect, bmp.getWidth(), bmp.getHeight());
                        canvas.drawARGB(0xff, 00, 00, 00);
                        canvas.drawBitmap(bmp, null, rect, null);
                    } else if (this.Scale == ScaleMode.Original) {
                        canvas.drawBitmap(bmp, null, canvas.getClipBounds(), null);
                    }

                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                _sh.unlockCanvasAndPost(canvas);
            }
        }

    }

    // 画图方法
    @SuppressLint("NewApi")
    private void drawImg() {
        if (_sh == null)
            return;
        Canvas canvas = _sh.lockCanvas();
        if (canvas == null) {
            this.post(new Runnable() {
                public void run() {
                    VideoImage.this.setBackground(new BitmapDrawable(bmp));
                }
            });
            return;
        } else {
            try {

                // canvas.drawColor(0x000000);
                // canvas.drawARGB(0xff, 00, 00, 00);
                int w = this.getWidth();
                int h = this.getHeight();

                Bitmap bmp1 = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
                int w1 = bmp1.getWidth();
                Rect rect = canvas.getClipBounds();
                canvas.drawBitmap(bmp1, null, canvas.getClipBounds(), null);
                bmp1.recycle();
                bmp1 = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

                canvas.drawBitmap(bmp1, null, canvas.getClipBounds(), null);
                bmp1.recycle();

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                _sh.unlockCanvasAndPost(canvas);
            }
        }
    }

    // 缩放并保持比例
    protected Rect ScaleSize(Rect rect, int sWidth, int sHeight) {
        int tWidth = rect.width();
        int tHeight = rect.height();

        float sProportion = (float) sWidth / (float) sHeight;// 原始比例
        float tProportion = (float) tWidth / (float) tHeight;// 目标比例
        if (tProportion > 1) {// 宽大
            tWidth = (int) (((float) tHeight / (float) sHeight) * sWidth);
        } else {
            tHeight = (int) (((float) tWidth / (float) sWidth) * sHeight);
        }

        int tTop = (rect.height() - tHeight) / 2;
        int tLeft = (rect.width() - tWidth) / 2;
        return new Rect(tLeft, tTop, tLeft + tWidth, tTop + tHeight);
    }

    public void Clean() {
        int w = this.getWidth();
        int h = this.getHeight();
        if (!OpenGLMode) {
            try {
                Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
                drawImg(bmp);
                bmp.recycle();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // 缩放图片
    private Bitmap getReduceBitmap(Bitmap bitmap, int w, int h) {

        int width = bitmap.getWidth();
        int hight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float wScake = ((float) w / width);
        float hScake = ((float) h / hight);
        matrix.postScale(wScake, hScake);
        return Bitmap.createBitmap(bitmap, 0, 0, width, hight, matrix, true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (!this.isInEditMode()) {
            BindHolder(holder);
        }
        // _surface = _sh.getSurface();
        if (OpenGLMode)
            super.surfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!this.isInEditMode()) {
            BindHolder(holder);
        }
        if (OpenGLMode)
            super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!this.isInEditMode()) {
            _sh = null;
        }
        if (OpenGLMode)
            super.surfaceDestroyed(holder);
    }

    @Override
    public void onPlayStart() {
        LogUtil.d("VideoImage.onPlayStart", "onPlayStart");
    }

    @Override
    public void onReceiveState(int state) {
        LogUtil.d("VideoImage.onReceiveState", state);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (OpenGLMode)
            setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public enum ScaleMode {
        // 原始尺寸
        Original(0),
        // 适合尺寸
        Fit(1),
        // 满屏
        FullScreen(2);

        private int id;

        ScaleMode(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

}
