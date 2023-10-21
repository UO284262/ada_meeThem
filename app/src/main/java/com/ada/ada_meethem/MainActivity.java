package com.ada.ada_meethem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.ada.ada_meethem.ui.HomeFragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    CollapsingToolbarLayout toolBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navView.setSelectedItemId(R.id.nav_home);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /* Cuando se selecciona uno de los botones / ítems*/
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int itemId = item.getItemId();

            /* Según el caso, crearemos un Fragmento u otro */
            if (itemId == R.id.nav_home)
            {
                /* Haciendo uso del FactoryMethod pasándole todos los parámetros necesarios */

                /* Argumento solamente necesita.... El argumento de la película */

                HomeFragment homeFragment=HomeFragment.newInstance("Home");

                /* ¿Qué estaremos haciendo aquí? */
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                return true;
            }

            if (itemId == R.id.nav_profile)
            {
                return true;
            }

            if (itemId == R.id.nav_plan_details)
            {
                /* Haciendo uso del FactoryMethod pasándole todos los parámetros necesarios */

                /* Argumento solamente necesita.... El argumento de la película */

                HomeFragment homeFragment=HomeFragment.newInstance("Detalles");

                /* ¿Qué estaremos haciendo aquí? */
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
                return true;
            }
            //Si no es nula y no entra... Algo falla.
            throw new IllegalStateException("Unexpected value: " + item.getItemId());
        };
    };
}