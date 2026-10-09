// 端到端测试环境：一个装了 Adyeshach 的 Paper 测试服，加一个由 Calcite 驱动的真实客户端（OP）。
// 观察都在客户端一侧完成：Adyeshach 的实体只存在于发给玩家的数据包里，客户端看得到才算生效。
import { readdir, rm } from 'node:fs/promises';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { Client, startPaperServer } from '@bkm016/calcite';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');

export const BOT = 'AdyBot';
export const VERSION = process.env.ADY_E2E_VERSION ?? '1.21.11';

/** ADYESHACH_JAR，否则取 dist/ 下的 Adyeshach-*.jar（./gradlew build 的产物）。 */
export async function pluginJar() {
  if (process.env.ADYESHACH_JAR) return resolve(process.env.ADYESHACH_JAR);
  const jars = (await readdir(join(root, 'dist')).catch(() => [])).filter((f) => /^Adyeshach-.*\.jar$/.test(f));
  if (!jars.length) throw new Error('没有找到 Adyeshach 插件：先执行 ./gradlew build，或设置 ADYESHACH_JAR');
  return join(root, 'dist', jars.sort().at(-1));
}

export const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

/** 反复执行 fn 直到返回真值；超时抛出带最后一次结果的错误。 */
export async function until(fn, label, timeoutMs = 10_000) {
  const deadline = Date.now() + timeoutMs;
  let last;
  for (;;) {
    last = await fn();
    if (last) return last;
    if (Date.now() > deadline) throw new Error(`${label}：${timeoutMs}ms 内没有满足（最后一次：${JSON.stringify(last)}）`);
    await sleep(250);
  }
}

export class World {
  server;
  bot;
  /** 平台中心，测试都在这里进行。 */
  base;
  #port;

  async start() {
    this.server = await this.#startServer(false);
    this.#port = this.server.port;
    this.bot = new Client({
      name: BOT,
      version: VERSION,
      server: this.server.address,
      render: 'off',
      reconnect: false,
      account: { type: 'offline', username: BOT },
    });
    await this.bot.start();
    const p = (await this.bot.state()).player;
    this.base = { x: Math.floor(p.x), y: 100, z: Math.floor(p.z) };
    const { x, y, z } = this.base;
    await this.cmd(`fill ${x - 12} ${y - 1} ${z - 12} ${x + 12} ${y - 1} ${z + 12} stone`);
    await this.cmd(`gamemode creative`);
    await this.home();
  }

  async stop() {
    await this.bot?.stop();
    await this.server?.stop();
  }

  /** 关服再以同一端口、同一世界重新开服，客户端重新进入。 */
  async restartServer() {
    await this.bot.stop();
    await this.server.stop();
    this.server = await this.#startServer(true);
    await this.bot.start();
  }

  /** keepWorld 为 false 时连同 Adyeshach 的数据目录（NPC 存档、配置）一起重置。 */
  async #startServer(keepWorld) {
    const dir = process.env.ADY_E2E_DIR ?? join(root, 'e2e', '.server');
    if (!keepWorld) await rm(join(dir, 'plugins', 'Adyeshach'), { recursive: true, force: true });
    let enabled = false;
    const errors = [];
    const server = await startPaperServer({
      dir,
      version: VERSION,
      port: this.#port,
      keepWorld,
      operators: [BOT],
      plugins: [await pluginJar()],
      onLine: (line) => {
        if (process.env.ADY_E2E_VERBOSE) console.log(`[server] ${line}`);
        if (/\[Adyeshach\] Enabling Adyeshach/.test(line)) enabled = true;
        if (/(Could not load plugin|Error occurred while enabling).*Adyeshach/.test(line)) errors.push(line);
      },
    });
    if (!enabled || errors.length) {
      await server.stop();
      throw new Error(`Adyeshach 没有成功启用，详见 ${join(dir, 'logs', 'latest.log')}\n${errors.join('\n')}`);
    }
    return server;
  }

  /** 以玩家身份执行指令（Adyeshach 的 create 等指令以玩家位置为准）。 */
  async cmd(command) {
    await this.bot.command(command);
    await sleep(200);
  }

  /** 执行 Adyeshach 指令，等到聊天栏出现匹配 expect 的回复并返回它。 */
  async ady(args, expect = /./) {
    const since = this.bot.lastSeq;
    await this.bot.command(`adyeshach ${args}`);
    return until(() => this.bot.chatSince(since).find((c) => expect.test(c.message))?.message, `/adyeshach ${args} 的回复`);
  }

  /** 把玩家传送到 dimension（如 minecraft:the_nether）的 x y z，等到客户端切换过去。 */
  async travel(dimension, x, y, z) {
    await this.cmd(`execute in ${dimension} run tp @s ${x} ${y} ${z}`);
    await until(async () => {
      const p = await this.player();
      return p.dimension === dimension && Math.abs(p.x - x) < 2 && Math.abs(p.z - z) < 2;
    }, `到达 ${dimension} ${x} ${y} ${z}`, 30_000);
  }

  async home() {
    const { x, y, z } = this.base;
    await this.cmd(`execute in minecraft:overworld run tp @s ${x + 0.5} ${y} ${z + 0.5} 0 0`);
    await until(async () => {
      const p = await this.player();
      return p.dimension === 'minecraft:overworld' && near(p, this.base, 1);
    }, '回到平台', 30_000);
  }

  async player() {
    return (await this.bot.state()).player;
  }

  /** 客户端可见的某类实体（不含自己）。 */
  async visible(type, radius = 48) {
    return this.bot.entities({ type, radius });
  }

  /** 等到客户端看到满足 pred 的 type 实体，返回它。 */
  async waitFor(type, pred, label, timeoutMs) {
    return until(async () => (await this.visible(type)).find(pred), label, timeoutMs);
  }

  /** 等到客户端一个 type 实体都看不到。 */
  async waitGone(type, label, timeoutMs) {
    await until(async () => (await this.visible(type)).length === 0, label, timeoutMs);
  }
}

/** 两点水平距离不超过 d（y 只比较到 1.5 格）。 */
export function near(a, b, d) {
  return Math.hypot(a.x - (b.x + 0.5), a.z - (b.z + 0.5)) <= d && Math.abs(a.y - b.y) <= 1.5;
}
