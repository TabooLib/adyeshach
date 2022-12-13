package ink.ptms.adyeshach.module

/**
 * 计算一个方法运行的时间（0.1ms）
 */
fun <T> cost(name: String, block: () -> T): T {
    val start = System.currentTimeMillis()
    val r = block()
    val cost = (System.currentTimeMillis() - start) // 100000.0
    println("$name - $cost ms")
    return r
}
