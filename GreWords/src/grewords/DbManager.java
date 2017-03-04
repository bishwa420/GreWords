/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grewords;

import java.net.UnknownHostException;
import java.util.Date;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Bishwajit
 */
public class DbManager {

    private String collectionName = null;
    private String databaseName = null;
    private MongoClient mongoClient = null;
    private DB database = null;
    private DBCollection table = null;// = db.getCollection("user");

    public DbManager(String name) throws UnknownHostException {
        this.collectionName = name;
        this.databaseName = name;
        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDB(databaseName);
        table = database.getCollection(collectionName);
    }
    
    public String[] randomWord(){
        DBCursor cursor = table.find();
        int n = cursor.count();
        System.out.println("cursor.count: " + n);
        int cnt=0,randomNum = ThreadLocalRandom.current().nextInt(0, n);
        while(cursor.hasNext() && cnt < randomNum){
            System.out.println("cnt: " + cnt + " cursor: " + cursor.next());
            cnt++;
        }
        String[] ret;
        if(cursor.hasNext()){
            ret = cursor.next().toMap().toString().split(",");
            return ret;
        }
        return null;
    }

    public boolean addItem(ArrayList<String> args) {
        BasicDBObject document = new BasicDBObject();
        for(int i=0; i<args.size(); i+=2){
            document.put(args.get(i), args.get(i+1));
        }
        table.insert(document);
        return true;
    }

    public String removeItem(String word) {
        BasicDBObject document = new BasicDBObject();
        document.put("word", word);
        table.remove(document);
        return "success";
    }

    public String changeItem(ArrayList<String> strings) {
        BasicDBObject query = new BasicDBObject();

        query.put(strings.get(0), strings.get(1));
        BasicDBObject newDocument = new BasicDBObject();
        for (int i = 2; i < strings.size(); i += 2) {
            if (i == 2) {
                newDocument.put(strings.get(i), strings.get(i + 1));
            } else {
                newDocument.append(strings.get(i), strings.get(i + 1));
            }

        }

        BasicDBObject updateObj = new BasicDBObject();
        updateObj.put("$set", newDocument);

        table.update(query, updateObj);
        return "success";
    }

    public String[] searchWord(String word) {
        BasicDBObject searchWord = new BasicDBObject();
        searchWord.put("word", word);
        DBCursor cursor = table.find(searchWord);
        if (cursor.hasNext() == true) {
            String[] ret = cursor.next().toMap().toString().split(",");
            for (String str : ret) {
                System.out.println("str: " + str);
            }
            return ret;
        } else{
            return null;
        }
    }
}
