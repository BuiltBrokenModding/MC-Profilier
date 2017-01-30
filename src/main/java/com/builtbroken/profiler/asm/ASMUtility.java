package com.builtbroken.profiler.asm;

import com.builtbroken.profiler.ProfilerCoreMod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ListIterator;

/**
 * Contains reusable methods for dealing with ASM
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/11/2016.
 */
public final class ASMUtility
{
    /**
     * Gets the index contained in the var inside of the method object
     *
     * @param method
     * @param varName
     * @return
     */
    public static int getVarIndex(MethodNode method, String varName)
    {
        for (LocalVariableNode var : method.localVariables)
        {
            if (var.name.equals(varName))
            {
                return var.index;
            }
        }
        System.out.println("Local variables for method " + method.name);
        method.localVariables.stream().forEach(v -> System.out.println("\t" + v.name));
        throw new RuntimeException("Failed to find var " + varName);
    }

    /**
     * Gets the index in the var list from the method
     *
     * @param method
     * @param varName
     * @return
     */
    public static int getVarIndex2(MethodNode method, String varName)
    {
        for (int i = 0; i < method.localVariables.size(); i++)
        {
            LocalVariableNode var = method.localVariables.get(i);
            if (var.name.equals(varName))
            {
                return i;
            }
        }
        System.out.println("Local variables for method " + method.name);
        method.localVariables.stream().forEach(v -> System.out.println("\t" + v.name));
        throw new RuntimeException("Failed to find var " + varName);
    }

    /**
     * Gets the parameter index in the list of parameters
     *
     * @param method
     * @param varName
     * @return
     */
    public static int getParamIndex(MethodNode method, String varName)
    {
        for (int i = 0; i < method.parameters.size(); i++)
        {
            ParameterNode var = method.parameters.get(i);
            if (var.name.equals(varName))
            {
                return i;
            }
        }
        System.out.println("Parameters for method " + method.name);
        method.localVariables.stream().forEach(v -> System.out.println("\t" + v.name));
        throw new RuntimeException("Failed to find param " + varName);
    }

    /**
     * Starts the injection process of editing byte code
     *
     * @param bytes
     * @return
     */
    public static ClassNode startInjection(String name, byte[] bytes)
    {
        ProfilerCoreMod.logger.info("Starting injection process for " + name);
        final ClassNode node = new ClassNode();
        final ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);
        return node;
    }

    /**
     * Finishes the injection process of editing byte code
     *
     * @param node
     * @return
     */
    public static byte[] finishInjection(String name, ClassNode node)
    {
        ProfilerCoreMod.logger.info("Ending injection process for " + name);
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        byte[] data = writer.toByteArray();
        _doDebug(node, data);
        return data;
    }

    /**
     * Handles debug information at the end of every injection process
     *
     * @param node
     * @param data
     */
    public static void _doDebug(ClassNode node, byte[] data)
    {
        if (ProfilerCoreMod.isDevMode())
        {
            try
            {
                String cl = node.name.replace(File.separator, ".").replace("/", ".").trim();
                File file = new File("./asmTestFolder/" + cl + ".class");
                if (!file.getParentFile().exists())
                {
                    file.mkdirs();
                }
                System.out.println("Writing ASM class[" + cl + "] to " + file);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the method with the name and sig
     *
     * @param node
     * @param name
     * @param sig
     * @return
     */
    public static MethodNode getMethod(ClassNode node, String name, String name2, String sig)
    {
        for (MethodNode methodNode : node.methods)
        {
            ObfMapping mapping = new ObfMapping(node.name, methodNode.name, methodNode.desc).toRuntime();
            if ((mapping.s_name.equals(name) || mapping.s_name.equals(name2)) && (sig == null || mapping.s_desc.equals(sig) || methodNode.desc.equals(sig)))
            {
                return methodNode;
            }
        }
        return null;
    }

    public static MethodNode getMethod(ClassNode node, String name, String name2)
    {
        return getMethod(node, name, name2, null);
    }

    /**
     * Gets the {@link MethodInsnNode} with the given name, does not
     * check the sig of the method
     * <p>
     * Will throw an error if the method call is not found
     *
     * @param method - method inside of the class
     * @param name   - name of the method call
     * @return
     */
    public static MethodInsnNode getMethodeNode(MethodNode method, String name, String name2)
    {
        ListIterator<AbstractInsnNode> it = method.instructions.iterator();
        while (it.hasNext())
        {
            AbstractInsnNode next = it.next();

            if (next instanceof MethodInsnNode)
            {
                MethodInsnNode mnode = (MethodInsnNode) next;
                ObfMapping mapping = new ObfMapping(mnode.owner, mnode.name, mnode.desc).toRuntime();
                if (mapping.s_name.equals(name) || mapping.s_name.equals(name2))
                {
                    return (MethodInsnNode) next;
                }
            }
        }
        System.out.println("Instructions for method " + method.name);
        printMethod(method);
        throw new RuntimeException("Failed to find method " + name);
    }

    /**
     * Prints out all the info about a method
     *
     * @param methodNode - method
     */
    public static void printMethod(MethodNode methodNode)
    {
        //TODO print rest of information
        ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator();
        while (it.hasNext())
        {
            AbstractInsnNode next = it.next();
            if (next instanceof MethodInsnNode)
            {
                System.out.println("MethodInsnNode( " + next.getOpcode() + ", " + ((MethodInsnNode) next).name + ((MethodInsnNode) next).desc + " )");
            }
            else if (next instanceof VarInsnNode)
            {
                System.out.println("VarInsnNode( " + next.getOpcode() + ", " + ((VarInsnNode) next).var + " )");
            }
            else if (next instanceof LabelNode)
            {
                System.out.println("LabelNode( " + ((LabelNode) next).getLabel() + " )");
            }
            else if (next instanceof LineNumberNode)
            {
                System.out.println("LineNumberNode( " + ((LineNumberNode) next).line + " )");
            }
            else
            {
                System.out.println(next);
            }
        }
    }
}
