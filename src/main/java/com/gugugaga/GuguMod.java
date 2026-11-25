package com.gugugaga;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class GuguMod implements ModInitializer {
    public static final String ID = "gugugaga";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }

    public static final ResourceKey<EntityType<?>> GUGU_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, id("gugugaga"));

    public static final EntityType<GuguEntity> GUGU = Registry.register(
            BuiltInRegistries.ENTITY_TYPE, GUGU_KEY,
            EntityType.Builder.of(GuguEntity::new, MobCategory.CREATURE)
                    .sized(0.8f, 1.4f)
                    .clientTrackingRange(10)
                    .build(GUGU_KEY));

    public static final ResourceKey<Item> EGG_KEY =
            ResourceKey.create(Registries.ITEM, id("gugugaga_spawn_egg"));

    public static final Item GUGU_SPAWN_EGG = Registry.register(
            BuiltInRegistries.ITEM, EGG_KEY,
            new SpawnEggItem(new Item.Properties().setId(EGG_KEY).spawnEgg(GUGU)));

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(GUGU, GuguEntity.createAttributes());
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(output -> output.accept(GUGU_SPAWN_EGG));
    }
}
