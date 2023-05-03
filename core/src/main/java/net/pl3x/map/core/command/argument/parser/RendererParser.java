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
package net.pl3x.map.core.command.argument.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.exception.RendererParseException;
import net.pl3x.map.core.registry.RendererRegistry;
import net.pl3x.map.core.renderer.Renderer;
import org.checkerframework.checker.nullness.qual.NonNull;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

/**
 * Parser that parses strings into {@link Renderer.Builder}s.
 *
 * @param <C> command sender type
 */
public class RendererParser<@NonNull C> implements ArgumentParser<@NonNull C, Renderer.@NonNull Builder> {
    @Override
    public @NonNull ArgumentParseResult<Renderer.@NonNull Builder> parse(@NonNull CommandContext<@NonNull C> context, @NonNull Queue<@NonNull String> queue) {
        String input = queue.peek();
        if (input == null) {
            return failure(new RendererParseException(null, RendererParseException.MUST_SPECIFY_RENDERER));
        }

        Renderer.Builder builder = Pl3xMap.api().getRendererRegistry().get(input);
        if (builder == null) {
            return failure(new RendererParseException(input, RendererParseException.NO_SUCH_RENDERER));
        }

        queue.remove();
        return success(builder);
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<@NonNull C> commandContext, @NonNull String input) {
        return Pl3xMap.api().getRendererRegistry()
                .values().stream()
                .map(Renderer.Builder::getKey)
                .filter(key -> !key.equals(RendererRegistry.BLOCKINFO))
                .collect(Collectors.toList());
    }
}
