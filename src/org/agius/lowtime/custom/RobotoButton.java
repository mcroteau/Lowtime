package org.agius.lowtime.custom;

import static org.agius.lowtime.LowtimeConstants.BLACK_FONT;
import static org.agius.lowtime.LowtimeConstants.BOLD_FONT;
import static org.agius.lowtime.LowtimeConstants.REGULAR_FONT;
import static org.agius.lowtime.LowtimeConstants.THIN_FONT;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import static org.agius.lowtime.LowtimeConstants.*;

public class RobotoButton extends Button{

	public RobotoButton(Context context) {
		super(context);
		setTextColor(Color.parseColor("#ff0000"));
		setTextSize(BUTTON_TEXT_SIZE);
	}

	public RobotoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	public RobotoButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
	
	@Override
	public void setTypeface(Typeface typeface, int style) {
		if(!isInEditMode()){
			typeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), BOLD_FONT);
		}
	    super.setTypeface(typeface, 0);
	}
}
