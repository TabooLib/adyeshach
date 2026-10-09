// 用真实客户端验证 NPC 的生命周期：生成、移动、传送、克隆、删除，以及离开视距、跨世界、重进、重启后的可见性。
// 用例按顺序依赖同一个服务器和客户端，必须串行执行。
import assert from 'node:assert/strict';
import { after, before, test } from 'node:test';
import { World, near, sleep, until } from './harness.mjs';

const world = new World();
const ZOMBIE = 'minecraft:zombie';
const HUMAN = 'minecraft:player';

/** 平台上相对中心 (dx, dz) 的方块位置。 */
const at = (dx, dz) => ({ x: world.base.x + dx, y: world.base.y, z: world.base.z + dz });
const zombieAt = (dx, dz, label, timeoutMs) => world.waitFor(ZOMBIE, (e) => near(e, at(dx, dz), 0.6), label, timeoutMs);
const humanAt = (dx, dz, label, timeoutMs) => world.waitFor(HUMAN, (e) => near(e, at(dx, dz), 0.6), label, timeoutMs);

before(() => world.start(), { timeout: 600_000 });
after(() => world.stop(), { timeout: 120_000 });

test('create 在玩家位置生成实体', async () => {
  await world.ady('create ZOMBIE e2e_zombie', /已创建/);
  await zombieAt(0, 0, '僵尸出现在平台中心');
});

test('create 生成玩家 NPC', async () => {
  await world.cmd(`tp @s ${at(-3, 0).x + 0.5} ${at(0, 0).y} ${at(0, 0).z + 0.5}`);
  await world.ady('create PLAYER e2e_human', /已创建/);
  await humanAt(-3, 0, '玩家 NPC 出现');
  await world.home();
});

test('move 让实体走到目标方块中心', async () => {
  const exactly = (dx, dz, label) => world.waitFor(ZOMBIE, (e) => near(e, at(dx, dz), 0.1), label, 20_000);
  await world.ady('move e2e_zombie to ~4 ~ ~', /正在派遣/);
  await exactly(4, 0, '僵尸走到 (4, 0)');
  await world.ady('move e2e_zombie to ~-2 ~ ~3', /正在派遣/);
  await exactly(2, 3, '僵尸斜向走到 (2, 3)');
});

test('tp 把实体传送到坐标', async () => {
  const { x, y, z } = at(2, 5);
  await world.ady(`tp e2e_zombie to world ${x + 0.5} ${y} ${z + 0.5}`, /传送至/);
  await zombieAt(2, 5, '僵尸被传送到 (2, 5)');
});

test('clone 在玩家位置复制一份，remove 删除', async () => {
  await world.ady('clone e2e_zombie e2e_zombie_copy', /克隆为/);
  await zombieAt(0, 0, '克隆体出现在玩家位置');
  assert.equal((await world.visible(ZOMBIE)).length, 2);
  await world.ady('remove e2e_zombie_copy', /回收站/);
  await world.waitFor(ZOMBIE, () => true, '原僵尸仍在');
  await until(async () => (await world.visible(ZOMBIE)).length === 1, '克隆体消失');
  await zombieAt(2, 5, '原僵尸位置不变');
});

test('离开视距后消失，回来后重新出现', async () => {
  const far = at(400, 0);
  await world.cmd(`tp @s ${far.x} ${far.y} ${far.z}`);
  await world.waitGone(ZOMBIE, '远离后僵尸消失', 20_000);
  await world.waitGone(HUMAN, '远离后玩家 NPC 消失', 20_000);
  await world.home();
  await zombieAt(2, 5, '回来后僵尸重新出现', 20_000);
  await humanAt(-3, 0, '回来后玩家 NPC 重新出现', 20_000);
});

test('去下界再回来，实体仍然可见', async () => {
  await world.travel('minecraft:the_nether', 0, 70, 0);
  await world.waitGone(ZOMBIE, '下界看不到主世界的僵尸', 20_000);
  await world.home();
  await zombieAt(2, 5, '回到主世界后僵尸可见', 20_000);
  await humanAt(-3, 0, '回到主世界后玩家 NPC 可见', 20_000);
});

test('客户端重新进入服务器后实体可见', async () => {
  await world.bot.stop();
  await world.bot.start();
  await world.home();
  await zombieAt(2, 5, '重进后僵尸可见', 20_000);
  await humanAt(-3, 0, '重进后玩家 NPC 可见', 20_000);
});

test('save-all 后重启服务器，实体仍在原处', { timeout: 600_000 }, async () => {
  await world.ady('save-all', /Saved/);
  await world.restartServer();
  await world.home();
  await zombieAt(2, 5, '重启后僵尸仍在', 30_000);
  await humanAt(-3, 0, '重启后玩家 NPC 仍在', 30_000);
});

test('remove 后实体消失，重进也不再出现', async () => {
  await world.ady('remove e2e_zombie', /回收站/);
  await world.ady('remove e2e_human', /回收站/);
  await world.waitGone(ZOMBIE, '僵尸被删除');
  await world.waitGone(HUMAN, '玩家 NPC 被删除');
  await world.bot.stop();
  await world.bot.start();
  await world.home();
  await sleep(3000);
  assert.deepEqual(await world.visible(ZOMBIE), []);
  assert.deepEqual(await world.visible(HUMAN), []);
});

