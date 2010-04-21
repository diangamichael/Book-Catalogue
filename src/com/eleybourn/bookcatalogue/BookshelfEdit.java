package com.eleybourn.bookcatalogue;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BookshelfEdit extends Activity {

	private EditText mBookshelfText;
	private Button mConfirmButton;
	private Button mCancelButton;
    private Long mRowId;
    private CatalogueDBAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	try {
	        super.onCreate(savedInstanceState);
	        mDbHelper = new CatalogueDBAdapter(this);
	        mDbHelper.open();
	        
	        setContentView(R.layout.edit_bookshelf);
	       
	        mBookshelfText = (EditText) findViewById(R.id.bookshelf);
            mConfirmButton = (Button) findViewById(R.id.confirm);
	        mCancelButton = (Button) findViewById(R.id.cancel);
	       
	        mRowId = savedInstanceState != null ? savedInstanceState.getLong(CatalogueDBAdapter.KEY_ROWID) : null;
	        if (mRowId == null) {
	        	Bundle extras = getIntent().getExtras();
	        	mRowId = extras != null ? extras.getLong(CatalogueDBAdapter.KEY_ROWID) : null;
	        }
	        populateFields();
	        
	        mConfirmButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                saveState();
	                setResult(RESULT_OK);
	                finish();
	            }
	        });
	        
	        mCancelButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                setResult(RESULT_OK);
	                finish();
	            }
	        });
	        
    	} catch (Exception e) {
    		//do nothing
    	}
    }
    
    private void populateFields() {
        if (mRowId != null && mRowId > 0) {
            Cursor bookshelf = mDbHelper.fetchBookshelf(mRowId);
            startManagingCursor(bookshelf);
            
            mBookshelfText.setText(bookshelf.getString(bookshelf.getColumnIndexOrThrow(CatalogueDBAdapter.KEY_BOOKSHELF)));
            mConfirmButton.setText(R.string.confirm_save_bs);
        } else {
            mConfirmButton.setText(R.string.confirm_add_bs);
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CatalogueDBAdapter.KEY_ROWID, mRowId);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
        String bookshelf = mBookshelfText.getText().toString();

        if (mRowId == null || mRowId == 0) {
            long id = mDbHelper.createBookshelf(bookshelf);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateBookshelf(mRowId, bookshelf);
        }
    }

}
