package com.etb.filemanager.ui.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.AttrRes;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.internal.ViewUtils;

public final class UiUtils {
    private UiUtils() {
    }

    @Px
    public static int dpToPx(@NonNull Context context, @Dimension(unit = Dimension.DP) int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    @Px
    public static int dpToPx(@NonNull Context context, @Dimension(unit = Dimension.DP) float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Px
    public static int spToPx(@NonNull Context context, @Dimension(unit = Dimension.SP) float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    @Dimension(unit = Dimension.DP)
    public static int pxToDp(@NonNull Context context, @Px int pixel) {
        return (int) ((float) pixel / context.getResources().getDisplayMetrics().density);
    }

    @StyleRes
    public static int getStyle(@NonNull Context context, @AttrRes int resId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }

    @Nullable
    public static Drawable getDrawable(@NonNull Context context, @AttrRes int resId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, typedValue, true);
        return ContextCompat.getDrawable(context, typedValue.resourceId);
    }

    public static int getColumnCount(@NonNull View v, @Dimension(unit = Dimension.DP) int columnWidth, int defaultCount) {
        int width = v.getWidth();
        if (width == 0) {
            return defaultCount;
        }
        int widthDp = pxToDp(v.getContext(), width);
        return (int) (widthDp / columnWidth + 0.5);
    }

    public static void hideKeyboard(@NonNull View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @SuppressWarnings("deprecation")
    public static void applyWindowInsetsAsPaddingNoTop(View v) {
        doOnApplyWindowInsets(v, (view, insets, initialPadding, initialMargin) -> {
            if (!ViewCompat.getFitsSystemWindows(view)) {
                // Do not add padding if fitsSystemWindows is false
                return insets;
            }
            int top = initialPadding.top;
            int bottom = initialPadding.bottom + insets.getSystemWindowInsetBottom();

            boolean isRtl = ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
            int systemWindowInsetLeft = insets.getSystemWindowInsetLeft();
            int systemWindowInsetRight = insets.getSystemWindowInsetRight();
            int start;
            int end;
            if (isRtl) {
                start = initialPadding.right + systemWindowInsetRight;
                end = initialPadding.left + systemWindowInsetLeft;
            } else {
                start = initialPadding.left + systemWindowInsetLeft;
                end = initialPadding.right + systemWindowInsetRight;
            }
            ViewCompat.setPaddingRelative(view, start, top, end, bottom);
            return insets;
        });
    }

    public static void applyWindowInsetsAsMargin(View v) {
        applyWindowInsetsAsMargin(v, true, true);
    }

    @SuppressWarnings("deprecation")
    public static void applyWindowInsetsAsMargin(View v, boolean bottomMargin, boolean topMargin) {
        doOnApplyWindowInsets(v, (view, insets, initialPadding, initialMargin) -> {
            if (initialMargin == null || !ViewCompat.getFitsSystemWindows(view)) {
                // Do not add padding if fitsSystemWindows is false
                return insets;
            }
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (!(layoutParams instanceof ViewGroup.MarginLayoutParams marginLayoutParams)) {
                return insets;
            }

            if (topMargin) {
                marginLayoutParams.topMargin = initialMargin.top + insets.getSystemWindowInsetTop();
            }
            if (bottomMargin) {
                marginLayoutParams.bottomMargin = initialMargin.bottom + insets.getSystemWindowInsetBottom();
            }
            marginLayoutParams.leftMargin = initialMargin.left + insets.getSystemWindowInsetLeft();
            marginLayoutParams.rightMargin = initialMargin.right + insets.getSystemWindowInsetRight();

            view.setLayoutParams(marginLayoutParams);
            return insets;
        });
    }

    /**
     * Wrapper around {@link androidx.core.view.OnApplyWindowInsetsListener} that records the initial
     * margin of the view and requests that insets are applied when attached.
     */
    @SuppressLint("RestrictedApi")
    public static void doOnApplyWindowInsets(@NonNull View view, @NonNull OnApplyWindowInsetsListener listener) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        Rect initialMargins;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams marginLayoutParams) {
            // Create a snapshot of the view's margin state.
            initialMargins = new Rect(marginLayoutParams.leftMargin, marginLayoutParams.topMargin,
                    marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
        } else initialMargins = null;
        Rect initialPadding = new Rect(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(),
                view.getPaddingBottom());
        // Set an actual OnApplyWindowInsetsListener which proxies to the given callback, also passing
        // in the original margin state.
        ViewCompat.setOnApplyWindowInsetsListener(view, (view1, insets) ->
                listener.onApplyWindowInsets(view1, insets, initialPadding, initialMargins));
        // Request some insets
        ViewUtils.requestApplyInsetsWhenAttached(view);
    }

    public static boolean isDarkMode(@NonNull Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    @SuppressWarnings("deprecation")
    public static boolean isDarkMode() {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                return true;
            default:
            case AppCompatDelegate.MODE_NIGHT_NO:
                return false;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
            case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED:
            case AppCompatDelegate.MODE_NIGHT_AUTO_TIME:
                return isDarkModeOnSystem();
        }
    }

    public static boolean isDarkModeOnSystem() {
        return (Resources.getSystem().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    @SuppressWarnings("deprecation")
    public static void setSystemBarStyle(@NonNull Window window, boolean needLightStatusBar) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (!isDarkMode()) {
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility()
                    | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && needLightStatusBar) {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility()
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int windowInsetBottom = window.getDecorView().getRootWindowInsets().getSystemWindowInsetBottom();
                if (windowInsetBottom >= Resources.getSystem().getDisplayMetrics().density * 40) {
                    window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility()
                            | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
            }
        }
        setSystemBarTransparent(window);
    }

    @SuppressWarnings("deprecation")
    private static void setSystemBarTransparent(@NonNull Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    /**
     * Wrapper around {@link androidx.core.view.OnApplyWindowInsetsListener} which also passes the
     * initial padding/margin set on the view. Used with {@link #doOnApplyWindowInsets(View,
     * OnApplyWindowInsetsListener)}.
     */
    public interface OnApplyWindowInsetsListener {

        /**
         * When {@link View#setOnApplyWindowInsetsListener(View.OnApplyWindowInsetsListener) set} on a
         * View, this listener method will be called instead of the view's own {@link
         * View#onApplyWindowInsets(WindowInsets)} method. The {@code initial*} is the view's
         * original padding/margin which can be updated and will be applied to the view automatically. This
         * method should return a new {@link WindowInsetsCompat} with any insets consumed.
         */
        WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets, @NonNull Rect initialPadding,
                                               @Nullable Rect initialMargin);
    }
}