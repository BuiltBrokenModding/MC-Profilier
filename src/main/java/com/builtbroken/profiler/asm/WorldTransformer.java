package com.builtbroken.profiler.asm;

import net.minecraft.block.Block;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
public class WorldTransformer implements IClassTransformer
{
    private static final String CLASS_KEY_WORLD = "net.minecraft.world.World";
    private static final String CLASS_KEY_BLOCK = "net.minecraft.block.Block";
    private static final String CLASS_KEY_TILE = "net.minecraft.tileentity.TileEntity";
    private static final String BLOCK_HOOK_CLASS = "com/builtbroken/profiler/hooks/BlockHooks";
    private static final String WORLD_HOOK_CLASS = "com/builtbroken/profiler/hooks/WorldHooks";

    private static final HashMap<String, SimpleEntry<String, String>> entryMap = new HashMap<String, SimpleEntry<String, String>>();

    private boolean obf;

    public WorldTransformer()
    {
        entryMap.put(CLASS_KEY_WORLD, new SimpleEntry<String, String>("net/minecraft/world/World", "ahb"));
        entryMap.put(CLASS_KEY_BLOCK, new SimpleEntry<String, String>("net/minecraft/block/Block", "aij"));
        entryMap.put(CLASS_KEY_TILE, new SimpleEntry<String, String>("net/minecraft/tileentity/TileEntity", "aor"));

    }

    @Override
    public byte[] transform(String name, String transformerName, byte[] bytes)
    {
        String changedName = name.replace('.', '/');
        if (changedName.equals(getName(CLASS_KEY_WORLD)))
        {
            ClassNode cn = ASMUtility.startInjection("profiler", bytes);
            injectSetBlock(cn);
            injectSetBlockWithMeta(cn);
            injectUpdateEntities(cn);
            return ASMUtility.finishInjection("profiler", cn);
        }
        return bytes;
    }

    /** {@link World#setBlock(int, int, int, Block, int, int)} */
    private void injectSetBlock(ClassNode cn)
    {
        MethodNode setBlockMethod = ASMUtility.getMethod(cn, "setBlock", "func_147465_d", "(IIIL" + getName(CLASS_KEY_BLOCK) + ";II)Z");

        if (setBlockMethod != null)
        {
            //Create method call
            final InsnList nodeAdd = new InsnList();

            nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
            nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 1));
            nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 2));
            nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 3));
            nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, BLOCK_HOOK_CLASS, "onBlockChange", "(L" + getName(CLASS_KEY_WORLD) + ";III)V", false));

            //Inject method call at top of method
            setBlockMethod.instructions.insertBefore(setBlockMethod.instructions.get(0), nodeAdd);

            //Locate all return points from the method
            List<AbstractInsnNode> returnNodes = new ArrayList();
            for (int i = 0; i < setBlockMethod.instructions.size(); i++)
            {
                AbstractInsnNode ain = setBlockMethod.instructions.get(i);
                if (ain.getOpcode() == Opcodes.IRETURN)
                {
                    returnNodes.add(ain);
                }
            }

            //Inject calls in front of return points
            for (AbstractInsnNode node : returnNodes)
            {
                //Create method call
                final InsnList nodeAdd2 = new InsnList();
                nodeAdd2.add(new VarInsnNode(Opcodes.ALOAD, 0));
                nodeAdd2.add(new VarInsnNode(Opcodes.ILOAD, 1));
                nodeAdd2.add(new VarInsnNode(Opcodes.ILOAD, 2));
                nodeAdd2.add(new VarInsnNode(Opcodes.ILOAD, 3));
                nodeAdd2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, BLOCK_HOOK_CLASS, "onPostBlockChange", "(L" + getName(CLASS_KEY_WORLD) + ";III)V", false));
                //Inject method call before return node
                setBlockMethod.instructions.insertBefore(node, nodeAdd2);
            }
        }
    }

    /** {@link World#setBlockMetadataWithNotify(int, int, int, int, int)} */
    private void injectSetBlockWithMeta(ClassNode cn)
    {
        MethodNode setBlockMetaMethod = ASMUtility.getMethod(cn, "setBlockMetadataWithNotify", "func_72921_c", "(IIIII)Z");

        if (setBlockMetaMethod != null)
        {
            final InsnList nodeAdd = new InsnList();

            nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0)); //this
            nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 1)); //x
            nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 2)); //y
            nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 3)); //z
            nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, BLOCK_HOOK_CLASS, "onBlockMetaChange", "(L" + getName(CLASS_KEY_WORLD) + ";III)V", false));

            setBlockMetaMethod.instructions.insertBefore(setBlockMetaMethod.instructions.get(0), nodeAdd);

            //Locate all return points from the method
            List<AbstractInsnNode> returnNodes = new ArrayList();
            for (int i = 0; i < setBlockMetaMethod.instructions.size(); i++)
            {
                AbstractInsnNode ain = setBlockMetaMethod.instructions.get(i);
                if (ain.getOpcode() == Opcodes.IRETURN)
                {
                    returnNodes.add(ain);
                }
            }

            //Inject calls in front of return points
            for (AbstractInsnNode node : returnNodes)
            {
                //Create method call
                final InsnList nodeAdd2 = new InsnList();
                nodeAdd2.add(new VarInsnNode(Opcodes.ALOAD, 0));
                nodeAdd2.add(new VarInsnNode(Opcodes.ILOAD, 1));
                nodeAdd2.add(new VarInsnNode(Opcodes.ILOAD, 2));
                nodeAdd2.add(new VarInsnNode(Opcodes.ILOAD, 3));
                nodeAdd2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, BLOCK_HOOK_CLASS, "onPostBlockMetaChange", "(L" + getName(CLASS_KEY_WORLD) + ";III)V", false));
                //Inject method call before return node
                setBlockMetaMethod.instructions.insertBefore(node, nodeAdd2);
            }
        }
    }

    /** {@link World#updateEntities()} */
    private void injectUpdateEntities(ClassNode cn)
    {
        MethodNode updateMethod = ASMUtility.getMethod(cn, "updateEntities", "func_72939_s", "()V");

        if (updateMethod != null)
        {
            int tileVarIndex = ASMUtility.getVarIndex(updateMethod, "tileentity");

            final InsnList nodeAdd = new InsnList();
            nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0)); //this
            nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, tileVarIndex));
            nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, WORLD_HOOK_CLASS, "onUpdateEntity", "(L" + getName(CLASS_KEY_WORLD) + ";L" + getName(CLASS_KEY_TILE) + ";)V", false));

            final InsnList nodeAdd2 = new InsnList();
            nodeAdd2.add(new VarInsnNode(Opcodes.ALOAD, 0)); //this
            nodeAdd2.add(new VarInsnNode(Opcodes.ALOAD, tileVarIndex));
            nodeAdd2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, WORLD_HOOK_CLASS, "onPostUpdateEntity", "(L" + getName(CLASS_KEY_WORLD) + ";L" + getName(CLASS_KEY_TILE) + ";)V", false));

            ListIterator<AbstractInsnNode> it = updateMethod.instructions.iterator();
            MethodInsnNode updateEntityCall = null;
            while (it.hasNext())
            {
                AbstractInsnNode node = it.next();
                if (node instanceof MethodInsnNode)
                {
                    //tileentity.updateEntity();
                    if (((MethodInsnNode) node).name.equals("updateEntity") && ((MethodInsnNode) node).owner.contains("TileEntity"))
                    {
                        updateEntityCall = (MethodInsnNode) node;
                        break;
                    }
                }
            }
            if (updateEntityCall != null)
            {
                //Inject replacement
                updateMethod.instructions.insertBefore(updateEntityCall, nodeAdd);
                updateMethod.instructions.insertBefore(updateEntityCall.getNext(), nodeAdd2);
            }
        }
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
