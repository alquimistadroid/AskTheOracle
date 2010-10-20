package com.alquimista.android.asktheoracle.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alquimista.android.asktheoracle.R;

public class ResultItemView extends RelativeLayout {

	public final static String TAG = "AskTheOracle.ResultItemView";

	private final boolean DEFAULT_SETTING_COLLAPSED = false;
	private final boolean DEFAULT_SETTING_SHOWLOADING = false;

	private LinearLayout mHeader;
	private ImageView mCollapsed;
	private ImageView mTitle;
	private ProgressBar mLoading;
	private WebView mWebView;

	private LinearLayout mBody;

	private String mStrContent = "";

	private boolean mIsCollapsed = DEFAULT_SETTING_COLLAPSED;
	private boolean mIsShowLoading = DEFAULT_SETTING_SHOWLOADING;

    /**
     * Mime-type to use when showing parsed results in a {@link WebView}.
     */
    public static final String MIME_TYPE = "text/html";

    /**
     * Encoding to use when showing parsed results in a {@link WebView}.
     */
    public static final String ENCODING = "utf-8";

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

		mHeader = (LinearLayout) findViewById(R.id.header);
		mCollapsed = (ImageView) findViewById(R.id.img_collapsed);
		mTitle = (ImageView) findViewById(R.id.img_title);
		mLoading = (ProgressBar) findViewById(R.id.progress_loading);
		mBody = (LinearLayout) findViewById(R.id.body);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setBackgroundColor(0);

		mHeader.setOnClickListener(collapsedListener);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ResultView, defStyle, 0);

		Drawable d = a.getDrawable(R.styleable.ResultView_title_bg);

		mTitle.setImageDrawable(d);

		mIsCollapsed = a.getBoolean(R.styleable.ResultView_collapsed, DEFAULT_SETTING_COLLAPSED);

		a.recycle();

		initResultItemView();
	}

	/**
	 *
	 */
	private void initResultItemView()
	{
		mCollapsed.setImageResource( mIsCollapsed ? R.drawable.collapsed : R.drawable.expanded );
		mBody.setVisibility(mIsCollapsed ? View.GONE : View.VISIBLE );

		mStrContent = "";
	}

	/**
	 *
	 * @param collapsed
	 */
	private void setCollapsed(boolean collapsed)
	{
		if ( ( mIsCollapsed != collapsed ) && ( !TextUtils.isEmpty( mStrContent ) ) )
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


	public void setContent( String content )
	{
		setContent( content , "" );
	}

	public void setContent( String content , String style )
	{
		String webviewContent = style + content;
		if ( mStrContent != webviewContent )
		{
			mWebView.loadData(webviewContent, MIME_TYPE, ENCODING);
			mStrContent = webviewContent;
		}
	}


}
