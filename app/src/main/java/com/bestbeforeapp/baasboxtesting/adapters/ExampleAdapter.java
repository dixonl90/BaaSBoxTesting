package com.bestbeforeapp.baasboxtesting.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baasbox.android.BaasDocument;
import com.bestbeforeapp.baasboxtesting.R;

import java.util.ArrayList;
import java.util.List;


public class ExampleAdapter extends FlexibleAdapter<ExampleAdapter.SimpleViewHolder, BaasDocument> {

	private static final String TAG = ExampleAdapter.class.getSimpleName();
	private List<BaasDocument> mDocuments;

	public interface OnItemClickListener {
		/**
		 * Delegate the click event to the listener and check if selection MULTI enabled.<br/>
		 * If yes, call toggleActivation.
		 * @param position
		 * @return true if MULTI selection is enabled, false for SINGLE selection
		 */
		boolean onListItemClick(int position);

		/**
		 * This always calls toggleActivation after listener event is consumed.
		 * @param position
		 */
		void onListItemLongClick(int position);
	}

	private Context mContext;
	private static final int ROW_VIEW_TYPE = 1;

	private LayoutInflater mInflater;
	private OnItemClickListener mClickListener;

	private int positionActivated;

	public ExampleAdapter(Context context, OnItemClickListener listener, ArrayList<BaasDocument> documents) {
		this.mContext = context;
		this.mClickListener = listener;
		this.mDocuments = documents;
		updateDataSet(null);
	}

	@Override
	public void setMode(int mode) {
		super.setMode(mode);
	}

	@Override
	public int getItemViewType(int position) {
		return ROW_VIEW_TYPE;
	}

	@Override
	public void updateDataSet(String param) {

	}

	@Override
	public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Log.d(TAG, "onCreateViewHolder for viewType " + viewType);
		if (mInflater == null) {
			mInflater = LayoutInflater.from(parent.getContext());
		}

		switch (viewType) {
			default:
				return new ViewHolder(
						mInflater.inflate(R.layout.list_row, parent, false),
						this);
		}
	}

	@Override
	public void onBindViewHolder(SimpleViewHolder holder, final int position) {
		Log.d(TAG, "onBindViewHolder for position " + position);
		final BaasDocument document = getItem(position);
		
		//When user scrolls this bind the correct selection status
		if (position == positionActivated) {
			holder.itemView.setActivated(true);
			holder.itemView.requestFocus();
		} else {
			holder.itemView.setActivated(false);
		}

		holder.mTitle.setText(document.getString("title"));
		holder.mSubtitle.setText(document.getCreationDate());
	}
	
	/**
	 * Used for UserLearnsSelection.
	 * Must be the base class of extension for Adapter Class.
	 */
	static class SimpleViewHolder extends RecyclerView.ViewHolder {

		TextView mTitle;
		TextView mSubtitle;
		ExampleAdapter mAdapter;

		SimpleViewHolder(View view) {
			super(view);
		}

		SimpleViewHolder(View view, ExampleAdapter adapter) {
			super(view);
			mAdapter = adapter;
			mTitle = (TextView) view.findViewById(R.id.text1);
			mSubtitle = (TextView) view.findViewById(R.id.text2);
		}
	}

	/**
	 * Provide a reference to the views for each data item.
	 * Complex data labels may need more than one view per item, and
	 * you provide access to all the views for a data item in a view holder.
	 */
	static final class ViewHolder extends SimpleViewHolder implements View.OnClickListener,
			View.OnLongClickListener {

		ViewHolder(View view, final ExampleAdapter adapter) {
			super(view);

			this.mAdapter = adapter;
			this.mTitle = (TextView) view.findViewById(R.id.text1);
			this.mSubtitle = (TextView) view.findViewById(R.id.text2);

			this.itemView.setClickable(true);

			this.itemView.setOnClickListener(this);
			this.itemView.setOnLongClickListener(this);

		}

		private void toggleActivation() {
			itemView.setActivated(mAdapter.isSelected(getAdapterPosition()));
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onClick(View view) {

			if (mAdapter.mClickListener.onListItemClick(getAdapterPosition())) {
				Log.d(TAG, "Click!");
				toggleActivation();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean onLongClick(View view) {
			mAdapter.mClickListener.onListItemLongClick(getAdapterPosition());
			Log.d(TAG, "Long Click!");
			toggleActivation();
			return true;
		}
	}

	public void setActivated(int positionActivated) {
		int oldPositionActivated = this.positionActivated;
		this.positionActivated = positionActivated;

		notifyItemChanged(oldPositionActivated);
		notifyItemChanged(positionActivated);
	}

	@Override
	public int getItemCount() {
		if (mDocuments==null){
			return 0;
		}
		return mDocuments.size();
	}

	@Override
	public BaasDocument getItem(int position) {
		return mDocuments.get(position);
	}

}