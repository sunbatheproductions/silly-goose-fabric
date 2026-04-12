package old.silly_goose.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

@Mixin(SplashManager.class)
public class SplashTextMixin {

    @Shadow
    private List<Component> splashes;

    @Inject(method = "apply*",
            at = @At("TAIL"))
    private void addGooseSplashes(CallbackInfo ci) {
        splashes = new ArrayList<>(splashes);

        Style splashStyle = Style.EMPTY.withColor(0xFFFF00);

        splashes.add(Component.translatable("splash.silly-goose.honk").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.silly_goose").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.define").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.quack").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.duck").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.57").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.goose_overlords").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.untitled_goose_game").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.no_geese_allowed").setStyle(splashStyle));
        splashes.add(Component.translatable("splash.silly-goose.honking").setStyle(splashStyle));
    }
}