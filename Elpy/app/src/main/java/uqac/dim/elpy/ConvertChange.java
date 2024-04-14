package uqac.dim.elpy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ConvertChange extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_converter_main);

        // Crée une instance de votre fragment
        ConvertChangeFragment convertChangeFragment = new ConvertChangeFragment();

        // Ajoute le fragment au conteneur
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_currency_converter_container, convertChangeFragment);
        fragmentTransaction.commit();
    }

}
