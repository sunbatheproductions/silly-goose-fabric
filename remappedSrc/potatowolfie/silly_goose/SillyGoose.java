package old.silly_goose;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import old.silly_goose.entity.SillyGooseEntities;
import old.silly_goose.entity.goose.GooseEntity;
import old.silly_goose.item.SillyGooseItems;
import old.silly_goose.registry.SillyGooseDataComponentTypes;
import old.silly_goose.registry.SillyGooseRegistryKeys;
import old.silly_goose.registry.SillyGooseTrackedDataHandlerRegistry;
import old.silly_goose.sound.SillyGooseSounds;

import java.util.Random;

public class SillyGoose implements ModInitializer {
	public static final String MOD_ID = "silly-goose";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SillyGooseRegistryKeys.registerModRegistryKeys();
		SillyGooseTrackedDataHandlerRegistry.register();
		SillyGooseDataComponentTypes.register();
		SillyGooseEntities.registerModEntities();
		SillyGooseItems.registerModItems();
		SillyGooseSounds.registerSounds();

		FabricDefaultAttributeRegistry.register(SillyGooseEntities.GOOSE, GooseEntity.createGooseAttributes());
		SpawnPlacements.register(
				SillyGooseEntities.GOOSE,
				SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				GooseEntity::canSpawn
		);
		BiomeModifications.addSpawn(
				BiomeSelectors.foundInOverworld()
						.and(BiomeSelectors.excludeByKey(
								Biomes.OCEAN,
								Biomes.DEEP_OCEAN,
								Biomes.COLD_OCEAN,
								Biomes.DEEP_COLD_OCEAN,
								Biomes.FROZEN_OCEAN,
								Biomes.DEEP_FROZEN_OCEAN,
								Biomes.LUKEWARM_OCEAN,
								Biomes.DEEP_LUKEWARM_OCEAN,
								Biomes.WARM_OCEAN,
								Biomes.DESERT,
								Biomes.BADLANDS,
								Biomes.WOODED_BADLANDS,
								Biomes.ERODED_BADLANDS
						)),
				MobCategory.CREATURE,
				SillyGooseEntities.GOOSE,
				18,
				1,
				3
		);

		Random random = new Random();
		int number = random.nextInt(3);
		if (number == 0) {
			LOGGER.info("HONK HONK");
		} else if (number == 1) {
			LOGGER.info("You're a silly goose!");
		} else if (number == 2) {
			LOGGER.info("Define Goose. (Hint: It isn't 12)");
		}
	}
}