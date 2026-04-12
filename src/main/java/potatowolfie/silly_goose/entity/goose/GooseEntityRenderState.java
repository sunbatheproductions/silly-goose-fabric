package potatowolfie.silly_goose.entity.goose;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import potatowolfie.silly_goose.entity.goose.variant.GooseVariant;

@Environment(EnvType.CLIENT)
public class GooseEntityRenderState extends ArmedEntityRenderState {
    @Nullable
    public GooseVariant variant;
    public Identifier babyTexture;

    public final ItemStackRenderState rightHandItemState = new ItemStackRenderState();
    public final ItemStackRenderState leftHandItemState = new ItemStackRenderState();
    public HumanoidArm mainArm = HumanoidArm.RIGHT;
    public ItemStack mainHandStack = ItemStack.EMPTY;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState idleWaterAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState swimFastAnimationState = new AnimationState();
    public final AnimationState wingsUpIdleAnimationState = new AnimationState();

    public final AnimationState babyIdleAnimationState = new AnimationState();
    public final AnimationState babyIdleWaterAnimationState = new AnimationState();
    public final AnimationState babyWalkAnimationState = new AnimationState();
    public final AnimationState babyRunAnimationState = new AnimationState();
    public final AnimationState babySwimAnimationState = new AnimationState();
    public final AnimationState babySwimFastAnimationState = new AnimationState();
    public final AnimationState babyWingsUpIdleAnimationState = new AnimationState();

    public GooseEntityRenderState() {
    }
}