/*
 * Copyright 2022 Alkaid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alkaidmc.alkaid.mongodb;

import com.alkaidmc.alkaid.mongodb.interfaces.SyncQueryActions;
import com.alkaidmc.alkaid.mongodb.interfaces.WriteableActions;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SyncMongodbConnection implements WriteableActions, SyncQueryActions {

    final Gson gson;
    final MongoDatabase database;

    public SyncMongodbConnection(Gson gson, MongoDatabase database) {
        this.gson = gson;
        this.database = database;
    }

    @Override
    public void create(String collection, Object data) {
        database.getCollection(collection)
                .insertOne(Document.parse(gson.toJson(data)));
    }

    @Override
    public void create(String collection, List<Object> data) {
        database.getCollection(collection)
                .insertMany(data.stream()
                        .map(d -> Document.parse(gson.toJson(d)))
                        .collect(Collectors.toList()));
    }

    @Override
    public void update(String collection, Map<String, Object> index, Object data) {
        database.getCollection(collection)
                .updateMany(
                        Document.parse(gson.toJsonTree(index, Map.class).toString()),
                        Document.parse(gson.toJson(data))
                );
    }

    @Override
    public void delete(String collection, Map<String, Object> index) {
        database.getCollection(collection)
                .deleteMany(Document.parse(gson.toJsonTree(index, Map.class).toString()));
    }

    // todo 可能存在内存泄漏
    @Override
    public <T> List<T> read(String collection, Map<String, Object> index, Class<T> type) {
        List<T> list = new ArrayList<>();
        // 获取数据库连接
        database.getCollection(collection)
                .find(Document.parse(gson.toJsonTree(index, Map.class).toString()))
                .iterator()
                .forEachRemaining(document -> {
                    // 将 document 转换回对象
                    list.add(gson.fromJson(document.toJson(), type));
                });
        return list;
    }

    @Override
    public <T, V> List<T> search(String collection, String data, V top, V bottom, int limit, Class<T> type) {
        List<T> list = new ArrayList<>();
        // 获取数据库连接
        database.getCollection(collection)
                .find(new BasicDBObject() {{
                    put(data, new BasicDBObject() {{
                        put("$gte", top);
                        put("$lte", bottom);
                    }});
                }})
                .skip(0)
                .limit(limit)
                .iterator()
                .forEachRemaining(document -> {
                    // 将 document 转换回对象
                    list.add(gson.fromJson(document.toJson(), type));
                });
        return list;
    }
}
