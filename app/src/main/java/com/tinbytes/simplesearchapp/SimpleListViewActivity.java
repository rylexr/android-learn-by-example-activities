/*
 * Copyright 2015 Tinbytes Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tinbytes.simplesearchapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SimpleListViewActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {
  private static final int ANIMAL_LOADER = 1;

  private ListView lvAnimals;
  private SimpleCursorAdapter scaAnimals;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_list_view);
    lvAnimals = (ListView) findViewById(R.id.list);
    scaAnimals = new SimpleCursorAdapter(
        this,
        android.R.layout.simple_list_item_2,
        null,
        new String[]{DatabaseContract.AnimalColumns.NAME, DatabaseContract.AnimalColumns.CATEGORY},
        new int[]{android.R.id.text1, android.R.id.text2},
        0);
    lvAnimals.setAdapter(scaAnimals);
    lvAnimals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // ListView Clicked item index
        Cursor c = (Cursor) lvAnimals.getItemAtPosition(position);
        // Show Alert
        Toast.makeText(getApplicationContext(),
            "Position " + position + " - Animal " + c.getString(c.getColumnIndex(DatabaseContract.AnimalColumns.NAME)), Toast.LENGTH_LONG).show();
      }
    });

    getSupportLoaderManager().initLoader(ANIMAL_LOADER, null, this);

    handleIntent(getIntent());
  }

  @Override
  protected void onNewIntent(Intent newIntent) {
    handleIntent(newIntent);
  }

  private void handleIntent(Intent intent) {
    String query;
    String intentAction = intent.getAction();
    if (Intent.ACTION_SEARCH.equals(intentAction)) {
      query = intent.getStringExtra(SearchManager.QUERY);
      Bundle b = new Bundle();
      b.putString("query", query);
      getSupportLoaderManager().restartLoader(ANIMAL_LOADER, b, this);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_simple_list_view, menu);
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search_animal));
    if (searchView != null) {
      searchView.setOnQueryTextListener(this);
      searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
      searchView.setIconifiedByDefault(true);
    }
    return true;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    switch (id) {
      case ANIMAL_LOADER:
        StringBuilder sb = new StringBuilder();
        if (args != null) {
          String query = args.getString("query");
          if (query != null && !query.trim().isEmpty()) {
            sb.append(DatabaseContract.AnimalColumns.NAME).append(" LIKE '%").append(query).append("%'");
          }
        }
        return new CursorLoader(this, DatabaseContract.AnimalTable.CONTENT_URI, null, sb.toString(), null, null);
      default:
        throw new IllegalArgumentException("Unknown loader ID = " + id);
    }
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    scaAnimals.changeCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    scaAnimals.changeCursor(null);
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    Bundle b = new Bundle();
    b.putString("query", newText);
    getSupportLoaderManager().restartLoader(ANIMAL_LOADER, b, this);
    return true;
  }
}


