package com.alquimista.android.asktheoracle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ResultItemView extends RelativeLayout {

	private final boolean DEFAULT_SETTING_COLLAPSED = false;
	private final boolean DEFAULT_SETTING_SHOWLOADING = false;

	private ImageView mCollapsed;
	private TextView mTitle;
	private ProgressBar mLoading;
	private View mContent;
	private LinearLayout mBody;

	private String mTitleStr;
	private boolean mIsCollapsed = DEFAULT_SETTING_COLLAPSED;
	private boolean mIsShowLoading = DEFAULT_SETTING_SHOWLOADING;

	private View.OnClickListener collapsedListener = new View.OnClickListener() {
		public void onClick(View v) {
			toggleCollapsed();

		}
	};


	public ResultItemView (Context context)
	{
		super(context);
		initResultItemView();
	}

	public ResultItemView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ResultItemView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.result_item, this, true);

		mCollapsed = (ImageView) findViewById(R.id.img_collapsed);
		mTitle = (TextView) findViewById(R.id.text_title);
		mLoading = (ProgressBar) findViewById(R.id.progress_loading);
		mBody = (LinearLayout) findViewById(R.id.body);

		mCollapsed.setOnClickListener(collapsedListener);
		mTitle.setOnClickListener(collapsedListener);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ResultView, defStyle, 0);
		mTitleStr = a.getString(R.styleable.ResultView_title);
		mIsCollapsed = a.getBoolean(R.styleable.ResultView_collapsed, DEFAULT_SETTING_COLLAPSED);

		a.recycle();

		initResultItemView();
	}

	/**
	 *
	 */
	private void initResultItemView()
	{
		if( mTitleStr != null )
		{
			mTitle.setText(mTitleStr);
		}

		mCollapsed.setImageResource( mIsCollapsed ? R.drawable.collapsed : R.drawable.expanded );

		mContent = null;
	}

	/**
	 *
	 * @param collapsed
	 */
	private void setCollapsed(boolean collapsed)
	{
		if ( ( mIsCollapsed != collapsed ) && mContent != null )
		{
			mIsCollapsed = collapsed;

			mCollapsed.setImageResource( mIsCollapsed ? R.drawable.collapsed : R.drawable.expanded );
			mBody.setVisibility(mIsCollapsed ? View.GONE : View.VISIBLE );

		}
	}

	/**
	 *
	 */
	public void toggleCollapsed()
	{
		setCollapsed(!mIsCollapsed);
	}


	/**
	 *
	 * @param showLoading
	 */
	public void showLoading(boolean showLoading)
	{
		if ( mIsShowLoading != showLoading )
		{
			mIsShowLoading = showLoading;
			mLoading.setVisibility(mIsShowLoading? VISIBLE : INVISIBLE);
		}
	}

	/**
	 *
	 * @param content
	 */
	public void setContent( View content )
	{
		if ( mContent != content )
		{
			if ( mContent != null )
			{
				mBody.removeView(mContent);
			}

			if( content != null )
			{
				mBody.addView(content);
				mBody.setVisibility(View.VISIBLE );
			}

			mContent = content;
		}
	}

}
