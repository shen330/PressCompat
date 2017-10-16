package me.jun.presscompat;

import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.SparseArray;

/**
 * Thanks the answer by Daniele Segato on <a href="https://stackoverflow.com/questions/6018602/statelistdrawable-to-switch-colorfilters/10571706#19111613">stackoverflow<a/>
 * <p/>
 * This is an extension to {@link android.graphics.drawable.StateListDrawable} that workaround a bug not allowing
 * to set a {@link android.graphics.ColorFilter} to the drawable in one of the states., it add a method
 * {@link #addState(int[], Drawable, int)} for that purpose.
 */
final class FilterableStateListDrawable extends StateListDrawable {

    private int currIdx = -1;
    private int childrenCount = 0;
    private final SparseArray<ColorFilter> filterMap;

    FilterableStateListDrawable() {
        super(); // super() 里面会触发 selectDrawable ， filterMap 的判 null 是必要的
        filterMap = new SparseArray<>();
    }

    @Override
    public void addState(int[] stateSet, Drawable drawable) {
        super.addState(stateSet, drawable);
        childrenCount++;
    }

    /**
     * Same as {@link #addState(int[], android.graphics.drawable.Drawable)}, but allow to set a colorFilter associated
     * to this Drawable.
     *
     * @param stateSet - An array of resource Ids to associate with the image.
     * Switch to this image by calling setState().
     * @param drawable -The image to show.
     * @param color - The color use to construct the {@link android.graphics.ColorFilter} to apply to this state
     */
    void addState(int[] stateSet, Drawable drawable, int color) {
        int currChild = childrenCount;
        filterMap.put(currChild, new LightingColorFilter(0x0, color));
        addState(stateSet, drawable);
    }

    @Override
    public boolean selectDrawable(int idx) {
        if (currIdx != idx) {
            setColorFilter(getColorFilterForIdx(idx));
        }
        boolean result = super.selectDrawable(idx);
        // check if the drawable has been actually changed to the one I expect
        //noinspection ConstantConditions
        if (getCurrent() != null) {
            currIdx = result ? idx : currIdx;
            if (!result) {
                // it has not been changed, meaning, back to previous filter
                setColorFilter(getColorFilterForIdx(currIdx));
            }
        } else if (getCurrent() == null) {
            currIdx = -1;
            setColorFilter(null);
        }
        return result;
    }

    private ColorFilter getColorFilterForIdx(int idx) {
        // super() 里面会触发 selectDrawable ， filterMap 的判 null 是必要的
        //noinspection ConstantConditions
        return filterMap != null ? filterMap.get(idx) : null;
    }
}