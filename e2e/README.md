# Adyeshach 端到端测试

用 [Calcite](https://github.com/bkm016/Calcite) 拉起一个装好 Adyeshach 的 Paper 测试服，再启动一个真实的 Minecraft 客户端（OP、离线账号）进服，从客户端一侧验证 NPC 的行为。Adyeshach 的实体只存在于发给玩家的数据包里，所以“客户端看得到”才是唯一可靠的判断标准。

## 覆盖内容

- `create` 生成普通实体与玩家 NPC
- `move` 寻路移动、`tp` 传送到坐标
- `clone` 复制、`remove` 删除
- 离开视距后消失、回来重新出现
- 去下界再回主世界后仍然可见
- 客户端重新进服后可见
- `save-all` 后重启服务器，实体仍在原处
- 删除后重进不再出现

## 运行

```bash
./gradlew build          # 生成 dist/Adyeshach-*.jar
cd e2e
npm install
npm test
```

首次运行会下载 Paper、对应版本的 Java 与 Minecraft 客户端，耗时较长；之后走缓存。没有显示器的 Linux 需要安装 `xvfb`。

## 环境变量

| 变量 | 说明 |
| --- | --- |
| `ADY_E2E_VERSION` | 服务器与客户端版本，默认 `1.21.11` |
| `ADYESHACH_JAR` | 指定插件 jar，默认取 `dist/` 下最新的构建 |
| `ADY_E2E_DIR` | 测试服目录，默认 `e2e/.server` |
| `ADY_E2E_VERBOSE` | 设置后打印服务器控制台输出 |

用例共享同一个服务器和客户端，按顺序执行（`--test-concurrency=1`），前一个用例生成的 NPC 会被后面的用例继续使用。
