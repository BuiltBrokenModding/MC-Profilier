package com.builtbroken.profiler.utils.plot;

import com.builtbroken.profiler.utils.Pos;
import net.minecraft.tileentity.TileEntity;

import java.lang.ref.WeakReference;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/15/2016.
 */
public class PlotTile extends PlotPos
{
    private WeakReference<TileEntity> tileEntityWeakReference;

    public PlotTile(String name, TileEntity tile)
    {
        super(name, new Pos(tile.xCoord, tile.yCoord, tile.zCoord));
        this.tileEntityWeakReference = new WeakReference<TileEntity>(tile);
    }
}
