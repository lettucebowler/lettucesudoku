package com.lettucebowler.lettucesudoku;

import android.content.Context;
import android.util.AttributeSet;

import androidx.gridlayout.widget.GridLayout;

public class CustomViews {
    public static class SquareTextView extends androidx.appcompat.widget.AppCompatTextView{
        public SquareTextView(Context context) {
            super(context);
        }

        public SquareTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = (int)((double) MeasureSpec.getSize(widthMeasureSpec) / 9.5);
            int height = (int)((double) MeasureSpec.getSize(widthMeasureSpec) / 9.5);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square
        }
    }

    public static class SquareButton extends androidx.appcompat.widget.AppCompatButton {
        public SquareButton(Context context) {
            super(context);
        }

        public SquareButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square
        }
    }

    public static class SquareImageButton extends androidx.appcompat.widget.AppCompatImageButton {
        public SquareImageButton(Context context) {
            super(context);
        }

        public SquareImageButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square
        }
    }

    public static class SquareGridLayout extends GridLayout {

        public SquareGridLayout(Context context) {
            super(context);
        }

        public SquareGridLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(width, height);
            setMeasuredDimension(size, size); // make it square
        }
    }
}
