package com.example.market.marketDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.market.marketDatabase.Converters;
import com.example.market.marketDatabase.Product;
import com.example.market.marketDatabase.ProductDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Product.class, Category.class},version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class MainRoomDatabase extends RoomDatabase {

    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();

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
            databaseWriteExecutor.execute(()->{
                super.onCreate(db);

                /*ArrayList<String> imagesURLS=new ArrayList<>();
                imagesURLS.add("/media/product_pics/1_zjNaCq8.jpg");
                Product prod1=new Product(1,"Title1","Description1",1, 60.0,1, Calendar.getInstance(),imagesURLS);
                Product prod2=new Product(2,"Title2","Description2",16, 61.0,1, Calendar.getInstance(),imagesURLS);

                ProductDao productDao = INSTANCE.productDao();
                productDao.insert(prod1);
                productDao.insert(prod2);

                String loremIpsium="Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
                Product prod3=new Product(3,"Title3",loremIpsium,3, 62.0,1, Calendar.getInstance(),imagesURLS);
                productDao.insert(prod3);*/
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

