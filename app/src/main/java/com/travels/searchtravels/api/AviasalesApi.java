package com.travels.searchtravels.api;

import android.util.Log;

import com.google.api.services.vision.v1.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AviasalesApi {
    public interface OnTicketPriceListener {
        public void onSuccess(Integer price, String countryName, String code);
        public void onError(String error);
    }
    public static void getTicketPrice(String city, OnTicketPriceListener onTicketPriceListener){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL obj = new URL("https://autocomplete.travelpayouts.com/places2?locale=en&types[]=city&term="+city);
                    HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                    //add reuqest header
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
                    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection.setRequestProperty("Content-Type", "application/json");

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = bufferedReader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    bufferedReader.close();

                    JSONArray responseJSON = new JSONArray(response.toString());
                    Log.d("myLogs", "responseJSON = " + responseJSON);
                    String code = responseJSON.getJSONObject(0).getString("code");
                    String countryName = responseJSON.getJSONObject(0).getString("country_name");

                    Log.d("myLogs", "code = " + code);

                    URL obj2 = new URL("https://api.travelpayouts.com/v1/prices/cheap?origin=LED&depart_date=2020-12&return_date=2019-12&token=471ae7d420d82eb92428018ec458623b&destination="+code);
                    HttpURLConnection connection2 = (HttpURLConnection) obj2.openConnection();

                    //add reuqest header
                    connection2.setRequestMethod("GET");
                    connection2.setRequestProperty("User-Agent", "Mozilla/5.0" );
                    connection2.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    connection2.setRequestProperty("Content-Type", "application/json");

                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
                    String inputLine2;
                    StringBuffer response2 = new StringBuffer();

                    while ((inputLine2 = bufferedReader2.readLine()) != null) {
                        response2.append(inputLine2);
                    }
                    bufferedReader2.close();

                    JSONObject responseJSON2 = new JSONObject(response2.toString());
                    Log.d("myLogs", "responseJSON2 = " + responseJSON2);

                    Integer ticketPrice = 6200;

                    try{
                        ticketPrice = Integer.parseInt(responseJSON2.getJSONObject("data").getJSONObject(code).getJSONObject("1").getString("price"));
                        onTicketPriceListener.onSuccess(ticketPrice, countryName,code);
                    }catch (Exception e){
                        onTicketPriceListener.onError(e.toString());
                    }
                }catch (Exception e){
                    onTicketPriceListener.onError(e.toString());
                }
            }
        });
        thread.run();


    }
}
