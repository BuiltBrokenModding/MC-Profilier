package com.builtbroken.profiler.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
public class WorldTransformer implements IClassTransformer
{
    private static final String CLASS_KEY_WORLD = "net.minecraft.world.World";
    private static final String CLASS_KEY_BLOCK = "net.minecraft.block.Block";

    private static final HashMap<String, SimpleEntry<String, String>> entryMap = new HashMap<String, SimpleEntry<String, String>>();

    private boolean obf;

    public WorldTransformer()
    {
        entryMap.put(CLASS_KEY_WORLD, new SimpleEntry<String, String>("net/minecraft/world/World", "ahb"));
        entryMap.put(CLASS_KEY_BLOCK, new SimpleEntry<String, String>("net/minecraft/block/Block", "aij"));

    }

    @Override
    public byte[] transform(String name, String transformerName, byte[] bytes)
    {
        System.out.println("transform(" + name + "," + transformerName + ", bytes)");
        String changedName = name.replace('.', '/');

        if (changedName.equals(getName(CLASS_KEY_WORLD)))
        {
            System.out.println("\tTransformer editing world class");
            ClassNode cn = startInjection(bytes);
            MethodNode setBlockMethod = getMethod(cn, "setBlock", "(IIIL" + getName(CLASS_KEY_BLOCK) + ";II)Z");

            if (setBlockMethod != null)
            {

                final InsnList nodeAdd = new InsnList();
                final InsnList nodeAdd2 = new InsnList();

                nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/builtbroken/profiler/hooks/BlockHooks", "onBlockChangeStart", "", false));
                nodeAdd2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/builtbroken/profiler/hooks/BlockHooks", "onBlockChangeEnd", "", false));

                setBlockMethod.instructions.insertBefore(setBlockMethod.instructions.get(0), nodeAdd);
                setBlockMethod.instructions.insert(setBlockMethod.instructions.get(setBlockMethod.instructions.size() - 1), nodeAdd2);
            }
            return finishInjection(cn);
        }
        return bytes;
    }

    private ClassNode startInjection(byte[] bytes)
    {
        final ClassNode node = new ClassNode();
        final ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        return node;
    }

    private byte[] finishInjection(ClassNode node)
    {
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    private MethodNode getMethod(ClassNode node, String name, String sig)
    {
        for (MethodNode methodNode : node.methods)
        {
            if (methodNode.name.equals(name) && methodNode.desc.equals(sig))
            {
                return methodNode;
            }
        }
        return null;
    }

    private String getName(String key)
    {
        SimpleEntry<String, String> entry = entryMap.get(key);
        if (entry == null)
        {
            return "";
        }
        else if (obf)
        {
            return entry.getValue();
        }
        else
        {
            return entry.getKey();
        }
    }


    private String getDeobfName(String key)
    {
        SimpleEntry<String, String> entry = entryMap.get(key);
        if (entry == null)
        {
            return "";
        }
        else
        {
            return entry.getKey();
        }
    }
}
