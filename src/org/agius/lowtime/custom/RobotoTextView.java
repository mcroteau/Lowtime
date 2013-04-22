package org.agius.lowtime.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import static org.agius.lowtime.LowtimeConstants.*;

public class RobotoTextView extends TextView {

	public RobotoTextView(Context context) {
		super(context);
	}
	
	public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
	
	
	@Override
	public void setTypeface(Typeface typeface, int style) {
		if(!isInEditMode()){
			/**
				Typeface.NORMAL = ROBOTO THIN
				Typeface.BOLD = ROBOTO BOLD
				Typeface.BOLD_ITALIC = ROBOTO BLACK
				Typeface.ITALIC = ROBOTO LIGHT
			**/
			
			switch(style){
				case Typeface.NORMAL:
					typeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), THIN_FONT);
					break;
				case Typeface.ITALIC:
					typeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), BLACK_FONT);
					break;
				case Typeface.BOLD:
					typeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), BOLD_FONT);
					break;
				case Typeface.BOLD_ITALIC:
					typeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), BLACK_FONT);
					break;				
				default :
					typeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), REGULAR_FONT);
					break;
					
			}
		}
	    super.setTypeface(typeface, 0);
	}

	
	
	protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }
   
}
