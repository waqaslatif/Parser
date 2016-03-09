package com.stella.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

/**
 * XmlSanitizingVariableResolver sanitize the user input according to provided regEx values.
 * 
 * @author Tamjeed
 *
 */
public class XmlSanitizingVariableResolver implements XPathVariableResolver {
	// create a map to contain the variable values
	private final Map<QName, String> variables = new HashMap<>();
	// keep a list of all valid patterns
	private final List<Pattern> validationPatterns;

	// constructor accepting regular expression patterns
	public XmlSanitizingVariableResolver(final String... regexPatterns) {
		this.validationPatterns = new ArrayList<>();
		for (final String regexPattern : regexPatterns) {
			this.validationPatterns.add(Pattern.compile(regexPattern));
		}
	}

	/**
	 * method to add variable value on which the sanity check is applied
	 * 
	 * @param name
	 * @param value
	 */
	public void addVariable(final String name, final String value) {
		for (final Pattern pattern : validationPatterns) {
			if (pattern.matcher(value).matches()) {
				variables.put(new QName(name), value);
				return;
			}
		}
		// don't accept invalid values
		throw new IllegalArgumentException("The value '" + value + "' is not allowed for a variable");
	}

	@Override
	public Object resolveVariable(final QName variableName) {
		return this.variables.get(variableName);
	}

}
