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
package net.pl3x.map.core.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import java.util.List;
import java.util.function.UnaryOperator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.pl3x.map.core.command.commands.ConfirmCommand;
import net.pl3x.map.core.command.commands.HelpCommand;
import net.pl3x.map.core.command.commands.HideCommand;
import net.pl3x.map.core.command.commands.ReloadCommand;
import net.pl3x.map.core.command.commands.ResetMapCommand;
import net.pl3x.map.core.command.commands.ShowCommand;
import net.pl3x.map.core.command.commands.StatusCommand;
import net.pl3x.map.core.command.commands.StitchCommand;
import net.pl3x.map.core.command.commands.VersionCommand;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents the command handler.
 */
public interface CommandHandler {
    /**
     * Get the command manager.
     *
     * @return command manager
     */
    @NonNull CommandManager<@NonNull Sender> getManager();

    /**
     * Get the root command.
     *
     * @return the root command
     */
    Command.@NonNull Builder<@NonNull Sender> getRoot();

    default void setupExceptionHandlers() {
        new MinecraftExceptionHandler<Sender>()
                .withDefaultHandlers()
                .withDecorator(component -> Component.text()
                        .append(Lang.parse(Lang.PREFIX_COMMAND)
                                .hoverEvent(Lang.parse(Lang.CLICK_FOR_HELP))
                                .clickEvent(ClickEvent.runCommand("/map help")))
                        .append(component)
                        .build())
                .apply(getManager(), AudienceProvider.nativeAudience());
    }

    /**
     * Register a new subcommand.
     *
     * @param builder command builder
     */
    default void registerSubcommand(@NonNull UnaryOperator<Command.@NonNull Builder<@NonNull Sender>> builder) {
        this.getManager().command(builder.apply(getRoot()));
    }

    default Command.@NonNull Builder<@NonNull Sender> buildRoot() {
        return getManager().commandBuilder("map", "pl3xmap")
                .permission("pl3xmap.command.map")
                .meta(CommandMeta.DESCRIPTION, "Pl3xMap command. '/map help'")
                .handler(context -> context.getSender().sendMessage(Lang.COMMAND_BASE));
    }

    default void registerSubcommands() {
        List.of(
                new ConfirmCommand(this),
                new HelpCommand(this),
                new HideCommand(this),
                new ReloadCommand(this),
                new ResetMapCommand(this),
                new ShowCommand(this),
                new StatusCommand(this),
                new StitchCommand(this),
                new VersionCommand(this)
        ).forEach(Pl3xMapCommand::register);
    }
}
