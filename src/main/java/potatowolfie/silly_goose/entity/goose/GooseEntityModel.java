package potatowolfie.silly_goose.entity.goose;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import potatowolfie.silly_goose.animation.BabyGooseAnimations;
import potatowolfie.silly_goose.animation.GooseAnimations;

// Made with Blockbench 5.0.4

@Environment(EnvType.CLIENT)
public class GooseEntityModel extends EntityModel<GooseEntityRenderState> implements ArmedModel {

	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation idleWaterAnimation;
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation runAnimation;
	private final KeyframeAnimation swimAnimation;
	private final KeyframeAnimation swimFastAnimation;
	private final KeyframeAnimation wingsUpIdleAnimation;

	private final KeyframeAnimation babyIdleAnimation;
	private final KeyframeAnimation babyIdleWaterAnimation;
	private final KeyframeAnimation babyWalkAnimation;
	private final KeyframeAnimation babyRunAnimation;
	private final KeyframeAnimation babySwimAnimation;
	private final KeyframeAnimation babySwimFastAnimation;
	private final KeyframeAnimation babyWingsUpIdleAnimation;

	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart wing0;
	private final ModelPart wing1;
	private final ModelPart tail;
	private final ModelPart leg0;
	private final ModelPart leg1;

	public GooseEntityModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = this.body.getChild("head");
		this.wing0 = this.body.getChild("wing0");
		this.wing1 = this.body.getChild("wing1");
		this.tail = this.body.getChild("tail");
		this.leg0 = root.getChild("leg0");
		this.leg1 = root.getChild("leg1");

		this.idleAnimation = GooseAnimations.GOOSE_IDLE.bake(root);
		this.idleWaterAnimation = GooseAnimations.GOOSE_SWIMMING_IDLE.bake(root);
		this.wingsUpIdleAnimation = GooseAnimations.GOOSE_IDLE_WINGS.bake(root);
		this.walkAnimation = GooseAnimations.GOOSE_WALK.bake(root);
		this.runAnimation = GooseAnimations.GOOSE_RUN.bake(root);
		this.swimAnimation = GooseAnimations.GOOSE_SWIM.bake(root);
		this.swimFastAnimation = GooseAnimations.GOOSE_SWIM_FAST.bake(root);

		this.babyIdleAnimation = BabyGooseAnimations.GOOSE_IDLE.bake(root);
		this.babyIdleWaterAnimation = BabyGooseAnimations.GOOSE_SWIMMING_IDLE.bake(root);
		this.babyWingsUpIdleAnimation = BabyGooseAnimations.GOOSE_IDLE_WINGS.bake(root);
		this.babyWalkAnimation = BabyGooseAnimations.GOOSE_WALK.bake(root);
		this.babyRunAnimation = BabyGooseAnimations.GOOSE_RUN.bake(root);
		this.babySwimAnimation = BabyGooseAnimations.GOOSE_SWIM.bake(root);
		this.babySwimFastAnimation = BabyGooseAnimations.GOOSE_SWIM_FAST.bake(root);
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition body = modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 0).addBox(-4.0F, -8.0F, -6.0F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 1.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 18).addBox(-2.0F, -15.5F, -3.0F, 4.0F, 16.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(36, 38).addBox(-2.0F, -13.5F, -5.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, -5.0F));

		PartDefinition wing0 = body.addOrReplaceChild("wing0", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -8.25F, -5.75F));

		PartDefinition wing1 = body.addOrReplaceChild("wing1", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -8.25F, -5.75F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, -3.5F, -1.0F, 0.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.5F, 4.5F));

		PartDefinition leg0 = modelPartData.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(36, 31).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 20.0F, 1.0F));

		PartDefinition leg1 = modelPartData.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(36, 31).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 20.0F, 1.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}
	public static LayerDefinition createBabyBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
						.texOffs(0, 0).addBox(-2.0F, -9.0F, -2.0F, 4.0F, 7.0F, 4.0F) // Main body
						.texOffs(0, 11).addBox(-1.0F, -7.0F, -3.0F, 2.0F, 1.0F, 1.0F), // Small front bit
				PartPose.offset(0.0F, 24.0F, 0.0F));

		body.addOrReplaceChild("wing0", CubeListBuilder.create()
						.texOffs(6, 11).addBox(0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F),
				PartPose.offsetAndRotation(2.0F, -4.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		body.addOrReplaceChild("wing1", CubeListBuilder.create()
						.texOffs(6, 11).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 2.0F),
				PartPose.offsetAndRotation(-2.0F, -4.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		root.addOrReplaceChild("leg0", CubeListBuilder.create()
						.texOffs(12, 11).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F),
				PartPose.offset(1.0F, 22.0F, 0.0F));

		root.addOrReplaceChild("leg1", CubeListBuilder.create()
						.texOffs(12, 11).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 1.0F),
				PartPose.offset(-1.0F, 22.0F, 0.0F));

		body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.ZERO);

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(GooseEntityRenderState gooseEntityRenderState) {
		super.setupAnim(gooseEntityRenderState);
		this.head.yRot = gooseEntityRenderState.yRot * 0.017453292F;
		this.head.xRot = gooseEntityRenderState.xRot * 0.017453292F;

		if (gooseEntityRenderState.isBaby) {
			this.babyIdleAnimation.apply(gooseEntityRenderState.babyIdleAnimationState, gooseEntityRenderState.ageInTicks);
			this.babyIdleWaterAnimation.apply(gooseEntityRenderState.babyIdleWaterAnimationState, gooseEntityRenderState.ageInTicks);
			this.babyWalkAnimation.apply(gooseEntityRenderState.babyWalkAnimationState, gooseEntityRenderState.ageInTicks);
			this.babyRunAnimation.apply(gooseEntityRenderState.babyRunAnimationState, gooseEntityRenderState.ageInTicks);
			this.babySwimAnimation.apply(gooseEntityRenderState.babySwimAnimationState, gooseEntityRenderState.ageInTicks);
			this.babySwimFastAnimation.apply(gooseEntityRenderState.babySwimFastAnimationState, gooseEntityRenderState.ageInTicks);
			this.babyWingsUpIdleAnimation.apply(gooseEntityRenderState.babyWingsUpIdleAnimationState, gooseEntityRenderState.ageInTicks);
		} else {
			this.idleAnimation.apply(gooseEntityRenderState.idleAnimationState, gooseEntityRenderState.ageInTicks);
			this.idleWaterAnimation.apply(gooseEntityRenderState.idleWaterAnimationState, gooseEntityRenderState.ageInTicks);
			this.walkAnimation.apply(gooseEntityRenderState.walkAnimationState, gooseEntityRenderState.ageInTicks);
			this.runAnimation.apply(gooseEntityRenderState.runAnimationState, gooseEntityRenderState.ageInTicks);
			this.swimAnimation.apply(gooseEntityRenderState.swimAnimationState, gooseEntityRenderState.ageInTicks);
			this.swimFastAnimation.apply(gooseEntityRenderState.swimFastAnimationState, gooseEntityRenderState.ageInTicks);
			this.wingsUpIdleAnimation.apply(gooseEntityRenderState.wingsUpIdleAnimationState, gooseEntityRenderState.ageInTicks);
		}
	}

	public ModelPart getBody() {
		return this.body;
	}
	public ModelPart getHead() {
		return this.head;
	}
	public ModelPart getTail() {
		return this.tail;
	}
	public ModelPart getLeg0() {
		return this.leg0;
	}
	public ModelPart getLeg1() {
		return this.leg1;
	}
	public ModelPart getWing0() {
		return this.wing0;
	}
	public ModelPart getWing1() {
		return this.wing1;
	}

	@Override
	public void translateToHand(EntityRenderState state, HumanoidArm arm, PoseStack matrices) {
		this.body.translateAndRotate(matrices);
		this.head.translateAndRotate(matrices);

		ItemStack stack = ItemStack.EMPTY;
		if (state instanceof GooseEntityRenderState gooseState) {
			stack = gooseState.getUseItemStackForArm(arm);
		}

		if (stack.is(ItemTags.SWORDS)) {
			matrices.translate(-0.09, -0.72, 0.155);
			matrices.mulPose(Axis.XP.rotationDegrees(0.0F));
			matrices.mulPose(Axis.YP.rotationDegrees(-100.0F));
			matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
			matrices.scale(0.85F, 0.85F, 0.85F);
		} else if (stack.is(Items.WHEAT) || stack.is(Items.BREAD)){
			matrices.translate(-0.09, -1.25, -0.355);
			matrices.mulPose(Axis.XP.rotationDegrees(170.0F));
			matrices.mulPose(Axis.YP.rotationDegrees(-45.0F));
			matrices.mulPose(Axis.ZP.rotationDegrees(170.0F));
			matrices.scale(0.85F, 0.85F, 0.85F);
		} else {
			matrices.translate(-0.265, -1.25, -0.255);
			matrices.mulPose(Axis.XP.rotationDegrees(170.0F));
			matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
			matrices.mulPose(Axis.ZP.rotationDegrees(170.0F));
			matrices.scale(0.85F, 0.85F, 0.85F);
		}
	}
}