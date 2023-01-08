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

import taboolib.common.env.RuntimeDependency;

@RuntimeDependency(
        value = "org.mongodb:mongo-java-driver:3.12.11",
        test = "!com.mongodb_3_12_11.client.MongoDatabase"
)
public class AlkaidMongodb {

    public static SyncMongodbConnector sync() {
        return new SyncMongodbConnector();
    }

    public static AsyncMongodbConnector async() {
        return new AsyncMongodbConnector();
    }
}
