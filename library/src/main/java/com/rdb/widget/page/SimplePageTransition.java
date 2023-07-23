package com.rdb.widget.page;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class SimplePageTransition extends PageTransition {

    private TransitionType leftOutType;
    private TransitionType leftInType;
    private TransitionType rightOutType;
    private TransitionType rightInType;

    public SimplePageTransition(TransitionType type) {
        updateTransition(type);
    }

    public SimplePageTransition(TransitionType in, TransitionType out) {
        updateTransition(in, out);
    }

    public SimplePageTransition(TransitionType leftOut, TransitionType leftIn, TransitionType rightOut, TransitionType rightIn) {
        updateTransition(leftOut, leftIn, rightOut, rightIn);
    }

    public void updateTransition(TransitionType type) {
        leftOutType = type;
        leftInType = type;
        rightOutType = type;
        rightInType = type;
        checkInType();
    }

    public void updateTransition(TransitionType in, TransitionType out) {
        leftOutType = out;
        leftInType = in;
        rightOutType = out;
        rightInType = in;
        checkInType();
    }

    public void updateTransition(TransitionType leftOut, TransitionType leftIn, TransitionType rightOut, TransitionType rightIn) {
        leftOutType = leftOut;
        leftInType = leftIn;
        rightOutType = rightOut;
        rightInType = rightIn;
        checkInType();
    }


    private void checkInType() {
        if (leftInType == TransitionType.STAY) {
            leftInType = TransitionType.SLIDE;
        }
        if (rightInType == TransitionType.STAY) {
            rightInType = TransitionType.SLIDE;
        }
    }

    @Override
    protected void onTransition(Canvas canvas, Paint paint, Bitmap in, Bitmap out, float moveX) {
        if (moveX > 0) {
            if (rightOutType == TransitionType.STAY) {
                doTransition(canvas, paint, out, rightOutType, moveX);
                doTransition(canvas, paint, in, leftInType, -in.getWidth() + moveX);
            } else {
                doTransition(canvas, paint, in, leftInType, -in.getWidth() + moveX);
                doTransition(canvas, paint, out, rightOutType, moveX);
            }
        } else if (moveX < 0) {
            if (leftOutType == TransitionType.STAY) {
                doTransition(canvas, paint, out, leftOutType, moveX);
                doTransition(canvas, paint, in, rightInType, in.getWidth() + moveX);
            } else {
                doTransition(canvas, paint, in, rightInType, in.getWidth() + moveX);
                doTransition(canvas, paint, out, leftOutType, moveX);
            }
        }
    }

    private void doTransition(Canvas canvas, Paint paint, Bitmap bitmap, TransitionType type, float startX) {
        if (type == TransitionType.STAY) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        } else if (type == TransitionType.SLIDE) {
            canvas.drawBitmap(bitmap, startX, 0, paint);
        } else if (type == TransitionType.FOLD) {
            drawFold(canvas, paint, bitmap, startX, 6);
        }
    }


    private void drawFold(Canvas canvas, Paint paint, Bitmap bitmap, float startX, int foldCount) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float foldWidth = width / foldCount;
        float curWidth = startX < 0 ? width + startX : width - startX;
        float curFoldWidth = curWidth / foldCount;
        startX = startX < 0 ? 0 : startX;
        int deepth = (int) Math.sqrt(foldWidth * foldWidth - curFoldWidth * curFoldWidth) / 2;
        Matrix matrix = new Matrix();
        float[] src = new float[8];
        float[] dst = new float[8];
        for (int i = 0; i < foldCount; i++) {
            src[0] = i * foldWidth;
            src[1] = 0;
            src[2] = src[0] + foldWidth;
            src[3] = 0;
            src[4] = src[2];
            src[5] = height;
            src[6] = src[0];
            src[7] = src[5];

            boolean isEven = (i % 2 == 0);
            dst[0] = startX + i * curFoldWidth;
            dst[1] = isEven ? 0 : deepth;
            dst[2] = dst[0] + curFoldWidth;
            dst[3] = isEven ? deepth : 0;
            dst[4] = dst[2];
            dst[5] = isEven ? height - deepth : height;
            dst[6] = dst[0];
            dst[7] = isEven ? height : height - deepth;
            matrix.setPolyToPoly(src, 0, dst, 0, 4);
            canvas.save();
            canvas.concat(matrix);
            canvas.clipRect(foldWidth * i, 0, foldWidth * i + foldWidth, height);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            canvas.restore();
            matrix.reset();
        }
    }

    public TransitionType getLeftOutType() {
        return leftOutType;
    }

    public TransitionType getLeftInType() {
        return leftInType;
    }

    public TransitionType getRightOutType() {
        return rightOutType;
    }

    public TransitionType getRightInType() {
        return rightInType;
    }
}
