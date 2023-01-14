package ink.ptms.adyeshach.impl.script

import taboolib.library.kether.Parser
import taboolib.module.kether.ParserHolder.option
import taboolib.module.kether.expects

fun expects(vararg s: String): Parser<String?> {
    return Parser.of { r -> r.expects(*s) }.option()
}