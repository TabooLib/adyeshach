package ink.ptms.adyeshach.common.entity.script

/**
 * @Author sky
 * @Since 2020-08-25 15:59
 */
class ScriptDemo {

    /**
     * ---------- 概念 ----------
     *
     * 选择储存类型
     * use public
     * use public temporary
     * use private
     * use private temporary
     *
     * 选择实体
     * select test1 (default by id)
     * select test1,test2 by id
     * select a1s91m2e by uniqueId
     *
     * 选择储存类型及实体
     * use private select test1 by id
     * use private temporary select test1 by id
     *
     * 创建实体
     * create test villager (default at world,0,0,0,0,0)
     * create test villager at world,0,0,0
     * create test villager at world,0,0,0,90,180
     *
     * 生成，销毁、移除、删除实体
     * [spawn|respawn|destroy|remove|delete] test
     *
     * 传送
     * teleport world,0,0,0
     *
     * 移动
     * controller move 0,0,0
     * controller move x=0, y=0, z=0
     * controller move x+1
     *
     * 观察
     * controller look x=0, y=0, z=0
     *
     * 赋予 AI
     * pathfinder add GeneralMove, GeneralGravity
     * pathfinder remove GeneralGravity
     * pathfinder reset
     *
     * 元数据编辑
     * metadata set customName=custom name
     * metadata set isBaby=false, isGlowing=true
     * metadata reset isBaby, isGlowing
     *
     * 可视状态
     * viewer add BlackSKY, Arasple
     * viewer remove BlackSKY
     * viewer reset
     *
     * 创建监听
     * listen interact run teleport world,0,0,0
     * listen interact run controller move x=0, y=0, z=0
     */
}