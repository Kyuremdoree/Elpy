[1mdiff --git a/Elpy/app/src/main/AndroidManifest.xml b/Elpy/app/src/main/AndroidManifest.xml[m
[1mindex b81b290..b358c49 100644[m
[1m--- a/Elpy/app/src/main/AndroidManifest.xml[m
[1m+++ b/Elpy/app/src/main/AndroidManifest.xml[m
[36m@@ -13,9 +13,9 @@[m
         android:allowBackup="true"[m
         android:dataExtractionRules="@xml/data_extraction_rules"[m
         android:fullBackupContent="@xml/backup_rules"[m
[31m-        android:icon="@mipmap/ic_launcher"[m
[32m+[m[32m        android:icon="@drawable/elpy"[m
         android:label="@string/app_name"[m
[31m-        android:roundIcon="@mipmap/ic_launcher_round"[m
[32m+[m[32m        android:roundIcon="@drawable/elpy"[m
         android:supportsRtl="true"[m
         android:theme="@style/Theme.Elpy"[m
         tools:targetApi="31">[m
[1mdiff --git a/Elpy/app/src/main/java/uqac/dim/elpy/ConvertChange.java b/Elpy/app/src/main/java/uqac/dim/elpy/ConvertChange.java[m
[1mindex c60fca7..cac2009 100644[m
[1m--- a/Elpy/app/src/main/java/uqac/dim/elpy/ConvertChange.java[m
[1m+++ b/Elpy/app/src/main/java/uqac/dim/elpy/ConvertChange.java[m
[36m@@ -21,4 +21,5 @@[m [mpublic class ConvertChange extends AppCompatActivity {[m
         fragmentTransaction.replace(R.id.fragment_currency_converter_container, convertChangeFragment);[m
         fragmentTransaction.commit();[m
     }[m
[32m+[m
 }[m
[1mdiff --git a/Elpy/app/src/main/java/uqac/dim/elpy/ConvertChangeFragment.java b/Elpy/app/src/main/java/uqac/dim/elpy/ConvertChangeFragment.java[m
[1mindex 9e495d3..99c291b 100644[m
[1m--- a/Elpy/app/src/main/java/uqac/dim/elpy/ConvertChangeFragment.java[m
[1m+++ b/Elpy/app/src/main/java/uqac/dim/elpy/ConvertChangeFragment.java[m
[36m@@ -6,6 +6,7 @@[m [mimport android.app.Dialog;[m
 import android.os.Bundle;[m
 import android.text.Editable;[m
 import android.text.TextWatcher;[m
[32m+[m[32mimport android.util.Log;[m
 import android.view.LayoutInflater;[m
 import android.view.View;[m
 import android.view.ViewGroup;[m
[36m@@ -58,12 +59,15 @@[m [mpublic class ConvertChangeFragment extends Fragment {[m
 [m
         View view = inflater.inflate(R.layout.currency_converter, container, false);[m
 [m
[32m+[m
         convertFromDropdown = view.findViewById(R.id.convert_from_dropdown_menu);[m
         convertToDropdown = view.findViewById(R.id.convert_to_dropdown_menu);[m
         convertedValue = view.findViewById(R.id.conversion_rate_text);[m
         amountToConvert = view.findViewById(R.id.edit_text_value_to_convert);[m
         convertButton = view.findViewById(R.id.conversion_button);[m
 [m
[32m+[m
[32m+[m
         arrayList = new ArrayList<>();[m
         for (String country : countries)[m
         {[m
[36m@@ -76,7 +80,7 @@[m [mpublic class ConvertChangeFragment extends Fragment {[m
             {[m
                 fromDialog = new Dialog(requireContext());[m
                 fromDialog.setContentView(R.layout.from_spinner);[m
[31m-                fromDialog.getWindow().setLayout(650,800);[m
[32m+[m[32m                fromDialog.getWindow().setLayout(700,850);[m
                 fromDialog.show();[m
 [m
                 EditText editText = fromDialog.findViewById(R.id.edit_text);[m
[1mdiff --git a/Elpy/app/src/main/java/uqac/dim/elpy/MainActivity.java b/Elpy/app/src/main/java/uqac/dim/elpy/MainActivity.java[m
[1mindex bf3ff9a..619345c 100644[m
[1m--- a/Elpy/app/src/main/java/uqac/dim/elpy/MainActivity.java[m
[1m+++ b/Elpy/app/src/main/java/uqac/dim/elpy/MainActivity.java[m
[36m@@ -44,8 +44,7 @@[m [mpublic class MainActivity extends AppCompatActivity implements NavigationView.On[m
         Fragment newFragment = null;[m
 [m
         if (itemId == R.id.fConvertisseur) {[m
[31m-            Intent intent = new Intent(MainActivity.this, ConvertChange.class);[m
[31m-            startActivity(intent);[m
[32m+[m[32m            newFragment = new ConvertChangeFragment();[m
         }[m
         else if (itemId == R.id.fPriseNote) {[m
             newFragment = new NoteSystem();[m
