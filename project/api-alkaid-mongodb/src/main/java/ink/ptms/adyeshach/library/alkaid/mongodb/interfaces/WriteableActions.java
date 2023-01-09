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

package ink.ptms.adyeshach.library.alkaid.mongodb.interfaces;

import java.util.List;
import java.util.Map;

public interface WriteableActions {

    /**
     * 创建一个数据文档
     *
     * @param collection 数据集合名称
     * @param data       数据实体
     */
    void create(String collection, Object data);

    /**
     * 创建一打数据集合
     *
     * @param collection 数据集合名称
     * @param data       数据实体
     */
    void create(String collection, List<Object> data);

    /**
     * 更新一些数据文档
     *
     * @param collection 数据集合名称
     * @param index      数据索引 需要 new 一个目标对应的 Map kv 值 仅写入索引数据 将自动处理为 Bson
     * @param data       数据实体 需要更新进入数据库的数据实体
     */
    void update(String collection, Map<String, Object> index, Object data);

    /**
     * 删除一些数据文档
     *
     * @param collection 数据集合名称
     * @param index      数据索引 需要 new 一个目标对应的 Map kv 值 仅写入索引数据 将自动处理为 Bson
     */
    void delete(String collection, Map<String, Object> index);
}
