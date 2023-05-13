/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.renderer.task.RegionProcessor;
import org.jetbrains.annotations.NotNull;

public class PauseCommand extends Pl3xMapCommand {
    public PauseCommand(@NotNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("pause")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_PAUSE_DESCRIPTION))
                .permission("pl3xmap.command.pause")
                .handler(this::execute));
    }

    private void execute(@NotNull CommandContext<@NotNull Sender> context) {
        Sender sender = context.getSender();

        RegionProcessor processor = Pl3xMap.api().getRegionProcessor();

        boolean paused = !processor.isPaused();

        processor.setPaused(paused);

        if (paused) {
            sender.sendMessage(Lang.COMMAND_PAUSE_PAUSED);
        } else {
            sender.sendMessage(Lang.COMMAND_PAUSE_UNPAUSED);
        }
    }
}
