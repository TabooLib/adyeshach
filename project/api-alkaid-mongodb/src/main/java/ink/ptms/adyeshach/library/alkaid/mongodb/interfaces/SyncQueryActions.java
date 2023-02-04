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
import java.util.function.Consumer;

public interface SyncQueryActions {

    boolean find(String collection, Map<String, Object> index);

    /**
     * 查询数据集合 符合的结果将进入 List 列表返回 需要提供泛型数据实体类类型
     *
     * @param collection 数据集合名称
     * @param index      数据索引 需要 new 一个目标对应的 Map kv 值 仅写入索引数据 将自动处理为 Bson
     * @param type       泛型数据实体类类型
     * @param <T>        返回的数据实体类型
     * @return 数据实体列表
     */
    <T> List<T> read(String collection, Map<String, Object> index, Class<T> type);

    /**
     * 搜索数据集合 符合的结果将进入 List 列表返回 需要提供泛型数据实体类类型 <br>
     * 本方法提供根据某一个字段名称的边界以及限制数量进行搜索的操作 <br>
     * <b>其中 top 与  bottom 值为泛型值类型 指定的字段需与存储数据的类型对应</b> <br>
     * 举例: <br>
     * number 存储数据类型为 int <br>
     * 那么 top 和 bottom 值为 int 类型 即无需双引号的拆箱类型
     *
     * @param collection 数据集合名称
     * @param data       字段名称
     * @param top        边界
     * @param bottom     边界
     * @param limit      限制值
     * @param type       泛型数据实体类类型
     * @param <T>        返回的数据实体类型
     * @param <V>        边界类型
     * @return 数据实体列表
     */
    <T, V> List<T> search(String collection, String data, V top, V bottom, int limit, Class<T> type);
}
