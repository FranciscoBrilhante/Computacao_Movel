package com.example.challenge2.models;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.challenge2.notesDatabase.NoteRoomDatabase;
import com.example.challenge2.notesDatabase.Topic;
import com.example.challenge2.notesDatabase.TopicDao;

import java.util.List;

public class TopicRepository {
    private final TopicDao topicDao;
    private final LiveData<List<Topic>> allTopics;
    private final MutableLiveData<List<Topic>> topicsByTitle;


    TopicRepository(Application application){
        NoteRoomDatabase db=NoteRoomDatabase.getDatabase(application);
        topicDao=db.topicDao();
        allTopics=topicDao.getAllTopics();
        topicsByTitle=new MutableLiveData<>();

    }

    LiveData<List<Topic>> getAllTopics(){
        return this.allTopics;
    }

    MutableLiveData<List<Topic>> getTopicsByTitle(){return this.topicsByTitle;}

    void insert(Topic topic){
        NoteRoomDatabase.databaseWriteExecutor.execute(()->{
            topicDao.insert(topic);
        });
    }

    void delete(Topic topic){
        NoteRoomDatabase.databaseWriteExecutor.execute(()->{
            topicDao.delete(topic);
        });
    }

    void updateTopicsByTitle(String title){

        NoteRoomDatabase.databaseWriteExecutor.execute(()->{
            topicsByTitle.postValue(topicDao.getTopicsByTitle(title));
        });
    }




}
