/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.crowsofwar.gorecore.tree;

import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

public class ArgumentList {
	
	private final Map<IArgument<?>, Object> argumentValues;
	
	public ArgumentList(String[] userInput, IArgument<?>[] arguments) {
		
		argumentValues = new HashMap<>();
		for (int i = 0; i < arguments.length; i++) {
			IArgument<?> argument = arguments[i];
			Object out = null;
			if (i < userInput.length) { // If possible, prefer user input over
										// default
				out = argument.convert(userInput[i]);
			} else { // Try to use the default value if the argument is optional
				if (argument.isOptional()) { // Argument has a default value,
												// which can be used
					out = argument.getDefaultValue();
				} else { // Argument isn't optional, but user input hasn't been
							// specified. Throw an
							// error.
					throw new TreeCommandException(Reason.ARGUMENT_MISSING, arguments[i].getArgumentName());
				}
			}
			argumentValues.put(argument, out);
		}
		
	}
	
	public <T> T get(IArgument<T> argument) {
		return (T) argumentValues.get(argument);
	}
	
	public int length() {
		return argumentValues.size();
	}
	
}
