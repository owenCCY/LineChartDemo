package com.example.sensordatademo;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView progressTextView;
    TextView data;
    Map<Integer, Float> map = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        progressTextView = (TextView) findViewById(R.id.progressTextView);
        data = (TextView) findViewById(R.id.data);

        progressTextView.setText("");
        Button btn = (Button) findViewById(R.id.getDataButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData retrieveData = new GetData();
                retrieveData.execute("");
            }
        });
    }

    private class GetData extends AsyncTask<String, String, String>{

        String msg = "";
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String URL = DbStrings.URL;

        @Override
        protected void onPreExecute(){
            progressTextView.setText("Connecting to database ... ");
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection conn = null;
            Statement stmt = null;
            try{
                Class.forName(JDBC_DRIVER).newInstance();
                ///////////
                conn = DriverManager.getConnection(URL, DbStrings.USERNAME, DbStrings.PASSWORD);
                ///////////
                stmt = conn.createStatement();
                String sql = "SELECT * FROM Sensor.SensorData";
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    Integer id = rs.getInt("Id");
                    float quantity = rs.getFloat("Quantity");
                    map.put(id, quantity);
                }
                msg = "Process complete.";
                rs.close();
                stmt.close();
                conn.close();

            }catch(SQLException connError){
                msg = connError.toString();
                connError.printStackTrace();
            } catch (ClassNotFoundException e) {
                msg = "JDBC NOT FOUND.";
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } finally {
                try{
                    if(stmt != null) {
                        stmt.close();
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
                try{
                    if(conn != null) {
                        conn.close();
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String msg){

            progressTextView.setText(this.msg);

            if(map.size() > 0){
                data.setText(String.valueOf(map.get(5)));
            }else{
                data.setText("Failed");
            }
        }
    }

}//
