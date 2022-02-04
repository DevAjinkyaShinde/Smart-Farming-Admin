package com.silentcodder.smartfarmingadmin.Notification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAATqxP0FI:APA91bEZ_oL6kHbV_vbNYOv2kqFmulmH7aOuZH3mAa_kvSLJpsCCZ9RQrfRcxEWFdef1lkYPh3fvfPlttVDJrCpFaFC05mIWgKPhY0NB9hSW0oTWDPOWwml1vuskK4YtC4STh2QmSfXA"
    })

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body NotificationSender body);

}
