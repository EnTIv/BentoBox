package us.tastybento.bskyblock.generators;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import us.tastybento.bskyblock.BSkyBlock;

public class IslandWorld {

    private static final String MULTIVERSE_SET_GENERATOR = "mv modify set generator ";
    private static final String MULTIVERSE_IMPORT = "mv import ";
    private static final String NETHER = "_nether";
    private static final String THE_END = "_the_end";
    private static final String CREATING = "Creating ";

    private BSkyBlock plugin;
    private static World islandWorld;
    private static World netherWorld;
    private static World endWorld;

    /**
     * Generates the Skyblock worlds.
     */
    public IslandWorld(BSkyBlock plugin) {
        this.plugin = plugin;
        if (plugin.getSettings().isUseOwnGenerator()) {
            // Do nothing
            return;
        }
        if (plugin.getServer().getWorld(plugin.getSettings().getWorldName()) == null) {
            Bukkit.getLogger().info(CREATING + plugin.getName() + "'s Island World...");
        }
        // Create the world if it does not exist
        islandWorld = WorldCreator.name(plugin.getSettings().getWorldName()).type(WorldType.FLAT).environment(World.Environment.NORMAL).generator(new ChunkGeneratorWorld(plugin))
                .createWorld();
        // Make the nether if it does not exist
        if (plugin.getSettings().isNetherGenerate()) {
            if (plugin.getServer().getWorld(plugin.getSettings().getWorldName() + NETHER) == null) {
                Bukkit.getLogger().info(CREATING + plugin.getName() + "'s Nether...");
            }
            if (!plugin.getSettings().isNetherIslands()) {
                netherWorld = WorldCreator.name(plugin.getSettings().getWorldName() + NETHER).type(WorldType.NORMAL).environment(World.Environment.NETHER).createWorld();
            } else {
                netherWorld = WorldCreator.name(plugin.getSettings().getWorldName() + NETHER).type(WorldType.FLAT).generator(new ChunkGeneratorWorld(plugin))
                        .environment(World.Environment.NETHER).createWorld();
            }
        }
        // Make the end if it does not exist
        if (plugin.getSettings().isEndGenerate()) {
            if (plugin.getServer().getWorld(plugin.getSettings().getWorldName() + THE_END) == null) {
                Bukkit.getLogger().info(CREATING + plugin.getName() + "'s End World...");
            }
            if (!plugin.getSettings().isEndIslands()) {
                endWorld = WorldCreator.name(plugin.getSettings().getWorldName() + THE_END).type(WorldType.NORMAL).environment(World.Environment.THE_END).createWorld();
            } else {
                endWorld = WorldCreator.name(plugin.getSettings().getWorldName() + THE_END).type(WorldType.FLAT).generator(new ChunkGeneratorWorld(plugin))
                        .environment(World.Environment.THE_END).createWorld();
            }
        }
        fixMultiverse(plugin);
    }

    private void fixMultiverse(BSkyBlock plugin) {
        // Multiverse configuration
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Core")) {
            Bukkit.getLogger().info("Trying to register generator with Multiverse ");
            try {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                        MULTIVERSE_IMPORT + plugin.getSettings().getWorldName() + " normal -g " + plugin.getName());
                if (!Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                        MULTIVERSE_SET_GENERATOR + plugin.getName() + " " + plugin.getSettings().getWorldName())) {
                    Bukkit.getLogger().severe("Multiverse is out of date! - Upgrade to latest version!");
                }
                if (netherWorld != null && plugin.getSettings().isNetherGenerate() && plugin.getSettings().isNetherIslands()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                            MULTIVERSE_IMPORT + plugin.getSettings().getWorldName() + "_nether nether -g " + plugin.getName());
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                            MULTIVERSE_SET_GENERATOR + plugin.getName() + " " + plugin.getSettings().getWorldName() + NETHER);
                }
                if (endWorld != null && plugin.getSettings().isEndGenerate() && plugin.getSettings().isEndIslands()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                            MULTIVERSE_IMPORT + plugin.getSettings().getWorldName() + "_the_end end -g " + plugin.getName());
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                            MULTIVERSE_SET_GENERATOR + plugin.getName() + " " + plugin.getSettings().getWorldName() + THE_END);
                }
            } catch (Exception e) {
                Bukkit.getLogger().severe("Not successfull! Disabling " + plugin.getName() + "!");
                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            }
        }


    }

    /**
     * @return the islandWorld
     */
    public World getIslandWorld() {
        if (plugin.getSettings().isUseOwnGenerator()) {
            return Bukkit.getServer().getWorld(plugin.getSettings().getWorldName());
        }
        return islandWorld;
    }

    /**
     * @return the netherWorld
     */
    public World getNetherWorld() {
        if (plugin.getSettings().isUseOwnGenerator()) {
            return Bukkit.getServer().getWorld(plugin.getSettings().getWorldName() + NETHER);
        }
        return netherWorld;
    }

    /**
     * @return the endWorld
     */
    public World getEndWorld() {
        if (plugin.getSettings().isUseOwnGenerator()) {
            return Bukkit.getServer().getWorld(plugin.getSettings().getWorldName() + THE_END);
        }
        return endWorld;
    }


}
