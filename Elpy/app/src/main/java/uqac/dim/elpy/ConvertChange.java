package uqac.dim.elpy;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class ConvertChange extends AppCompatActivity {

    TextView convertFromDropdown, convertToDropdown, convertedValue;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog, toDialog;
    Button convertButton;
    String convertFromValue, convertToValue, conversionValue;
    String[] countries = {"AED","AFN","ALL","AMD","ANG","AOA","ARS","AUD","AWG","AZN","BAM","BBD",
            "BDT","BGN","BHD","BIF","BMD","BND","BOB","BRL","BSD","BTN","BWP","BYR","BZD","CAD","CDF",
            "CHF","CLP","CNY","COP","CRC","CUP","CVE","CZK","DJF","DKK","DOP","DZD","EEK","EGP","PGK",
            "ERN","ETB","EUR","FJD","FKP","GBP","GEL","GHS","GIP","GMD","GNF","GTQ","GYD","HKD","PHP",
            "HNL","HRK","HTG","HUF","IDR","ILS","INR","IQD","IRR","ISK","JMD","JOD","JPY","KES","KGS",
            "KHR","KMF","KPW","KRW","KWD","KYD","KZT","LAK","LBP","LKR","LRD","LSL","LTL","LVL","LYD",
            "MAD","MDL","MGA","MKD","MMK","MNT","MOP","MRO","MUR","MVR","MWK","MXN","MYR","MZN","NAD",
            "NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PKR","PLN","PYG","QAR","RON","RSD","RUB",
            "RWF","SAR","SBD","SCR","SDG","SEK","SGD","SHP","SLL","SOS","SRD","STD","SVC","SYP","SZL",
            "THB","TJS","TND","TOP","TRY","TTD","TWD","TZS","UAH","UGX","USD","UYU","UZS","VED","VEF",
            "VES","VND","VUV","WST","XAF","XCD","XOF","XPF","YER","ZAR","ZMK","ZWD"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_converter);

        convertFromDropdown = findViewById(R.id.convert_from_dropdown_menu);
        convertToDropdown = findViewById(R.id.convert_to_dropdown_menu);
        convertedValue = findViewById(R.id.conversion_rate_text);
        amountToConvert = findViewById(R.id.edit_text_value_to_convert);
        convertButton = findViewById(R.id.conversion_button);

        arrayList = new ArrayList<>();
        for (String country : countries)
        {
            arrayList.add(country);
        }

        convertFromDropdown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                fromDialog = new Dialog(ConvertChange.this);
                fromDialog.setContentView(R.layout.from_spinner);
                fromDialog.getWindow().setLayout(650,800);
                fromDialog.show();

                EditText editText = fromDialog.findViewById(R.id.edit_text);
                ListView listView = fromDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ConvertChange.this,
                        android.R.layout.simple_list_item_1, arrayList);

                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convertFromDropdown.setText(adapter.getItem(position));
                        fromDialog.dismiss();
                        convertFromValue = adapter.getItem(position);
                    }
                });
            }
        });


        convertToDropdown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                toDialog = new Dialog(ConvertChange.this);
                toDialog.setContentView(R.layout.to_spinner);
                toDialog.getWindow().setLayout(650,800);
                toDialog.show();

                EditText editText = toDialog.findViewById(R.id.edit_text);
                ListView listView = toDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ConvertChange.this,
                        android.R.layout.simple_list_item_1, arrayList);

                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convertToDropdown.setText(adapter.getItem(position));
                        toDialog.dismiss();
                        convertToValue = adapter.getItem(position);
                    }
                });
            }
        });
        convertButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                    Double amountToConvert = Double.valueOf(ConvertChange.this.amountToConvert.getText().toString());
                    getConversionRate(convertFromValue, convertToValue, amountToConvert);
                 }
                 catch (Exception e)
                 {

                 }
             }
         }
        );

    }
    public String getConversionRate(String convertFrom, String convertTo, Double amountToConvert)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://free.currconv.com/api/v7/convert7?q="+convertFrom+"_"+convertTo+"&compact=ultra&apiKey=22e91ab924e2aa6f9a4";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Double conversionRateVal = round(((Double) jsonObject.get(convertFrom + "_" + convertTo)), 2);
                    conversionValue = "" + round(conversionRateVal * amountToConvert, 2);
                    convertedValue.setText(conversionValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
        return null;
    }

    public static double round(double value, int places)
    {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
