package com.example.aps2021v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout homeCL;
    private ProgressBar loadingPB;
    private TextView nomeCidadeTV, temperaturaTV, condicaoTV;
    private RecyclerView weatherRV;
    private TextInputEditText cidadeEdt;
    private ImageView backIV,iconIV,searchIV;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cidadeNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        homeCL = findViewById(R.id.idCLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        nomeCidadeTV = findViewById(R.id.idTVCidadeNome);
        temperaturaTV = findViewById(R.id.idTVTemperatura);
        condicaoTV = findViewById(R.id.idTVCondicao);
        weatherRV = findViewById(R.id.idRvWeather);
        cidadeEdt = findViewById(R.id.idEDtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this,weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED);

        {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cidadeNome = getCidadeNome(location.getLongitude(),location.getLatitude());
        
        getWeatherInfo(cidadeNome);
        
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cidade = cidadeEdt.getText().toString();
                if(cidade.isEmpty()){
                    Toast.makeText(MainActivity.this, "Digite o nome da citade", Toast.LENGTH_SHORT).show();
                }else {
                    nomeCidadeTV.setText(cidadeNome);
                    getWeatherInfo(cidade);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Aceite a permissão", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCidadeNome (double longitude, double latitude){
        String cidadeNome = "Náo encontrado";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);

            for (Address adr : addresses){
                if(adr!=null){
                    String cidade = adr.getLocality();
                    if(cidade!=null && !cidade.equals("")){
                        cidadeNome = cidade;
                    }else {
                        Log.d("TAG", "CIDADE NÃO ENCONTRADA");
                        Toast.makeText(this, "Cidade não encontrada...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cidadeNome;
    }

    private void getWeatherInfo(String NomeCidade){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=f346ba99bc574f5494233647212710&q="+NomeCidade+"&days=1&aqi=no&alerts=no";

        nomeCidadeTV.setText(cidadeNome);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeCL.setVisibility(View.VISIBLE);
                weatherRVModalArrayList.clear();

                try {
                    String temperatura = response.getJSONObject("current").getString("temp_c");
                    temperaturaTV.setText(temperatura+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condicao = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String condicaoIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(condicaoIcon)).into(iconIV);
                    condicaoTV.setText(condicao);
                    if(isDay==1){
                        Picasso.get().load("https://www.google.com/search?q=imagens+c%C3%A9u+azul&tbm=isch&ved=2ahUKEwih8tGP8-nzAhVzNLkGHTL2B1gQ2-cCegQIABAA&oq=imagens+ceu&gs_lcp=CgNpbWcQARgEMgcIIxDvAxAnMgcIIxDvAxAnMgUIABCABDIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgARQlRpYlRpgrjloAHAAeACAAWSIAcMBkgEDMS4xmAEAoAEBqgELZ3dzLXdpei1pbWfAAQE&sclient=img&ei=C-l4YeHAAfPo5OUPsuyfwAU&bih=677&biw=1440#imgrc=U6XfLsBLJ5JqUM").into(backIV);
                    }else {
                        Picasso.get().load("https://www.google.com/search?q=ceu+estrelado&sxsrf=AOaemvIMD0YVQBLWucOaH_2_T-Q6PUP13g:1635313996804&source=lnms&tbm=isch&sa=X&sqi=2&ved=2ahUKEwiOloCv8-nzAhXQrJUCHZjJAGEQ_AUoAXoECAEQAw&biw=1440&bih=677&dpr=1#imgrc=QQrgVsp-D6lliM").into(backIV);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for (int i=0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String tempt = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModalArrayList.add(new WeatherRVModal(time,tempt,img,wind));

                    }

                    weatherRVAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Entre com uma cidade valida", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}