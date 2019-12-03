package com.travel.cotravel.fragment.chat.module;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAKg7GvHw:APA91bFaKMNRaGrjYAk104o7V8qWsJGyzVUAog8eO-0AJrr9GH0-LUiNQ01yHe6emfFZyT0YtD6MFeiUDhqVRHw0Un9pO8hpdDexHEqBod0X1kYL0K-vAC_LeTC_VJqJB0b9B7vnsOVX"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
