package reinforcedblocks.reinforcedblocks;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org .bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public final class ReinforcedBlocks extends JavaPlugin implements Listener {
    List<reinforcement> reinforced = new ArrayList<reinforcement>();
    List<Integer> values = new ArrayList<Integer>();


    @Override
    public void onEnable() {
        // Plugin startup logic

        try {
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(this,this);
        System.out.println("Enabled");


    }

    @Override
    public void onDisable() {
        try {
            write();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Plugin shutdown logic
        System.out.println("Disabled");
    }




    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getMaterial().equals(Material.OBSIDIAN)) {
            boolean alreadydone = false;
            for (int i = 0; i < reinforced.size(); i++) {
                if (event.getClickedBlock().getX() == reinforced.get(i).coords[0]&&event.getClickedBlock().getY() == reinforced.get(i).coords[1]&&event.getClickedBlock().getZ() == reinforced.get(i).coords[2]) {
                    alreadydone = true;
                }
            }
            if (!alreadydone) {
                reinforced.add(new reinforcement(new int[]{event.getClickedBlock().getX(),event.getClickedBlock().getY(),event.getClickedBlock().getZ()}, 1000, event.getClickedBlock().getType()));
                event.getPlayer().getInventory().getItemInHand().setAmount(event.getPlayer().getInventory().getItemInHand().getAmount()-1);
            }




        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        System.out.println("Block broken");
        BlockBreakEvent clone = event;
        //event.getBlock().getX();
        for (int i = 0; i < reinforced.size();i++) {
            if (reinforced.get(i).check(clone)) {
                event.setCancelled(true);
                break;
            }
        }





    }




    void write() throws FileNotFoundException {
        try {
            FileWriter writer = new FileWriter(System.getProperty("user.dir")+"\\reinforcement.txt", true);
            for (int i = 0; i < reinforced.size(); i++) {
                writer.write(reinforced.get(i).coords[0]+","+reinforced.get(i).coords[1]+","+reinforced.get(i).coords[2] + "," +reinforced.get(i).breaksleft);
                writer.write("\r\n");   // write new line
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void read() throws IOException, FileNotFoundException {
        FileReader reader = new FileReader(System.getProperty("user.dir")+"\\reinforcement.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String read = null;


        for (int i = 0; true; i++) {
            read = bufferedReader.readLine();
            System.out.println(read);
            if (read == null) {
                break;
            }
            String[] split = read.split(",");
            //System.out.println(Integer.valueOf(split[0]));
            //System.out.println(Integer.valueOf(split[1]));
            //System.out.println(Integer.valueOf(split[2]));
            //System.out.println(Integer.valueOf(split[3]));

            int x = Integer.valueOf(split[0]);
            int y = Integer.valueOf(split[1]);
            int z = Integer.valueOf(split[2]);
            int breaks = Integer.valueOf(split[3]);
            reinforced.add(new reinforcement(new int[]{x,y,z},breaks,Material.OBSIDIAN));
        }
    }



}



class reinforcement {
    Material type = Material.OBSIDIAN;
    int breaksleft = 10;
    int[] coords = {0, 0, 0};
    Location loc = null;

    reinforcement(int[] coords, int breaks, Material material) {
        this.coords = coords.clone();
        breaksleft = breaks;
        type = material;
    }


    boolean check(BlockBreakEvent event) {
        if (event.getBlock().getX() == coords[0] && event.getBlock().getY() == coords[1] && event.getBlock().getZ() == coords[2]) {
            if (breaksleft > 0) {
                breaksleft--;

                event.getPlayer().sendMessage(breaksleft + "");
                event.getPlayer().setGameMode(GameMode.SURVIVAL);

                return true;
            }
        }

        return false;
    }



}