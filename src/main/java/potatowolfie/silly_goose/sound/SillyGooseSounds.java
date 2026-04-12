package potatowolfie.silly_goose.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import potatowolfie.silly_goose.SillyGoose;

public class SillyGooseSounds {
    public static final SoundEvent GOOSE_HONK = registerSoundEvent("goose_honk");
    public static final SoundEvent GOOSE_ATTACK = registerSoundEvent("goose_attack");
    public static final SoundEvent GOOSE_DEATH = registerSoundEvent("goose_death");
    public static final SoundEvent GOOSE_HURT = registerSoundEvent("goose_hurt");

    private static SoundEvent registerSoundEvent(String name) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, name),
                SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, name)));
    }

    public static void registerSounds() {
        SillyGoose.LOGGER.info("Registering Honk Sounds for " + SillyGoose.MOD_ID);
    }
}