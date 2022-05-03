package com.example.goputapplication.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAL5OpZuE:APA91bFWWSL_dN2qCkdJxADxXlyvJkOgkV_IR1nSQOhpJdrm7fVG3-hBGvNpy0_ro3UbpWGxfdd-W7mIgB_2QALAwCavmVmxraskujQzbsHQojtKVrSJG3-fypz1-Zr9Q032YcbhHDx5"
            }
    )
//    @Url("https://fcm.googleapis.com/fcm/send")
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
