package com.project.urlshortener.common.exception;

/**
 * An interface meant for an Exception. <br/>
 * his interface has a message key, and one or several message arguments.
 */
public interface ExceptionWithMessageKey {

    /**
     * A message key, the kind that can be used with a MessageSource.
     *
     * @return String the message key
     */
    String getMessageKey();

    /**
     * Message arguments that can be used to create a message string with a MessageSource and a Message key.
     *
     * @return String[] an array of string arguments. Default : empty arguments.
     */
    default String[] getMessageArguments() {
        return new String[0];
    }
}
