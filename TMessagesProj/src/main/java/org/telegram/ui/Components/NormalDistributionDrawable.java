package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NormalDistributionDrawable extends Drawable {
    private final Paint paint;
    private final Paint fillPaint;
    private float stdDevFactor = 8f; // Default divisor for stdDev

    public NormalDistributionDrawable() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setAntiAlias(true);

        fillPaint = new Paint();
        fillPaint.setColor(Color.BLACK);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
    }

    public void setStdDevFactor(float factor) {
        this.stdDevFactor = factor;
        invalidateSelf();
    }

    public float getStdDevFactor() {
        return stdDevFactor;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        float mean = width / 2f;
        float stdDev = width / stdDevFactor; // Use adjustable stdDev

        Path path = new Path();
        boolean started = false;

        for (int x = 0; x <= width; x++) {
            double z = (x - mean) / stdDev;
            double yNorm = Math.exp(-0.5 * z * z) / (Math.sqrt(2 * Math.PI));
            float y = (float) (height - (yNorm * height * 0.9f));
            if (!started) {
                path.moveTo(x, height);
                path.lineTo(x, y);
                started = true;
            } else {
                path.lineTo(x, y);
            }
        }
        path.lineTo(width, height);
        path.close();
        canvas.drawPath(path, fillPaint);
        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        fillPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable android.graphics.ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        fillPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.OPAQUE;
    }
}
