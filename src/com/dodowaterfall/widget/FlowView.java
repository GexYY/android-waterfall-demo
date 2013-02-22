package com.dodowaterfall.widget;

import java.io.BufferedInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class FlowView extends ImageView implements View.OnClickListener,
		View.OnLongClickListener {

	private AnimationDrawable loadingAnimation;
	private FlowTag flowTag;
	private Context context;
	public Bitmap bitmap;
	private ImageLoaderTask task;
	private int columnIndex;// 鍥剧墖灞炰簬绗嚑鍒�
	private int rowIndex;// 鍥剧墖灞炰簬绗嚑琛�
	private Handler viewHandler;

	public FlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		Init();
	}

	public FlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		Init();
	}

	public FlowView(Context c) {
		super(c);
		this.context = c;
		Init();
	}

	private void Init() {

		setOnClickListener(this);
		this.setOnLongClickListener(this);
		setAdjustViewBounds(true);

	}

	@Override
	public boolean onLongClick(View v) {
		Log.d("FlowView", "LongClick");
		Toast.makeText(context, " " + this.flowTag.getFlowId(),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.d("FlowView", "Click");
		Toast.makeText(context, " " + this.flowTag.getFlowId(),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 鍔犺浇鍥剧墖
	 */
	public void LoadImage() {
		if (getFlowTag() != null) {

			new LoadImageThread().start();
		}
	}

	/**
	 * 閲嶆柊鍔犺浇鍥剧墖
	 */
	public void Reload() {
		if (this.bitmap == null && getFlowTag() != null) {

			new ReloadImageThread().start();
		}
	}

	/**
	 * 鍥炴敹鍐呭瓨
	 */
	public void recycle() {
		setImageBitmap(null);
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
	}

	public FlowTag getFlowTag() {
		return flowTag;
	}

	public void setFlowTag(FlowTag flowTag) {
		this.flowTag = flowTag;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}

	class ReloadImageThread extends Thread {

		@Override
		public void run() {
			if (flowTag != null) {

				BufferedInputStream buf;
				try {
					buf = new BufferedInputStream(flowTag.getAssetManager()
							.open(flowTag.getFileName()));
					bitmap = BitmapFactory.decodeStream(buf);

				} catch (IOException e) {

					e.printStackTrace();
				}

				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null) {// 姝ゅ鍦ㄧ嚎绋嬭繃澶氭椂鍙兘涓簄ull
							setImageBitmap(bitmap);
						}
					}
				});
			}

		}
	}

	class LoadImageThread extends Thread {
		LoadImageThread() {
		}

		public void run() {

			if (flowTag != null) {

				BufferedInputStream buf;
				try {
					buf = new BufferedInputStream(flowTag.getAssetManager()
							.open(flowTag.getFileName()));
					bitmap = BitmapFactory.decodeStream(buf);

				} catch (IOException e) {

					e.printStackTrace();
				}
				// if (bitmap != null) {

				// 姝ゅ涓嶈兘鐩存帴鏇存柊UI锛屽惁鍒欎細鍙戠敓寮傚父锛�
				// CalledFromWrongThreadException: Only the original thread that
				// created a view hierarchy can touch its views.
				// 涔熷彲浠ヤ娇鐢℉andler鎴栬�Looper鍙戦�Message瑙ｅ喅杩欎釜闂

				((Activity) context).runOnUiThread(new Runnable() {
					public void run() {
						if (bitmap != null) {// 姝ゅ鍦ㄧ嚎绋嬭繃澶氭椂鍙兘涓簄ull
							int width = bitmap.getWidth();// 鑾峰彇鐪熷疄瀹介珮
							int height = bitmap.getHeight();

							LayoutParams lp = getLayoutParams();

							int layoutHeight = (height * flowTag.getItemWidth())
									/ width;// 璋冩暣楂樺害
							if (lp == null) {
								lp = new LayoutParams(flowTag.getItemWidth(),
										layoutHeight);
							}
							setLayoutParams(lp);

							setImageBitmap(bitmap);
							Handler h = getViewHandler();
							Message m = h.obtainMessage(flowTag.what, width,
									layoutHeight, FlowView.this);
							h.sendMessage(m);
						}
					}
				});

				// }

			}

		}
	}
}
