package ink.ptms.adyeshach.common.script.action;

import io.izzel.kether.common.api.KetherCompleters;
import io.izzel.kether.common.api.QuestAction;
import io.izzel.kether.common.api.QuestActionParser;
import io.izzel.kether.common.api.QuestContext;

import java.util.concurrent.CompletableFuture;

public class ActionCall<CTX extends QuestContext> implements QuestAction<Void, CTX> {

    private final String block;

    public ActionCall(String block) {
        this.block = block;
    }

    public boolean isAsync() {
        return true;
    }

    public boolean isPersist() {
        return true;
    }

    public CompletableFuture<Void> process(CTX context) {
        QuestContext child = context.createChild(this.block, false);
        child.setJump(this.block, 1);
        return child.runActions();
    }

    public String getDataPrefix() {
        return "call_" + this.block;
    }

    public String toString() {
        return "CallAction{block='" + this.block + '\'' + '}';
    }

    public static <C extends QuestContext> QuestActionParser parser() {
        return QuestActionParser.of((resolver) -> new ActionCall<>(resolver.nextElement()), KetherCompleters.consume());
    }
}
