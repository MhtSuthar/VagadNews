package com.vagad.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by ubuntu on 13/7/16.
 */
public class AnimationUtils {

    private static final int DELAY = 100;
    private static final int ANIM = 400;

    public AnimationUtils() {
    }

    public static void animateIn(View view) {
        if (view instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) view;
            for (int i = 0; i < relativeLayout.getChildCount(); i++) {
                View child = relativeLayout.getChildAt(i);
                child.animate()
                        .setStartDelay(100 + i * DELAY)
                        .alpha(1)
                        .scaleX(1)
                        .scaleY(1);
            }
        } else {
            LinearLayout linearLayout = (LinearLayout) view;
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                View child = linearLayout.getChildAt(i);
                child.animate()
                        .setStartDelay(100 + i * DELAY)
                        .alpha(1)
                        .scaleX(1)
                        .scaleY(1);
            }
        }
    }

    public static void animateScaleOut(final View view) {
        if (!view.isShown()) {
            view.setScaleX(0.2f);
            view.setScaleY(0.2f);
            view.animate()
                    .setStartDelay(200)
                    .alpha(1)
                    .scaleX(1)
                    .scaleY(1).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }


    public static void animateScaleIn(final View view) {
        view.setScaleX(1);
        view.setScaleY(1);
        view.animate()
                .setStartDelay(100)
                .alpha(1)
                .scaleX(0.1f)
                .scaleY(0.1f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public static void animateOut(View view) {
        if (view instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) view;
            for (int i = 0; i < relativeLayout.getChildCount(); i++) {
                View child = relativeLayout.getChildAt(i);
                child.animate()
                        .setStartDelay(i)
                        .alpha(0)
                        .scaleX(0f)
                        .scaleY(0f);
            }
        } else {
            LinearLayout linearLayout = (LinearLayout) view;
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                View child = linearLayout.getChildAt(i);
                child.animate()
                        .setStartDelay(i)
                        .alpha(0)
                        .scaleX(0f)
                        .scaleY(0f);
            }
        }

    }

    public void translateAnim(View view) {
        if (view instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) view;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(relativeLayout);
            }
        } else {
            LinearLayout linearLayout = (LinearLayout) view;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(linearLayout);
            }
        }
    }

    public static void revealColorFromCoordinates(int x, int y, View viewRoot) {
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());
        Animator anim = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        }
        //viewRoot.setBackgroundColor(ContextCompat.getColor(viewRoot.getContext(), R.color.colorPrimary));
        anim.setDuration(1000);
        anim.start();
    }

    public void changeColor(final View view, int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    public static void changeGradientColor(final View view, int colorFrom, int colorTo) {
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                colorFrom,
                colorTo);

        final GradientDrawable background = (GradientDrawable) view.getBackground();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                background.setColor((Integer) animator.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(250);
        valueAnimator.start();
    }

    public static void expand(final View v) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(ANIM);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(ANIM);
        v.startAnimation(a);
    }

    public static void rotate0to180(View view) {
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(ANIM);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    public static void rotate180to360(View view) {
        RotateAnimation rotate = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(ANIM);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    public static void delayTransaction(ViewGroup viewGroup) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(viewGroup);
        }
    }

    public static void runEnterAnimation(View view, int height) {
        view.setTranslationY(height);
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                })
                .start();
    }

}
