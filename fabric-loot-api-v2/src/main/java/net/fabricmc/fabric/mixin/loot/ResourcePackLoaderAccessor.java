package net.fabricmc.fabric.mixin.loot;

import java.util.Map;

import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.ResourcePackLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resource.ResourcePack;

@Mixin(ResourcePackLoader.class)
public interface ResourcePackLoaderAccessor {
    @Accessor(remap = false)
    static Map<IModFile, ResourcePack> getModResourcePacks() {
        throw new UnsupportedOperationException();
    }
}