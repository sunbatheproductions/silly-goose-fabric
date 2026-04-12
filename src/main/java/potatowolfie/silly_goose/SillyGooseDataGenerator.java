package potatowolfie.silly_goose;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import potatowolfie.silly_goose.datagen.*;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariants;
import potatowolfie.silly_goose.registry.SillyGooseRegistryKeys;

public class SillyGooseDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(SillyGooseItemTagProvider::new);
		pack.addProvider(SillyGooseModelProvider::new);
		pack.addProvider(SillyGooseRecipeGenerator::new);
		pack.addProvider(SillyGooseRegistryDataGenerator::new);
		pack.addProvider(SillyGooseWorldGenerator::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(SillyGooseRegistryKeys.GOOSE_VARIANT, GooseVariants::bootstrap);
	}
}