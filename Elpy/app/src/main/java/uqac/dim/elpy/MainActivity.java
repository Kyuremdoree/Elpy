package uqac.dim.elpy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.Main_page_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigatioin_drawer);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Fragment newFragment = null;

        if (itemId == R.id.fConvertisseur) {
            // Démarrer le convertisseur
        }
        else if (itemId == R.id.fPriseNote) {
            newFragment = new NoteSystem();
        }
        else if (itemId == R.id.fPingMap) {
            // Démarrer la carte de ping
        }
        else if (itemId == R.id.fChrono) {
            // Démarrer le chronomètre
        }
        else if (itemId == R.id.fAudioConvertisseur) {
            // Démarrer le convertisseur audio
        }

        if (newFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, newFragment)
                    .addToBackStack(null)
                    .commit();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}