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

package net.fabricmc.fabric.mixin.command;

import net.fabricmc.fabric.api.command.v2.FabricEntitySelectorReader;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(EntitySelectorParser.class)
public class EntitySelectorReaderMixin implements FabricEntitySelectorReader {
	@Unique
	private final Set<ResourceLocation> flags = new HashSet<>();

	@Override
	public void setCustomFlag(ResourceLocation key, boolean value) {
		if (value) {
			this.flags.add(key);
		} else {
			this.flags.remove(key);
		}
	}

	@Override
	public boolean getCustomFlag(ResourceLocation key) {
		return this.flags.contains(key);
	}
}
