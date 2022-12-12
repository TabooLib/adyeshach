package ink.ptms.adyeshach.impl.bytecode

import taboolib.library.asm.ClassWriter
import taboolib.library.asm.Label
import taboolib.library.asm.MethodVisitor
import taboolib.library.asm.Opcodes

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.bytecode.SimpleEntityGenerator
 *
 * @author 坏黑
 * @since 2022/6/19 17:10
 */
class SimpleEntityGenerator : EntityGenerator, Opcodes {

    override fun generate(className: String, baseClass: String, interfaces: List<String>): ByteArray {
        val classWriter = ClassWriter(0)
        val ifs = interfaces.map { it.replace('.', '/') }.toTypedArray()
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER, className.replace('.', '/'), null, baseClass.replace(',', '/'), ifs)
        classWriter.visitSource("$className.java", null)
        val methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Link/ptms/adyeshach/common/entity/EntityTypes;)V", null, null)
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitLineNumber(15, label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, baseClass.replace(',', '/'), "<init>", "(Link/ptms/adyeshach/common/entity/EntityTypes;)V", false)
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(16, label1)
        methodVisitor.visitInsn(Opcodes.RETURN)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLocalVariable("this", "L${className.replace('.', '/')};", null, label0, label2, 0)
        methodVisitor.visitLocalVariable("entityType", "Link/ptms/adyeshach/common/entity/EntityTypes;", null, label0, label2, 1)
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()
        classWriter.visitEnd()
        return classWriter.toByteArray()
    }
}