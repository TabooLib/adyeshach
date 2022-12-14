package ink.ptms.adyeshach.impl.bytecode

import ink.ptms.adyeshach.core.entity.EntityBase
import ink.ptms.adyeshach.core.entity.EntityTypes
import taboolib.library.asm.ClassWriter
import taboolib.library.asm.Opcodes

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.bytecode.SimpleEntityGenerator
 *
 * @author 坏黑
 * @since 2022/6/19 17:10
 */
class SimpleEntityGenerator : EntityGenerator, Opcodes {

    val set = "L${EntityTypes::class.java.name.replace('.', '/')};"
    val seb = "L${EntityBase::class.java.name.replace('.', '/')};"

    override fun generate(className: String, baseClass: String, interfaces: List<String>): ByteArray {
        val classWriter = ClassWriter(0)
        val ifs = interfaces.map { it.replace('.', '/') }.toTypedArray()
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER, className.replace('.', '/'), null, baseClass.replace(',', '/'), ifs)
        classWriter.visitSource("$className.java", null)

        // 构造方法
        var methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "($set)V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, baseClass.replace(',', '/'), "<init>", "($set)V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()

        // 继承方法
        methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "createEmpty", "()$seb", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitTypeInsn(Opcodes.NEW, className.replace('.', '/'))
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className.replace('.', '/'), "getEntityType", "()$set", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, className.replace('.', '/'), "<init>", "($set)V", false)
        methodVisitor.visitInsn(Opcodes.ARETURN)
        methodVisitor.visitMaxs(3, 1)
        methodVisitor.visitEnd()
        
        classWriter.visitEnd()
        return classWriter.toByteArray()
    }
}