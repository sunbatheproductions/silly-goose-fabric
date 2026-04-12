package potatowolfie.silly_goose.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import potatowolfie.silly_goose.SillyGoose;

public class SillyGooseDamageTypes {
    public static final ResourceKey<DamageType> GOOSE_BOTHER = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose_bother"));
    public static final ResourceKey<DamageType> GOOSE_PECK = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose_peck"));
    public static final ResourceKey<DamageType> GOOSE_HONK = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, "goose_honk"));
}