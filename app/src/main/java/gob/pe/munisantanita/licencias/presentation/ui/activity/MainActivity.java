package gob.pe.munisantanita.licencias.presentation.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import gob.pe.munisantanita.licencias.R;
import gob.pe.munisantanita.licencias.presentation.utils.Tools;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private CardView mBtnQueryCamara;
    private CardView mBtnQueryForm;

    NavigationView nav_view;
    DrawerLayout drawer;

    private SharedPreferences sharedPref;
    String STRING_PREFERENCES = "licencias_sesion";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        mBtnQueryCamara = findViewById(R.id.btnQueryCamara);
        mBtnQueryForm = findViewById(R.id.btnQueryForm);
        nav_view = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);


        sharedPref = getSharedPreferences(STRING_PREFERENCES, MODE_PRIVATE);
        String nombre = sharedPref.getString("nombre", "");
        String apellido = sharedPref.getString("apellido", "");
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.txtUsuario);
        navUsername.setText(apellido + ", " + nombre);


        nav_view.setNavigationItemSelectedListener(this);
        mBtnQueryCamara.setOnClickListener(this);
        mBtnQueryForm.setOnClickListener(this);

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        Tools.setSystemBarColor(this, R.color.green_900);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            /*case R.id.nav_inicio:
                Intent main = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(main);
                break;*/
            case R.id.nav_lectura_qr:
                Intent camara = new Intent(getApplicationContext(), CamaraQRActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(camara);
                break;
            case R.id.nav_consulta:
                Intent search = new Intent(getApplicationContext(), SearchByFormActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(search);
                break;
            case R.id.nav_cerrar_sesion:
                sharedPref.edit().clear().commit();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(login);
                finish();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnQueryCamara:
                Intent intentCamara = new Intent(getApplicationContext(), CamaraQRActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentCamara);
                break;
            case R.id.btnQueryForm:
                Intent intentFormulario = new Intent(getApplicationContext(), SearchByFormActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentFormulario);
                break;
        }
    }



}
