package com.example.nishant.imageloader;

import com.example.nishant.imageloader.models.MasterResponse;
import com.example.nishant.imageloader.network.ExecutorService;
import com.example.nishant.imageloader.network.RequestQueue;
import com.example.nishant.imageloader.network.parser.JsonParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by nishant on 11/30/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class MindValleyAppTest {

    private static final String TAG = MindValleyAppTest.class.getSimpleName();

    @Test
    public void requestQueueOverFlow() throws Exception {
        RequestQueue requests = new RequestQueue(50);
        for (int i = 0; i < 100; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                }
            };
            requests.add(runnable);
        }
        assertEquals(50, requests.size());
    }

    @Test
    public void testListParser() throws Exception {
        JsonParser jsonParser = new JsonParser();
        ArrayList<MasterResponse> resp = (ArrayList<MasterResponse>) jsonParser.toList("[\n" +
                "   {\n" +
                "      \"id\":\"4kQA1aQK8-Y\",\n" +
                "      \"created_at\":\"2016-05-29T15:42:02-04:00\",\n" +
                "      \"width\":2448,\n" +
                "      \"height\":1836,\n" +
                "      \"color\":\"#060607\",\n" +
                "      \"likes\":12,\n" +
                "      \"liked_by_user\":false,\n" +
                "      \"user\":{\n" +
                "         \"id\":\"OevW4fja2No\",\n" +
                "         \"username\":\"nicholaskampouris\",\n" +
                "         \"name\":\"Nicholas Kampouris\",\n" +
                "         \"profile_image\":{\n" +
                "            \"small\":\"https://images.unsplash.com/profile-1464495186405-68089dcd96c3?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=faces\\u0026fit=crop\\u0026h=32\\u0026w=32\\u0026s=63f1d805cffccb834cf839c719d91702\",\n" +
                "            \"medium\":\"https://images.unsplash.com/profile-1464495186405-68089dcd96c3?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=faces\\u0026fit=crop\\u0026h=64\\u0026w=64\\u0026s=ef631d113179b3137f911a05fea56d23\",\n" +
                "            \"large\":\"https://images.unsplash.com/profile-1464495186405-68089dcd96c3?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=faces\\u0026fit=crop\\u0026h=128\\u0026w=128\\u0026s=622a88097cf6661f84cd8942d851d9a2\"\n" +
                "         },\n" +
                "         \"links\":{\n" +
                "            \"self\":\"https://api.unsplash.com/users/nicholaskampouris\",\n" +
                "            \"html\":\"http://unsplash.com/@nicholaskampouris\",\n" +
                "            \"photos\":\"https://api.unsplash.com/users/nicholaskampouris/photos\",\n" +
                "            \"likes\":\"https://api.unsplash.com/users/nicholaskampouris/likes\"\n" +
                "         }\n" +
                "      },\n" +
                "      \"current_user_collections\":[\n" +
                "\n" +
                "      ],\n" +
                "      \"urls\":{\n" +
                "         \"raw\":\"https://images.unsplash.com/photo-1464550883968-cec281c19761\",\n" +
                "         \"full\":\"https://images.unsplash.com/photo-1464550883968-cec281c19761?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=entropy\\u0026s=4b142941bfd18159e2e4d166abcd0705\",\n" +
                "         \"regular\":\"https://images.unsplash.com/photo-1464550883968-cec281c19761?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=entropy\\u0026w=1080\\u0026fit=max\\u0026s=1881cd689e10e5dca28839e68678f432\",\n" +
                "         \"small\":\"https://images.unsplash.com/photo-1464550883968-cec281c19761?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=entropy\\u0026w=400\\u0026fit=max\\u0026s=d5682032c546a3520465f2965cde1cec\",\n" +
                "         \"thumb\":\"https://images.unsplash.com/photo-1464550883968-cec281c19761?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=entropy\\u0026w=200\\u0026fit=max\\u0026s=9fba74be19d78b1aa2495c0200b9fbce\"\n" +
                "      },\n" +
                "      \"categories\":[\n" +
                "         {\n" +
                "            \"id\":4,\n" +
                "            \"title\":\"Nature\",\n" +
                "            \"photo_count\":46148,\n" +
                "            \"links\":{\n" +
                "               \"self\":\"https://api.unsplash.com/categories/4\",\n" +
                "               \"photos\":\"https://api.unsplash.com/categories/4/photos\"\n" +
                "            }\n" +
                "         },\n" +
                "         {\n" +
                "            \"id\":6,\n" +
                "            \"title\":\"People\",\n" +
                "            \"photo_count\":15513,\n" +
                "            \"links\":{\n" +
                "               \"self\":\"https://api.unsplash.com/categories/6\",\n" +
                "               \"photos\":\"https://api.unsplash.com/categories/6/photos\"\n" +
                "            }\n" +
                "         }\n" +
                "      ],\n" +
                "      \"links\":{\n" +
                "         \"self\":\"https://api.unsplash.com/photos/4kQA1aQK8-Y\",\n" +
                "         \"html\":\"http://unsplash.com/photos/4kQA1aQK8-Y\",\n" +
                "         \"download\":\"http://unsplash.com/photos/4kQA1aQK8-Y/download\"\n" +
                "      }\n" +
                "   },\n" +
                "   ]\n", MasterResponse.class);
        assertTrue(resp != null);
    }

    @Test
    public void testJsonParser() throws Exception {
        JsonParser jsonParser = new JsonParser();
        JsonTestClass masterResponse = jsonParser.parse("{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"A green door\",\n" +
                "    \"price\": 12.50,\n" +
                "    \"tags\": [\"home\", \"green\"]\n" +
                "}", JsonTestClass.class);
        assertTrue(masterResponse != null);
    }

    @Test
    public void testExecutor() {
        LinkedBlockingDeque<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<>();
        ExecutorService executorService = new ExecutorService(linkedBlockingDeque);
        final boolean[] didRun = {false};
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                didRun[0] = true;
            }
        });

        assertArrayEquals(didRun, new boolean[]{true});
    }

}
