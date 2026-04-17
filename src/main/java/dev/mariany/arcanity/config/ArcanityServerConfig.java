package dev.mariany.arcanity.config;

public class ArcanityServerConfig {
    public ArcaneTable arcaneTable = new ArcaneTable();
    public ArcaneTool arcaneTool = new ArcaneTool();

    public static class ArcaneTable {
        public boolean treasureEnchantments = true;

        public SpawnRates spawnRates = new SpawnRates();

        public static class SpawnRates {
            public float bastionTreasure = 1;
        }
    }

    public static class ArcaneTool {
        public boolean fromVillager = true;

        public SpawnRates spawnRates = new SpawnRates();

        public static class SpawnRates {
            public float endCityTreasure = 0.03F;
        }
    }
}
