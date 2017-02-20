package com.vagad.utils;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private final GestureDetector gestureDetector;

    public RecyclerTouchListener(final RecyclerView recyclerView, final OnRecyclerClickListener recyclerClickListener) {
        gestureDetector = new GestureDetector(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (recyclerClickListener == null) return false;
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    recyclerClickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (recyclerClickListener == null) return;
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    recyclerClickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface OnRecyclerClickListener {
        void onClick(View v, int position);
        void onLongClick(View v, int position);
    }
}