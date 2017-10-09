package com.walowtech.fblaapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import static android.graphics.Shader.TileMode.REPEAT;

/**
 * Class for TextView with gradient background
 *
 * Upon the layout being changed, a gradient is applied
 * as the background to the text
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */

//Created 10/8/2017
public class GradientTextView extends AppCompatTextView {
    public GradientTextView(Context context) {
        super(context, null, -1);
    }

    GradientTextView(Context context, AttributeSet attrs){
        super(context, attrs, -1);
    }

    GradientTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        Shader.TileMode tile_mode = Shader.TileMode.CLAMP;
        LinearGradient grad = new LinearGradient(0, 0, 0, getHeight(), Color.BLACK, Color.TRANSPARENT, tile_mode);
        Shader shader_gradient = grad;
        getPaint().setShader(shader_gradient);

    }
}
