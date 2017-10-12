package me.jun.presscompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

/**
 * 解决 Button 设置点击效果需要兼容不同系统版本的蛋疼问题
 * <p/>
 * author shenwenjun
 * date 19/07/2017
 */

public final class PressCompat {

    private final static int[] PRESS_STATE_SET = new int[] { android.R.attr.state_pressed };
    private final static int[] DEFAULT_STATE_SET = new int[] {};
    private final static PressImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            IMPL = new LollipopPressImpl();
        } else {
            IMPL = new HoneycombPressImpl();
        }
    }

    private PressCompat() {
    }

    public static void pressableBackground(View view) {
        IMPL.pressableBackground(view);
    }

    public static Drawable getPressableDrawable(@Nullable Drawable original, @ColorInt int color) {
        return IMPL.getPressableDrawable(original, color);
    }

    interface PressImpl {

        void pressableBackground(View view);

        Drawable getPressableDrawable(@Nullable Drawable original, @ColorInt int color);
    }

    private abstract static class BasePressImpl implements PressImpl {

        @Override
        public void pressableBackground(View view) {
            Drawable background = view.getBackground();
            int color = getPressColorInTheme(view.getContext());
            Drawable newBackground = getPressableDrawable(background, color);
            view.setBackground(newBackground);
            //newBackground.invalidateSelf(); // FIXME: 12/10/2017
        }

        private static int getPressColorInTheme(Context context) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorControlHighlight, value, true);
            return ContextCompat.getColor(context, value.resourceId);
        }
    }

    private static class LollipopPressImpl extends BasePressImpl {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public Drawable getPressableDrawable(@Nullable Drawable original, @ColorInt int color) {
            return new RippleDrawable(ColorStateList.valueOf(color), original, original);
        }
    }

    private static class HoneycombPressImpl extends BasePressImpl {

        @Override
        public Drawable getPressableDrawable(@Nullable Drawable original, @ColorInt int color) {
            FilterableStateListDrawable drawable = new FilterableStateListDrawable();
            if (original == null) {
                drawable.addState(PRESS_STATE_SET, new ColorDrawable(color));
                return drawable;
            }
            if (original instanceof ColorDrawable) {
                drawable.addState(PRESS_STATE_SET, new ColorDrawable(color));
            } else {
                drawable.addState(PRESS_STATE_SET, original, color);
            }
            drawable.addState(DEFAULT_STATE_SET, original);
            return drawable;
        }
    }
}
