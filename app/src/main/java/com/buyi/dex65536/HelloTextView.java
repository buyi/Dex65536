package com.buyi.dex65536;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class HelloTextView extends TextView {

	public HelloTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setText(R.string.hello);
	}

}
