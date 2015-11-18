package com.bestbeforeapp.baasboxtesting.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;
import com.bestbeforeapp.baasboxtesting.DividerItemDecoration;
import com.bestbeforeapp.baasboxtesting.R;
import com.bestbeforeapp.baasboxtesting.adapters.ExampleAdapter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A list fragment representing a list of Notes. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link NoteDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class NoteListFragment extends Fragment implements ExampleAdapter.OnItemClickListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    //TODO: Why do we use this?
    private final static String SAVED_DOCS = "saved_docs";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private ExampleAdapter mAdapter;

    private ArrayList<BaasDocument> mDocuments;

    private RecyclerView mRecyclerView;

    private ActionMode mActionMode;

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public boolean onListItemClick(int position) {
        if (mActionMode != null && position != ListView.INVALID_POSITION) {
            toggleSelection(position);
            return true;
        } else {
            //Notify the active callbacks interface (the activity, if the
            //fragment is attached to one) that an item has been selected.
            if (mAdapter.getItemCount() > 0) {
                if (position != mActivatedPosition)
                    setActivatedPosition(position);

                BaasDocument item = mAdapter.getItem(position);
                Timber.d("Selected " + item.getString("title"));
                mAdapter.setActivated(position);

                mCallbacks.onItemSelected(mAdapter.getItem(position));
            }
            return false;
        }
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position, false);

        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            Timber.d("toggleSelection finish the actionMode");
            mActionMode.finish();
        } else {
            Timber.d("toggleSelection update title after selection count=" + count);
            mActionMode.invalidate();
        }
    }

    @Override
    public void onListItemLongClick(int position) {

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(BaasDocument document);
        public void startLoginScreen();
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(BaasDocument document) {
        }

        @Override
        public void startLoginScreen() {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState==null){
            mDocuments = new ArrayList<BaasDocument>();
        } else {
            mDocuments = savedInstanceState.getParcelableArrayList(SAVED_DOCS);
        }


        refreshDocuments();

//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(
//                getActivity(),
//                android.R.layout.simple_list_item_activated_1,
//                android.R.id.text1,
//                DummyContent.ITEMS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_content, container, false);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        // specify an adapter (see also next example)
        mAdapter = new ExampleAdapter(getActivity(), this, mDocuments);
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mAdapter.setMode(activateOnItemClick ? ExampleAdapter.MODE_SINGLE : ExampleAdapter.MODE_MULTI);

//        getListView().setChoiceMode(activateOnItemClick
//                ? ListView.CHOICE_MODE_SINGLE
//                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        Timber.d("ItemList New mActivatedPosition=" + position);
        mActivatedPosition = position;
    }


    public void refreshDocuments() {
        //TODO: Loading dialog

        RequestToken mRefresh = BaasDocument.fetchAll("notes", onRefresh);
    }

    private final BaasHandler<List<BaasDocument>> onRefresh = new BaasHandler<List<BaasDocument>>() {
        @Override
        public void handle(BaasResult<List<BaasDocument>> result) {
            try {
                refreshDocuments(result.get());
            }catch (BaasInvalidSessionException e){
                mCallbacks.startLoginScreen();
            }catch (BaasException e){
                Timber.e("Error: " + e.getMessage(), e);
            }
        }
    };

    public void refreshDocuments(List<BaasDocument> docs) {
        mDocuments.clear();
        mDocuments.addAll(docs);
        mAdapter.notifyDataSetChanged();
    }
}
