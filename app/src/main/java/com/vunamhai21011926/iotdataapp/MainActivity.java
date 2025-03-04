package com.vunamhai21011926.iotdataapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ListView tvData;  // ListView để hiển thị dữ liệu
    private ArrayAdapter<String> adapter;  // Adapter để quản lý dữ liệu của ListView
    private List<String> dataList = new ArrayList<>();
    TextView sotien;
    Button thanhtoan;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Gán layout cho activity

        // Ánh xạ TextView từ giao diện XML
        tvData = findViewById(R.id.tvData);

        // Khởi tạo adapter với context hiện tại và danh sách dữ liệu trống
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        tvData.setAdapter(adapter);
        thanhtoan = findViewById(R.id.thanhtoan);

        // Thiết lập sự kiện click cho button
        thanhtoan.setOnClickListener(view -> {
            // Tạo một Intent để chuyển từ MainActivity sang StartActivity
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent); // Chuyển sang StartActivity
        });
        sotien = findViewById(R.id.sotien);

        // Gọi hàm để lấy dữ liệu từ server
        handler.post(runnableCode);
    }

    // Hàm lấy dữ liệu từ server
    private void fetchDataFromServer() {
        String url = "http://172.20.10.2/IoT/fetch_data.php";  // Địa chỉ API

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley", "Response received: " + response.toString());

                        try {
                            // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                            dataList.clear();
                            double totalAmount = 0;  // Đặt lại tổng số tiền về 0

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String tag = obj.getString("tag");
                                String time = obj.getString("time");
                                // Thêm dữ liệu mới vào dataList
                                dataList.add("Trạng thái: " + tag + "\nTime: " + time);

                                if (obj.has("thanhtien")) {
                                    Double thanhtien = obj.getDouble("thanhtien");
                                    Log.d("Volley", "Thanhtien: " + thanhtien);
                                    totalAmount += thanhtien;  // Cộng dồn số tiền mới
                                }
                            }

                            // Cập nhật TextView 'sotien' với số tiền mới
                            sotien.setText(String.format("Tổng tiền: %.2f", totalAmount));

                            // Cập nhật giao diện ListView
                            adapter.notifyDataSetChanged();

                            //Toast.makeText(MainActivity.this, "Dữ liệu được cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,
                                    "Error parsing data: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this,
                        "Error fetching data: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        // Tạo hàng đợi Request và thêm yêu cầu vào hàng đợi
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Gọi hàm fetchDataFromServer để gửi yêu cầu
            fetchDataFromServer();

            // Lặp lại hành động sau 5 giây (5000ms)
            handler.postDelayed(this, 1000);
        }
    };
}
