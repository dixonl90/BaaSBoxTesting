package com.bestbeforeapp.baasboxtesting.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baasbox.android.BaasDocument;

import java.util.List;

public class NotesAdapter extends  RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<BaasDocument> mDocuments;
    private LayoutInflater mInflater;

    public NotesAdapter(Context context, List<BaasDocument> documents){
        this.mDocuments=documents;
        mInflater = LayoutInflater.from(context);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView text1;
        public TextView text2;

        public ViewHolder(View v) {
            super(v);
            text1 = (TextView) v.findViewById(android.R.id.text1);
            text2 = (TextView) v.findViewById(android.R.id.text2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BaasDocument document = mDocuments.get(position);

        String title = document.getString("title");
        String creationDate = document.getCreationDate();

        holder.text1.setText(title);
        holder.text2.setText(creationDate);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mDocuments==null){
            return 0;
        }
        return mDocuments.size();
    }

}
