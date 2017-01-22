package com.builtbroken.profiler;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
@IFMLLoadingPlugin.TransformerExclusions(value = {"com.builtbroken.profiler.asm.WorldTransformer"})
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class ProfilierCoreMod implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"com.builtbroken.profiler.asm.WorldTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "com.builtbroken.profiler.asm.ModContainer";
    }

    @Override
    public String getSetupClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        // TODO Auto-generated method stub
        return "";
    }

}
