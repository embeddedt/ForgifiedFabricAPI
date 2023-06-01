/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.test.lookup.entity;

import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.test.lookup.FabricApiLookupTest;
import net.fabricmc.fabric.test.lookup.api.Inspectable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.RegistryObject;

public class FabricEntityApiLookupTest {
    public static final EntityApiLookup<Inspectable, Void> INSPECTABLE =
        EntityApiLookup.get(new ResourceLocation(FabricApiLookupTest.MOD_ID, "inspectable"), Inspectable.class, Void.class);

    public static final RegistryObject<EntityType<InspectablePigEntity>> INSPECTABLE_PIG = FabricApiLookupTest.ENTITY_TYPE_REGISTER.register("inspectable_pig",
        () -> FabricEntityTypeBuilder.create()
            .spawnGroup(MobCategory.CREATURE)
            .entityFactory(InspectablePigEntity::new)
            .dimensions(EntityDimensions.scalable(0.9F, 0.9F))
            .trackRangeChunks(10)
            .build());

    public static void onAttributesCreate(EntityAttributeCreationEvent event) {
        event.put(INSPECTABLE_PIG.get(), Pig.createAttributes().build());
    }

    public static void onInitialize() {}

    public static void runTests() {
        INSPECTABLE.registerSelf(INSPECTABLE_PIG.get());
        INSPECTABLE.registerForTypes(
            (entity, context) -> () -> Component.literal("registerForTypes: " + entity.getClass().getName()),
            EntityType.PLAYER,
            EntityType.COW);
        INSPECTABLE.registerFallback((entity, context) -> {
            if (entity instanceof Creeper) {
                return () -> Component.literal("registerFallback: CreeperEntity");
            } else {
                return null;
            }
        });
    }
}
