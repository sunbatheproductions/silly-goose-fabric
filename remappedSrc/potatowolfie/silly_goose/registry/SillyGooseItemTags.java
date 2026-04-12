package old.silly_goose.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import old.silly_goose.SillyGoose;

public class SillyGooseItemTags {
    public static class Item {
        public static final TagKey<net.minecraft.world.item.Item> GOOSE_EGGS = createTag("goose_eggs");

        private static TagKey<net.minecraft.world.item.Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(SillyGoose.MOD_ID, name));
        }
    }
}