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

package net.fabricmc.fabric.mixin.client.message;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ChatListener.class)
public abstract class MessageHandlerMixin {
	@Inject(method = "showMessageToPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getChat()Lnet/minecraft/client/gui/components/ChatComponent;", ordinal = 0), cancellable = true)
	private void fabric_onSignedChatMessage(ChatType.Bound params, PlayerChatMessage message, Component decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
		fabric_onChatMessage(decorated, message, sender, params, receptionTimestamp, cir);
	}

	@Inject(method = "showMessageToPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getChat()Lnet/minecraft/client/gui/components/ChatComponent;", ordinal = 1), cancellable = true)
	private void fabric_onFilteredSignedChatMessage(ChatType.Bound params, PlayerChatMessage message, Component decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
		Component filtered = message.filterMask().applyWithFormatting(message.signedContent());

		if (filtered != null) {
			fabric_onChatMessage(params.decorate(filtered), message, sender, params, receptionTimestamp, cir);
		}
	}

	@Inject(method = "m_244709_", at = @At("HEAD"), cancellable = true)
	private void fabric_onProfilelessChatMessage(ChatType.Bound params, Component content, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
		fabric_onChatMessage(params.decorate(content), null, null, params, receptionTimestamp, cir);
	}

	@Unique
	private void fabric_onChatMessage(Component message, @Nullable PlayerChatMessage signedMessage, @Nullable GameProfile sender, ChatType.Bound params, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
		if (ClientReceiveMessageEvents.ALLOW_CHAT.invoker().allowReceiveChatMessage(message, signedMessage, sender, params, receptionTimestamp)) {
			ClientReceiveMessageEvents.CHAT.invoker().onReceiveChatMessage(message, signedMessage, sender, params, receptionTimestamp);
		} else {
			ClientReceiveMessageEvents.CHAT_CANCELED.invoker().onReceiveChatMessageCanceled(message, signedMessage, sender, params, receptionTimestamp);
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "handleSystemMessage", at = @At("HEAD"), cancellable = true)
	private void fabric_allowGameMessage(Component message, boolean overlay, CallbackInfo ci) {
		if (!ClientReceiveMessageEvents.ALLOW_GAME.invoker().allowReceiveGameMessage(message, overlay)) {
			ClientReceiveMessageEvents.GAME_CANCELED.invoker().onReceiveGameMessageCanceled(message, overlay);
			ci.cancel();
		}
	}

	@ModifyVariable(method = "handleSystemMessage", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
	private Component fabric_modifyGameMessage(Component message, Component message1, boolean overlay) {
		message = ClientReceiveMessageEvents.MODIFY_GAME.invoker().modifyReceivedGameMessage(message, overlay);
		ClientReceiveMessageEvents.GAME.invoker().onReceiveGameMessage(message, overlay);
		return message;
	}
}
