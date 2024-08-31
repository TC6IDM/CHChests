package com.example.CHChests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.util.parsing.json.JSONObject;

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
                // Send /locraw after 5 seconds
                mc.thePlayer.sendChatMessage("/locraw");

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Check if the condition is met after /locraw, if not continue with /warp home
                        if (!conditionMet) {
                            ChatComponentText chatMessage = new ChatComponentText("Server Doesn't Match - Target: " + targetServer+ " Actual: " + serverName+ " Warping home");
                            mc.thePlayer.addChatMessage(chatMessage);
                            mc.thePlayer.sendChatMessage("/warp home");

                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // After 5 seconds, start the sequence again with /warp ch
                                    executeCommandSequence(sender);
                                }
                            }, 5000);
                        } else {
                            // Condition met, stop the loop
                            ChatComponentText chatMessage = new ChatComponentText("Found Server " + targetServer);
                            mc.thePlayer.addChatMessage(chatMessage);
                            stopLoop();
                        }
                    }
                }, 5000);
            }
        }, 5000);

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
        serverName = "";
        String message = event.message.getUnformattedText();
        System.out.println("CHC MESSAGE: "+ message);

        // Extract the JSON part of the message
        int jsonStartIndex = message.indexOf("{");
        if (jsonStartIndex != -1) {
            System.out.println("CHC index: "+ jsonStartIndex);
            String jsonPart = message.substring(jsonStartIndex);

            // Parse the extracted JSON string
            try {
                JsonParser parser = new JsonParser();
                JsonObject jsonMessage = parser.parse(jsonPart).getAsJsonObject();
                System.out.println("CHC json: "+ jsonMessage);

                // Check if the JSON message has a "server" field
                if (jsonMessage.has("server")) {
                    serverName = jsonMessage.get("server").getAsString();
                    System.out.println("found server: "+serverName );
                    System.out.println("target server: "+targetServer);
                    // Check if the server name matches the target server
                    if (serverName.equalsIgnoreCase(targetServer)) {
                        System.out.println("server equal");
                        stopLoop();
                    }
                }
            } catch (Exception e) {
                // If there's an error parsing the message, ignore it
                e.printStackTrace();
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
