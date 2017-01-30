package com.builtbroken.profiler.asm;

import com.builtbroken.profiler.ProfilerCoreMod;
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

    public WorldTransformer()
    {
        entryMap.put(CLASS_KEY_WORLD, new SimpleEntry<String, String>("net/minecraft/world/World", "ahb"));
        entryMap.put(CLASS_KEY_BLOCK, new SimpleEntry<String, String>("net/minecraft/block/Block", "aij"));
        entryMap.put(CLASS_KEY_TILE, new SimpleEntry<String, String>("net/minecraft/tileentity/TileEntity", "aor"));
    }

    @Override
    public byte[] transform(String name, String transformerName, byte[] bytes)
    {
        //debug("WorldTransformer >> Name: " + name + "   TransformerName: " + transformerName);
        String changedName = name.replace('.', '/');
        if (changedName.equals(getName(CLASS_KEY_WORLD)))
        {
            debug("Found world class file");
            ClassNode cn = ASMUtility.startInjection("profiler", bytes);


            ProfilerCoreMod.logger.error("--==xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            debug("Injecting set block hook");
            injectSetBlock(cn);

            ProfilerCoreMod.logger.error("--==xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            debug("Injecting set meta hook");
            injectSetBlockWithMeta(cn);

            ProfilerCoreMod.logger.error("--==xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            debug("Injecting update tile hook");
            injectUpdateEntities(cn);

            ProfilerCoreMod.logger.error("--==xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

            return ASMUtility.finishInjection("profiler", cn);
        }
        return bytes;
    }

    /** {@link World#setBlock(int, int, int, Block, int, int)} */
    private void injectSetBlock(ClassNode cn)
    {
        MethodNode setBlockMethod;
        if (ProfilerCoreMod.obfuscated)
        {
            setBlockMethod = ASMUtility.getMethod(cn, "d", "(IIIL" + entryMap.get(CLASS_KEY_BLOCK).getValue() + ";II)Z");
        }
        else
        {
            setBlockMethod = ASMUtility.getMethod(cn, "setBlock", "(IIIL" + entryMap.get(CLASS_KEY_BLOCK).getKey() + ";II)Z");
        }
        if (setBlockMethod == null)
        {
            debug("Failed to locate World#setBlock(), moving to backup name");
            setBlockMethod = ASMUtility.getMethod(cn, "func_147465_d", "(IIIL" + entryMap.get(CLASS_KEY_BLOCK).getKey() + ";II)Z");
        }

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
            ProfilerCoreMod.blockChangeHookAdded = true;
        }

        else
        {
            ProfilerCoreMod.logger.error("Failed to find setBlock method");
        }
    }

    /** {@link World#setBlockMetadataWithNotify(int, int, int, int, int)} */
    private void injectSetBlockWithMeta(ClassNode cn)
    {
        MethodNode setBlockMetaMethod = ASMUtility.getMethod(cn, "setBlockMetadataWithNotify", "(IIIII)Z");
        if (setBlockMetaMethod == null)
        {
            debug("Failed to locate World#setBlockMetadataWithNotify(), moving to backup name");
            setBlockMetaMethod = ASMUtility.getMethod(cn, "func_72921_c", "(IIIII)Z");
        }

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
            ProfilerCoreMod.blockChangeMetaHookAdded = true;
        }
        else
        {
            ProfilerCoreMod.logger.error("Failed to find setBlockMetadataWithNotify method");
        }
    }

    /** {@link World#updateEntities()} */
    private void injectUpdateEntities(ClassNode cn)
    {
        MethodNode updateMethod = ASMUtility.getMethod(cn, "updateEntities", "()V");
        if (updateMethod == null)
        {
            debug("Failed to locate World#updateEntities(), moving to backup name");
            updateMethod = ASMUtility.getMethod(cn, "func_72939_s", "()V");
        }

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
                    //we are looking for (tileentity.updateEntity();) inside of a for loop in the World#updateEntities() method
                    if (ASMUtility.doesMethodsMatch("updateEntity", null, entryMap.get(CLASS_KEY_TILE).getKey(), (MethodInsnNode) node))
                    {
                        updateEntityCall = (MethodInsnNode) node;
                        break;
                    }
                    //func_145845_h
                    if (ASMUtility.doesMethodsMatch("func_145845_h", null, entryMap.get(CLASS_KEY_TILE).getKey(), (MethodInsnNode) node))
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
                ProfilerCoreMod.tileUpdateHookAdded = true;
            }
            else
            {
                ProfilerCoreMod.logger.error("Failed to find tileEntity.updateEntity() call in world#updateEntities() method");
            }
        }
        else
        {
            ProfilerCoreMod.logger.error("Failed to find updateEntities method");
        }
    }

    private void debug(String msg)
    {
        if (ProfilerCoreMod.isDevMode())
        {
            ProfilerCoreMod.logger.info(msg);
        }
    }

    private String getName(String key)
    {
        SimpleEntry<String, String> entry = entryMap.get(key);
        if (entry == null)
        {
            return "";
        }
        else if (ProfilerCoreMod.obfuscated)
        {
            return entry.getValue();
        }
        else
        {
            return entry.getKey();
        }
    }
}
