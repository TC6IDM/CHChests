package com.example.CHChests;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CommandCHCLocateClient extends CommandBase {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean conditionMet = false;
    private String targetServer;
    private String serverName;

    @Override
    public String getCommandName() {
        return "chc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chc <locate/stop> [server]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 1 && args[0].equalsIgnoreCase("locate")) {
            targetServer = args[1]; // Store the server name provided by the user
            conditionMet = false; // Reset the condition
            ChatComponentText chatMessage = new ChatComponentText("Locating Server: " + targetServer);
            mc.thePlayer.addChatMessage(chatMessage);
            executeCommandSequence(sender);
        } else if (args[0].equalsIgnoreCase("stop")) {
            ChatComponentText chatMessage = new ChatComponentText("Stopping");
            mc.thePlayer.addChatMessage(chatMessage);
            stopLoop();
        }
    }

    private void executeCommandSequence(final ICommandSender sender) {

        // Start the sequence with /warp ch
        ChatComponentText chatMessage = new ChatComponentText("Warping to CH");
        mc.thePlayer.addChatMessage(chatMessage);
        mc.thePlayer.sendChatMessage("/warp ch");

        // Create a timer to handle delays
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if the condition is met after 5 seconds
                if (conditionMet) {
                    ChatComponentText successMessage = new ChatComponentText("Target Server Found " + targetServer);
                    mc.thePlayer.addChatMessage(successMessage);
                    stopLoop(); // Stop the sequence if the server matches
                } else {
                    ChatComponentText chatMessage = new ChatComponentText("Server Doesn't Match - Target: " + targetServer+ " Actual: " + serverName+ " Warping home...");
                    mc.thePlayer.addChatMessage(chatMessage);
                    mc.thePlayer.sendChatMessage("/warp home"); // Warp home if the server doesn't match

                    // Wait another 5 seconds before restarting the sequence
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ChatComponentText retryMessage = new ChatComponentText("Retrying sequence...");
                            mc.thePlayer.addChatMessage(retryMessage);
                            executeCommandSequence(sender); // Restart the sequence
                        }
                    }, 5000); // Wait 5 seconds after /warp home
                }
            }
        }, 5000); // Wait 5 seconds after /warp ch
    }


    private void stopLoop() {
        // Unregister the event and stop the loop
//        MinecraftForge.EVENT_BUS.unregister(this);
        conditionMet = true;
    }


    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        // This event is triggered whenever a chat message is received
        System.out.println("Chat message received: " + event.message.getUnformattedText());
//        serverName = "NONE";
        String message = event.message.getUnformattedText();
        System.out.println("CHC MESSAGE: "+ message);

        if (message.contains("Sending to server")) {
            // Extract the server name using simple string manipulation
            int startIndex = message.indexOf("Sending to server") + "Sending to server ".length();
            int endIndex = message.indexOf("...", startIndex);
            if (endIndex != -1) {
                serverName = message.substring(startIndex, endIndex).trim();
                System.out.println("Found server: " + serverName);

                // Check if the server name matches the target server
                if (serverName.equalsIgnoreCase(targetServer)) {
                    System.out.println("Server matched: " + serverName);
                    stopLoop();
                } else {
                    System.out.println("Server doesn't match. Target: " + targetServer + ", Actual: " + serverName);
                }
            }
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "locate") : null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Allows any player to use the command
    }
}
