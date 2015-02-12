package net.timbusproject.extractors.modules.linuxhardware.local;

import org.apache.commons.cli.Option;

/**
 * Created with IntelliJ IDEA.
 * User: jorge
 * Date: 1/7/14
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
class CLOption {

    private final Option option;
    private final String defaultValue;
    private String title;

    private CLOption(Option option) {
        this(option, null);
    }

    private CLOption(Option option, String defaultValue) {
        this.option = option;
        this.defaultValue = defaultValue;
    }

    static CLOption buildOption(Option option) {
        return new CLOption(option);
    }

    static CLOption buildOption(Option option, String argName) {
        option.setArgName(argName);
        return new CLOption(option);
    }

    static CLOption buildOption(Option option, Class type) {
        option.setType(type);
        return new CLOption(option);
    }

    static CLOption buildOption(Option option, String argName, Class type) {
        option.setArgName(argName);
        option.setType(type);
        return new CLOption(option);
    }

    static CLOption buildOption(Option option, String argName, Class type, String defaultValue) {
        option.setArgName(argName);
        option.setType(type);
        return new CLOption(option, defaultValue);
    }

    CLOption setTitle(String title) {
        this.title = title;
        return this;
    }

    String getDefaultValue() {
        return defaultValue;
    }

    String getTitle() {
        return title;
    }

    Option getOption() {
        return option;
    }












/*
    public CLOption(String opt, String description) throws IllegalArgumentException {
        super(opt, description);
    }

    public CLOption(String opt, boolean hasArg, String description) throws IllegalArgumentException {
        super(opt, hasArg, description);
    }

    public CLOption(String opt, boolean hasArg, String description, Class type) throws IllegalArgumentException {
        super(opt, hasArg, description);
        setType(type);
    }

    public CLOption(String opt, String longOpt, boolean hasArg, String description) throws IllegalArgumentException {
        super(opt, longOpt, hasArg, description);
    }

    public CLOption(String opt, String longOpt, boolean hasArg, String description, Class type) throws IllegalArgumentException {
        super(opt, longOpt, hasArg, description);
        setType(type);
    }

    public CLOption(String opt, String longOpt, boolean hasArg, String description, String argName) throws IllegalArgumentException {
        super(opt, longOpt, hasArg, description);
        setArgName(argName);
    }

    public CLOption(String opt, String longOpt, boolean hasArg, String description, String argName, Class type) throws IllegalArgumentException {
        this(opt, longOpt, hasArg, description, argName);
        setType(type);
    }

    public CLOption(String opt, String longOpt, boolean hasArg, String description, int args) throws IllegalArgumentException {
        super(opt, longOpt, hasArg, description);
        setArgs(args);
    }

    public CLOption(String opt, String longOpt, boolean hasArg, String description, int args, Class type) throws IllegalArgumentException {
        this(opt, longOpt, hasArg, description, args);
        setType(type);
    }

    public CLOption(String opt, String longOpt, boolean hasArg, String description, String argName, int args) throws IllegalArgumentException {
        this(opt, longOpt, hasArg, description, argName);
        setArgs(args);
    }

    public CLOption(String opt, String longOpt, boolean hasArg, String description, String argName, int args, Class type) throws IllegalArgumentException {
        this(opt, longOpt, hasArg, description, argName, args);
        setType(type);
    }
*/

}
