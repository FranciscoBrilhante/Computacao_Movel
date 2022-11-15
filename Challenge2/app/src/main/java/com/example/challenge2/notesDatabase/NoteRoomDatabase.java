package com.example.challenge2.notesDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class,Topic.class},version = 1,exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    public abstract NoteDao noteDao();
    public abstract  TopicDao topicDao();

    private static volatile NoteRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS=4;
    public static final ExecutorService databaseWriteExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static NoteRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (NoteRoomDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),NoteRoomDatabase.class,"note_database").build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);

            databaseWriteExecutor.execute(()->{
                NoteDao dao= INSTANCE.noteDao();
                dao.deleteAll();

                Note note = new Note("Title1","Body1");
                dao.insert(note);
                note = new Note("Title2","Body2");
                dao.insert(note);

            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            databaseWriteExecutor.execute(()->{
                NoteDao dao= INSTANCE.noteDao();
                dao.deleteAll();

                Note note = new Note("Title1","Body1");
                dao.insert(note);
                note = new Note("Title2","Body2");
                dao.insert(note);

                TopicDao dao2=INSTANCE.topicDao();
                dao2.deleteAll();


            });
        }
    };


}
