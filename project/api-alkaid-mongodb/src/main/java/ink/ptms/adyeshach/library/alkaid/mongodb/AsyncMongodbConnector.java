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

package ink.ptms.adyeshach.library.alkaid.mongodb;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncMongodbConnector {

    String host = "127.0.0.1";
    int port = 27017;
    String database;
    String username;
    String password;
    Gson gson = new Gson();
    MongoClientOptions options = null;

    // 线程池参数
    int thread = 16;

    // 托管 Client 的实例
    MongoClient client = null;

    ExecutorService pool = null;

    public AsyncMongodbConnector connect() {
        pool = Executors.newFixedThreadPool(thread);
        client = Optional.ofNullable(client).orElseGet(() -> {
            if (username != null && password != null) {
                MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
                return new MongoClient(new ServerAddress(host, port), credential, Optional.ofNullable(options).orElse(MongoClientOptions.builder().build()));
            } else {
                return new MongoClient(new ServerAddress(host, port), Optional.ofNullable(options).orElse(MongoClientOptions.builder().build()));
            }
        });
        return this;
    }


    public AsyncMongodbConnection connection() {
        return new AsyncMongodbConnection(gson, client.getDatabase(database), pool);
    }

    public void disconnect() {
        client.close();
    }

    public String host() {
        return host;
    }

    public AsyncMongodbConnector host(String host) {
        this.host = host;
        return this;
    }

    public int port() {
        return port;
    }

    public AsyncMongodbConnector port(int port) {
        this.port = port;
        return this;
    }

    public String database() {
        return database;
    }

    public AsyncMongodbConnector database(String database) {
        this.database = database;
        return this;
    }

    public String username() {
        return username;
    }

    public AsyncMongodbConnector username(String username) {
        this.username = username;
        return this;
    }

    public String password() {
        return password;
    }

    public AsyncMongodbConnector password(String password) {
        this.password = password;
        return this;
    }

    public Gson gson() {
        return gson;
    }

    public AsyncMongodbConnector gson(Gson gson) {
        this.gson = gson;
        return this;
    }

    public MongoClientOptions options() {
        return options;
    }

    public AsyncMongodbConnector options(MongoClientOptions options) {
        this.options = options;
        return this;
    }

    public int thread() {
        return thread;
    }

    public AsyncMongodbConnector thread(int thread) {
        this.thread = thread;
        return this;
    }

    public MongoClient client() {
        return client;
    }

    public AsyncMongodbConnector client(MongoClient client) {
        this.client = client;
        return this;
    }

    public ExecutorService pool() {
        return pool;
    }

    public AsyncMongodbConnector pool(ExecutorService pool) {
        this.pool = pool;
        return this;
    }
}
