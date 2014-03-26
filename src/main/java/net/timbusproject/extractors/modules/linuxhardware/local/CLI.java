package net.timbusproject.extractors.modules.linuxhardware.local;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: jorge
 * Date: 1/10/14
 * Time: 9:58 AM
 * To change this template use File | Settings | File Templates.
 */
class CLI {

    private final Options options = new Options();
    private final Properties optionDefaults = new Properties();
    private final Properties optionTitles = new Properties();

    private int optionsTab = 0;
    private CommandLine cmd;

    Options addOption(CLOption... options) {
        for (CLOption option : options) {
            this.options.addOption(option.getOption());
            if (option.getDefaultValue() != null)
                optionDefaults.setProperty(getOptionKey(option.getOption()), option.getDefaultValue());
            setOptionTitle(option);
        }
        return this.options;
    }

    OptionGroup addOptionGroup(CLOption... options) {
        OptionGroup group = new OptionGroup();
        for (CLOption option : options) {
            group.addOption(option.getOption());
            if (option.getDefaultValue() != null)
                optionDefaults.setProperty(getOptionKey(option.getOption()), option.getDefaultValue());
            if (option.getTitle() != null)
                optionTitles.setProperty(getOptionKey(option.getOption()), option.getTitle());
        }
        this.options.addOptionGroup(group);
        return group;
    }

    private String getOptionKey(Option option) {
        // if 'opt' is null, then it is a 'long' option
        if (option.getOpt() == null)
            return option.getLongOpt();
        return option.getOpt();
    }

    void parse(String... args) throws ParseException {
//        cmd = new BasicParser().parse(options, args);
//        cmd = new BasicParser().parse(options, args, optionDefaults);

        BasicParser parser = new BasicParser();
        cmd = parser.parse(options, args);

    }

    boolean hasOption(String opt) {
        return cmd.hasOption(opt);
    }

    boolean hasArg(String arg){
        String[] args = cmd.getArgs();
        for(String a : args){
            if(a.equals(arg))
                return true;
        }
        return false;
    }

    String getOptionValue(String optKey) {
        return cmd.getOptionValue(optKey);
    }

    Options getOptions() {
        return options;
    }

    Option[] getParsedOptions() {
        return cmd != null ? cmd.getOptions() : null;
    }

    Hashtable<String, List<Object>> getParsedValues() throws ParseException {
        Hashtable<String, List<Object>> table = new Hashtable<>();
        for (Option option : getParsedOptions()) {
            String title = getOptionTitle(option);
            if (!table.containsKey(title))
                table.put(title, new ArrayList<>());
            table.get(title).add(getParsedValue(option));
        }
        return table;
    }

    Object getParsedValue(Option option) throws ParseException {
        return TypeHandler.createValue(option.getValue(), option.getType());
    }

    Object[] getAllParsedValues(String opt) throws ParseException {
        List<Object> list = new ArrayList<>();
        for (Option option : getParsedOptions())
            if (option.getOpt().equals(opt) || option.getLongOpt().equals(opt))
                list.add(TypeHandler.createValue(option.getValue(), option.getType()));
        return list.toArray(new Object[list.size()]);
    }

    Object getParsedValue(String opt) throws ParseException {
        return cmd.getParsedOptionValue(opt);
    }

    int getOptionsTab() {
        return optionsTab + 1;
    }

    private void setOptionTitle(CLOption option) {
        if (option.getTitle() == null)
            return;
        optionTitles.setProperty(getOptionKey(option.getOption()), option.getTitle());
        optionsTab = Math.max(optionsTab, option.getTitle().length());
    }

    String getOptionTitle(Option option) {
        return optionTitles.getProperty(getOptionKey(option));
    }

}
