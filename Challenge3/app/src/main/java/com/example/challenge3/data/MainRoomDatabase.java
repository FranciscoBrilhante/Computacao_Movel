package com.example.challenge3.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Sample.class, Sensor.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MainRoomDatabase extends RoomDatabase {

    public abstract SampleDao sampleDao();
    public abstract SensorDao sensorDao();

    private static volatile MainRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS=4;
    public static final ExecutorService databaseWriteExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MainRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (MainRoomDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),MainRoomDatabase.class,"main_database").addCallback(sRoomDatabaseCallback).build();
                    //INSTANCE= Room.databaseBuilder(context.getApplicationContext(),MainRoomDatabase.class,"main_database").build();
                }
            }
        }
        return INSTANCE;
    }

    private static final MainRoomDatabase.Callback sRoomDatabaseCallback = new MainRoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);

            databaseWriteExecutor.execute(()->{
                SampleDao sampleDao= INSTANCE.sampleDao();
                SensorDao sensorDao= INSTANCE.sensorDao();

                Sensor humiditySensor=new Sensor("Humidity",true,50,"dynamic_humidity_topic");
                Sensor temperatureSensor=new Sensor("Temperature",true,40,"dynamic_temperature_topic");

                sensorDao.insert(humiditySensor);
                sensorDao.insert(temperatureSensor);

            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            databaseWriteExecutor.execute(()->{

            });
        }
    };

}

