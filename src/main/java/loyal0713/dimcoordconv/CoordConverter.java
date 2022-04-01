package loyal0713.dimcoordconv;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

public class CoordConverter implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("dimcoordconv");
    private static KeyBinding keyBinding;

    @Override
    public void onInitialize() {
        // add key listener
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Display converted coords",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "Coordinate Converter"
        ));

        ConfigBuilder builder = ConfigBuilder.create();

        // event to register
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                // can sometimes be null
                Identifier world;
                try {
                    world = client.world.getRegistryKey().getValue();
                } catch (NullPointerException e) {
                    LOGGER.error(e);
                    return;
                }

                // no end conversion
                if (world.equals(World.END.getValue())) return;

                // get player coordinates and rotation
                int playerX, playerY, playerZ;
                double playerRot = (client.player.headYaw + 180) % 360;
                LOGGER.info(playerRot);

                try {
                    playerX = (int)client.player.getX();
                    playerY = (int)client.player.getY();
                    playerZ = (int)client.player.getZ();
                } catch (NullPointerException e) {
                    LOGGER.error(e);
                    return;
                }

                // convert coords
                StringBuilder message = new StringBuilder();
                if (world.equals(World.OVERWORLD.getValue())) {
                        int translatedX = playerX / 8;
                        int translatedZ = playerZ / 8;
                        message.append(String.format("Nether: %d, %d, %d, ", translatedX, playerY, translatedZ));
                } else {    // in nether
                    int translatedX = playerX * 8;
                    int translatedZ = playerZ * 8;
                    message.append(String.format("Overworld: %d, %d, %d, ", translatedX, playerY , translatedZ));
                }

                // convert rotation to heading
                // facing right side of compass
                if(45 <= playerRot && playerRot < 135) message.append("east");
                else if(135 <= playerRot && playerRot < 225) message.append("south");
                else if(225 <= playerRot && playerRot < 315) message.append("west");
                else message.append("north");

                // tell player translated coords and direction
                client.player.sendMessage(new LiteralText(message.toString()), false);
            }
        });
    }
}
