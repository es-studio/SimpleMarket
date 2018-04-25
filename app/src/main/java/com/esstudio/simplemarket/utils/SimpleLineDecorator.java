package com.esstudio.simplemarket.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class SimpleLineDecorator extends RecyclerView.ItemDecoration
{
	private final Paint m_paint;
	private final int m_height;

	public SimpleLineDecorator(int color, int height)
	{
		m_height = height;
		m_paint = new Paint();
		m_paint.setColor(color);
	}

	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
	{
		int right = parent.getWidth() - parent.getPaddingRight();

		int childCount = parent.getChildCount();

		for (int i = 0; i < childCount - 1; i++)
		{
			View child = parent.getChildAt(i);
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();

			float top = child.getBottom() + params.bottomMargin;
			float bottom = top + m_height;

			c.drawRect(0, top, right, bottom, m_paint);
		}
	}
}
