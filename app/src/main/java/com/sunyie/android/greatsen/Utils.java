package com.sunyie.android.greatsen;

import android.content.Context;

/**
 * Created by yukunlin on 2017/2/7.
 */

public class Utils {

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
