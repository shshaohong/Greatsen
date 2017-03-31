package com.sunyie.android.greatsen.network;

import com.sunyie.android.greatsen.entity.GetAllDataEntity;
import com.sunyie.android.greatsen.entity.GetDataEntity;
import com.sunyie.android.greatsen.entity.LoginEntity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by shaohong on 2017-2-27.
 */

public class GreatsenApi {
    public interface Api{
        //登陆
        @GET("/vrscount/login/{phonenumber}/{md5password}")
        Call<LoginEntity> login(@Path("phonenumber") String phonenumber,
                                @Path("md5password") String password);

        //修改密码
        @GET("/vrscount/reset/{phonenumber}/{md5oldpassword}/{md5newpassword}")
        Call<LoginEntity> resetPassowrd(@Path("phonenumber") String phonenumber,
                                        @Path("md5oldpassword") String oldPassword,
                                        @Path("md5newpassword") String newPassword);

        //获取料号、层别、批次
        @GET("/vrscount/list/{startdate}/{enddate}")
        Call<GetDataEntity> getSearch(@Path("startdate") String startDate,
                                      @Path("enddate") String enddate);

        //获取主界面信息
        @GET("/vrscount/count/{startdate}/{enddate}/{search}/{layer}/{batch}/{badnumber}")
        Call<GetAllDataEntity> getAllData(@Path("startdate") String startDate,
                                          @Path("enddate") String endDate,
                                          @Path("search") String search,
                                          @Path("layer") String layer,
                                          @Path("batch") String batch,
                                          @Path("badnumber") int badNumber);
    }
    public static Api createApi(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://121.40.70.170")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);
        return api;
    }

}
