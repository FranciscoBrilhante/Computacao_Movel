package com.example.challenge3.data;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.SimpleDateFormat;
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
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),MainRoomDatabase.class,"main_database").allowMainThreadQueries().addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static MainRoomDatabase.Callback sRoomDatabaseCallback = new MainRoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);

            databaseWriteExecutor.execute(()->{
                SampleDao sampleDao= INSTANCE.sampleDao();
                SensorDao sensorDao= INSTANCE.sensorDao();

                Sensor humiditySensor=new Sensor("Humidity",true,0,"dynamic_humidity_topic");
                Sensor temperatureSensor=new Sensor("Temperature",true,0,"dynamic_temperature_topic");

                sensorDao.insert(humiditySensor);
                sensorDao.insert(temperatureSensor);

                sampleDao.insert(new Sample(new Date(),0.9,sensorDao.getByName("Humidity").get(0).getUid()));

                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.DATE, 1);  // number of days to add
                sampleDao.insert(new Sample(c.getTime(),1.1,sensorDao.getByName("Humidity").get(0).getUid()));

                c.add(Calendar.DATE,2);
                sampleDao.insert(new Sample(c.getTime(),0.95,sensorDao.getByName("Humidity").get(0).getUid()));

                c.add(Calendar.DATE,2);
                sampleDao.insert(new Sample(c.getTime(),2,sensorDao.getByName("Temperature").get(0).getUid()));

            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            databaseWriteExecutor.execute(()->{
                SampleDao dao= INSTANCE.sampleDao();
            });
        }
    };

}

