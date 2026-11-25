package com.gugugaga;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class GuguSounds {
    private GuguSounds() {}
    public static final SoundEvent AMBIENT = register("entity.gugugaga.ambient");
    public static final SoundEvent HURT = register("entity.gugugaga.hurt");
    public static final SoundEvent DEATH = register("entity.gugugaga.death");
    public static final SoundEvent FEED = register("entity.gugugaga.feed");

    private static SoundEvent register(String path) {
        var id = GuguMod.id(path);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
}
