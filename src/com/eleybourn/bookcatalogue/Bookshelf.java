package com.eleybourn.bookcatalogue;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/*
 * A book catalogue application that integrates with Google Books.
 */
public class Bookshelf extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private CatalogueDBAdapter mDbHelper;
    private static final int INSERT_ID = Menu.FIRST + 0;
    private static final int DELETE_ID = Menu.FIRST + 1;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_bookshelves);
		mDbHelper = new CatalogueDBAdapter(this);
		mDbHelper.open();
		fillBookshelves();
		registerForContextMenu(getListView());
    }
    
    private void fillBookshelves() {
    	// base the layout and the query on the sort order
       	int layout = R.layout.row_bookshelf;
    	
    	// Get all of the rows from the database and create the item list
    	Cursor BookshelfCursor = mDbHelper.fetchAllBookshelves();
        startManagingCursor(BookshelfCursor);
     
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{CatalogueDBAdapter.KEY_BOOKSHELF, CatalogueDBAdapter.KEY_ROWID};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.row_bookshelf};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter books = new SimpleCursorAdapter(this, layout, BookshelfCursor, from, to);
        setListAdapter(books);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert_bs);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createBookshelf();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
	
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete_bs);
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
    	case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    		if (info.id == 1) {
    			Toast.makeText(this, R.string.delete_1st_bs, Toast.LENGTH_LONG).show();
    		} else {
    			mDbHelper.deleteBookshelf(info.id);
    			fillBookshelves();
    		}
   			return true;
		}
		return super.onContextItemSelected(item);
	}
	
    private void createBookshelf() {
        Intent i = new Intent(this, BookshelfEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, BookshelfEdit.class);
        i.putExtra(CatalogueDBAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillBookshelves();
    }

}
