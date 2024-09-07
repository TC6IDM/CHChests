package com.example.CHChests.command;

import com.example.CHChests.CHChests;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in CHChests.java with `CommandManager.INSTANCE.registerCommand(new ExampleCommand());`
 *
 * @see Command
 * @see Main
 * @see CHChests
 */
@Command(value = CHChests.MODID, description = "Access the " + CHChests.NAME + " GUI.")
public class ExampleCommand {
    @Main
    private void handle() {
        CHChests.INSTANCE.config.openGui();
    }
}