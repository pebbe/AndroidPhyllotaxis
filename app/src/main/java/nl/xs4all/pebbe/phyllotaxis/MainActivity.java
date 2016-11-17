package nl.xs4all.pebbe.phyllotaxis;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;

import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final double angleDefault = 360.0/pow((sqrt(5.0) + 1.0)/2.0, 2.0);
    private double angle;
    private float size = 4;
    private float density = 1;
    private boolean ready = false;

    private SurfaceHolder holder;
    private SeekBar seekbar;

    private final static String angelState = "nl.xs4all.pebbe.phyllotaxis.ANGLE";

    private static ReentrantLock lock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ready = false;
        angle = angleDefault;
        density = getResources().getDisplayMetrics().density;
        if (savedInstanceState != null) {
            angle = savedInstanceState.getDouble(angelState, angleDefault);
        }

        SurfaceView surface = (SurfaceView) findViewById(R.id.surfaceView);
        holder = surface.getHolder();
        holder.addCallback(this);

        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == (int)angleDefault) {
                    angle = angleDefault;
                } else {
                    angle = i;
                }
                //f (! b) {
                    draw();
                //}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                draw();
            }
        });
    }

    protected void reset(View view) {
        angle = angleDefault;
        seekbar.setProgress((int)angleDefault);
    }

    private void draw() {
        if (! ready) {
            return;
        }
        if (lock.tryLock()) {
            ready = false;
            lock.unlock();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Canvas canvas = holder.lockCanvas();

                    int height = canvas.getHeight();
                    int width = canvas.getWidth();

                    Paint p = new Paint();

                    p.setColor(Color.WHITE);
                    canvas.drawRect(0, 0, (float) width, (float) height, p);

                    p.setColor(Color.LTGRAY);
                    p.setStrokeWidth(density * 4);
                    p.setStyle(Paint.Style.STROKE);
                    canvas.drawRect(0, 0, (float) width, (float) height, p);

                    p.setColor(Color.parseColor("#FF4081"));
                    p.setStyle(Paint.Style.FILL);
                    int x0 = width / 2;
                    int y0 = height / 2;
                    double r = 0;
                    double myAngle = angle;
                    for (int i = 0; i < 500; i++) {
                        float d = (float) (density * 2 * size * sqrt(i));
                        float a = (float) (r / 180 * PI);
                        float x = (float) (x0 + d * sin(a));
                        float y = (float) (y0 + d * cos(a));
                        r += myAngle;
                        canvas.drawCircle(x, y, density * size, p);
                    }

                    holder.unlockCanvasAndPost(canvas);
                    ready = true;
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        holder = surfaceHolder;
        ready = true;
        draw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        surfaceCreated(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        ready = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(angelState, angle);
    }


}
