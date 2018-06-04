//package com.nio.so.service.steward.data.repository;
//
//import com.facebook.stetho.okhttp3.StethoInterceptor;
//import com.google.gson.FieldNamingPolicy;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//import okhttp3.Interceptor;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;
//
///**
// * User: tangpeng.yang
// * Date: 20/03/2018
// * Description:
// * FIXME
// */
//public class ServiceFactor {
//
//  public static <T> T createService(String host, Boolean isDebug, Class<T> clazz) {
//    OkHttpClient okHttpClient = createOkHttpClient(isDebug, createLoggingInterceptor(isDebug));
//    return createService(host, okHttpClient, createGson(), clazz);
//  }
//
//  private static <T> T createService(String host, OkHttpClient okHttpClient, Gson gson, Class<T> clazz) {
//    Retrofit retrofit = new Retrofit.Builder().baseUrl(host)
//        .client(okHttpClient)
//        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//        .addConverterFactory(GsonConverterFactory.create(gson))
//        .build();
//    return retrofit.create(clazz);
//  }
//
//  private static OkHttpClient createOkHttpClient(Boolean isDebug, HttpLoggingInterceptor httpLoggingInterceptor) {
//    OkHttpClient.Builder builder = new OkHttpClient.Builder();
//    builder.addInterceptor(httpLoggingInterceptor).addInterceptor(createHeadInterceptor());
//    //if (isDebug) {
//    builder.addNetworkInterceptor(new StethoInterceptor());
//    //}
//    return builder.connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).build();
//  }
//
//  private static Gson createGson() {
//    return new GsonBuilder().setLenient()
//        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//        .create();
//  }
//
//  private static Interceptor createHeadInterceptor() {
//    return new Interceptor() {
//      @Override public Response intercept(Chain chain) throws IOException {
//        Request original = chain.request();
//        Request request = original.newBuilder()
//            .header("Content-Type", "application/json;charset=UTF-8")
//            .method(original.method(), original.body())
//            .build();
//        return chain.proceed(request);
//      }
//    };
//  }
//
//  private static HttpLoggingInterceptor createLoggingInterceptor(Boolean isDebug) {
//    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//    if (isDebug) {
//      logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//    } else {
//      logging.setLevel(HttpLoggingInterceptor.Level.NONE);
//    }
//    return logging;
//  }
//}
