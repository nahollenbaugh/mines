package com.nahollenbaugh.mines.main;

import android.os.Bundle;

import com.nahollenbaugh.mines.R;
import com.nahollenbaugh.mines.dialogs.StringInputDialogFragment;
import com.nahollenbaugh.mines.dialogs.StringInputDialogListener;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.nahollenbaugh.mines.databinding.ActivityMainBinding;
import com.nahollenbaugh.mines.storage.StoredDataStrings;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StringInputDialogListener {

    private ActivityMainBinding binding;

    List<Fragment> fragments;
    public MainActivity(){
        getSupportFragmentManager().addFragmentOnAttachListener((fm, f) -> fragments.add(f));
        fragments = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> { });

        StoredDataStrings.accessStuff(getResources());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, (AppBarConfiguration)null)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onPositiveDialogClick(StringInputDialogFragment dialog){
        // Okay I don't know why the fragments defined in the navgraph don't have the ids set
        // in their xml there and that show up in R.id, but since I'm on a plane right now I
        // don't really wanna try to figure it out right now.  This works so whatever.
        List<Fragment> fragments = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getFragments();
        for (Fragment f : fragments){
            if (f instanceof StringInputDialogListener){
                ((StringInputDialogListener)f).onPositiveDialogClick(dialog);
            }
        }
    }

    @Override
    public void onNegativeDialogClick(StringInputDialogFragment dialog){
        // Okay I don't know why the fragments defined in the navgraph don't have the ids set
        // in their xml there and that show up in R.id, but since I'm on a plane right now I
        // don't really wanna try to figure it out right now.  This works so whatever.
        List<Fragment> fragments = getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getFragments();
        for (Fragment f : fragments){
            if (f instanceof StringInputDialogListener){
                ((StringInputDialogListener)f).onPositiveDialogClick(dialog);
            }
        }
    }
}