package co.uk.mommyheather.tsb.coremod;

import static org.objectweb.asm.Opcodes.ASM4;

import java.util.HashMap;
import java.util.function.BiFunction;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.minecraft.launchwrapper.IClassTransformer;


public class TSBTransformer implements IClassTransformer {
    
    private static final HashMap<String, BiFunction<Integer, ClassVisitor, ClassVisitor>> TRANSFORMERS = new HashMap<>();
    
    static {
        TRANSFORMERS.put("slimeknights.tconstruct.library.utils.ToolHelper", ToolHelperTransformer::new);
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (TRANSFORMERS.containsKey(name)) {
            System.out.println("Transforming class : " + name);
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            reader.accept(TRANSFORMERS.get(name).apply(ASM4, writer), 0);
            
            return writer.toByteArray();
        }
        return bytes;
    }
    
    
    /*
    * Transformer - run an extra check when adding a block to an AOE tool block list.
    * Our custom method takes in the boolean - this is way easier than adding a second if statement.
    */
    
    public static class ToolHelperTransformer extends ClassVisitor{
        
        public ToolHelperTransformer(int api, ClassVisitor cv) {
            super(api, cv);
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            System.out.println(name);
            if (!name.equals("calcAOEBlocks")) return super.visitMethod(access, name, desc, signature, exceptions);
            return new CalcAOEBlocksMethodVisitor(ASM4, super.visitMethod(access, name, desc, signature, exceptions));
        }
        
        private static class CalcAOEBlocksMethodVisitor extends MethodVisitor {
            
            
            public CalcAOEBlocksMethodVisitor(int api, MethodVisitor mv) {
                super(api, mv);
                System.out.println("Transforming method calcAOEBlocks");
                
            }
            
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                
                super.visitMethodInsn(opcode, owner, name, desc, itf);

                if (opcode == Opcodes.INVOKESTATIC && name.equals("isToolEffective2")) {
                    super.visitVarInsn(Opcodes.ALOAD, 2);
                    super.visitMethodInsn(opcode, "co/uk/mommyheather/tsb/util/AOEToolHelper", "isPlayerNotCrouching", "(ZLnet/minecraft/entity/player/EntityPlayer;)Z", itf);
                }
            }
        }
        
    }
    
    
    
}

