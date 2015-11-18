package com.bestbeforeapp.baasboxtesting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.baasbox.android.SaveMode;
import com.bestbeforeapp.baasboxtesting.R;
import com.bestbeforeapp.baasboxtesting.RandomSentences;
import com.bestbeforeapp.baasboxtesting.fragments.NoteDetailFragment;
import com.bestbeforeapp.baasboxtesting.fragments.NoteListFragment;

import timber.log.Timber;


/**
 * An activity representing a list of Notes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NoteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NoteListFragment} and the item details
 * (if present) is a {@link NoteDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link NoteListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class NoteListActivity extends AppCompatActivity
        implements NoteListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BaasUser.current() == null){
            startLoginScreen();
            return;
        }

        setContentView(R.layout.activity_note_app_bar);

        final NoteListFragment noteListFragment = (NoteListFragment) getSupportFragmentManager().findFragmentById(R.id.note_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                BaasDocument doc = new BaasDocument("notes");
                doc.put("title", RandomSentences.main(null));
                doc.put("content", RandomSentences.main(null));

                RequestToken mSaving = doc.save(SaveMode.IGNORE_VERSION, new BaasHandler<BaasDocument>() {
                    @Override
                    public void handle(BaasResult<BaasDocument> result) {
                        if (result.isSuccess()) {
                            //We successfully save the document
                            Timber.d("Saved document");


                        } else {
                            Timber.e("Could not save document!");
                        }

                        noteListFragment.refreshDocuments();
                    }
                });

            }
        });

        if (findViewById(R.id.note_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            noteListFragment.setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link NoteListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(BaasDocument document) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(NoteDetailFragment.DOCUMENT_ID, document);
            NoteDetailFragment fragment = new NoteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.note_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, NoteDetailActivity.class);
            detailIntent.putExtra(NoteDetailFragment.DOCUMENT_ID, document);
            startActivity(detailIntent);
        }
    }

    public void startLoginScreen(){
//        mDoRefresh = false;
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_logout:
                BaasUser.current().logout(new BaasHandler<Void>() {
                    @Override
                    public void handle(BaasResult<Void> result) {
                        if(result.isSuccess()) {
                            Timber.d("Logged out: " + (BaasUser.current() == null));

                            Snackbar.make(findViewById(R.id.toolbar), "Logged out!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            finish();
                        } else{
                            Timber.e("Error: " + result.error());
                        }
                    };
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}