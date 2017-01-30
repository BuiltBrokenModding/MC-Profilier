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
                    file.getParentFile().mkdirs();
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
     * @param node - class node
     * @param name - name of the method
     * @param desc - method description (Params)
     * @return method if found
     */
    public static MethodNode getMethod(ClassNode node, String name, String desc)
    {
        for (MethodNode methodNode : node.methods)
        {
            if (doesMethodsMatch(name, desc, node.name, methodNode, node.name))
            {
                return methodNode;
            }
        }
        if (ProfilerCoreMod.isDevMode())
        {
            ProfilerCoreMod.logger.error("ASMUtility.getMethod(" + node.name + ", " + name + ", " + desc + ") Failed to find method");
        }
        return null;
    }

    /**
     * Checks if a method node matches the given name and description provided.
     * <p>
     * Will attempt to remap the method if obfuscated
     * <p>
     * If in development mode this method will output debug information to check if the method
     * partially matched expected results. Just in case the description was incorrectly entered
     * or has changed.
     *
     * @param name  - name of the method
     * @param desc  - method description (Params)
     * @param owner - owner of the method
     * @param node  - method to check
     * @return true if it matches
     */
    public static boolean doesMethodsMatch(String name, String desc, String owner, MethodNode node, String nodeOwner)
    {
        return doesMethodsMatch(name, desc, owner, node.name, node.desc, nodeOwner);
    }

    /**
     * Checks if a method node matches the given name and description provided.
     * <p>
     * Will attempt to remap the method if obfuscated
     * <p>
     * If in development mode this method will output debug information to check if the method
     * partially matched expected results. Just in case the description was incorrectly entered
     * or has changed.
     *
     * @param name  - name of the method
     * @param desc  - method description (Params)
     * @param owner - owner of the method
     * @param node  - method to check
     * @return true if it matches
     */
    public static boolean doesMethodsMatch(String name, String desc, String owner, MethodInsnNode node)
    {
        return doesMethodsMatch(name, desc, owner, node.name, node.desc, node.owner);
    }

    /**
     * Checks if a method node matches the given name and description provided.
     * <p>
     * Will attempt to remap the method if obfuscated
     * <p>
     * If in development mode this method will output debug information to check if the method
     * partially matched expected results. Just in case the description was incorrectly entered
     * or has changed.
     *
     * @param name       - name of the method
     * @param desc       - method description (Params)
     * @param owner      - owner of the method
     * @param checkOwner - class the method belongs to
     * @param checkName  - name of the method to check
     * @param checkDesc  - method description (Params)
     * @return true if it matches
     */
    public static boolean doesMethodsMatch(String name, String desc, String owner, String checkName, String checkDesc, String checkOwner)
    {
        //Check if the method node matches directly first (Only works in dev workspace)
        if (checkName.equals(name) && (desc == null || checkDesc.equals(desc)))
        {
            return owner == null || owner.equals(checkOwner);
        }
        //If not then use the ObfMappings as we most likely are on a live copy of Minecraft
        else
        {
            ObfMapping mapping = new ObfMapping(checkOwner, checkName, checkDesc).toRuntime();
            //Check if remapped name or ordinal name matches our expected name
            if (checkName.equals(name) || mapping.s_name.equals(name))
            {
                //Check if remapped description or orginal description match
                if (desc == null || mapping.s_desc.equals(desc) || checkDesc.equals(desc))
                {
                    return owner == null || owner.equals(checkOwner) || owner.equals(mapping.s_owner);
                }
                //Development debug to check if method signature changed or is incorrect
                if (ProfilerCoreMod.isDevMode())
                {
                    ProfilerCoreMod.logger.info("------------------------------------------------------------------------");
                    ProfilerCoreMod.logger.info("ASMUtility.getMethod(" + owner + ", " + name + ", " + desc + ") Found method by same name");
                    ProfilerCoreMod.logger.info("\tMethod: " + checkName + checkDesc + "  Owner: " + checkOwner);
                    ProfilerCoreMod.logger.info("\tMapped: " + mapping.s_name + mapping.s_desc + "  Owner: " + mapping.s_owner);
                    ProfilerCoreMod.logger.info("------------------------------------------------------------------------");
                }
            }
        }
        return false;
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
