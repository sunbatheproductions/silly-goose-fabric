package potatowolfie.silly_goose.entity.goose.goals;

import net.minecraft.world.entity.ai.goal.PanicGoal;
import potatowolfie.silly_goose.entity.goose.GooseEntity;
import potatowolfie.silly_goose.sound.SillyGooseSounds;

public class GoosePanicGoal extends PanicGoal {
    private final GooseEntity goose;
    private int honkTimer = 0;

    public GoosePanicGoal(GooseEntity goose, double speedModifier) {
        super(goose, speedModifier);
        this.goose = goose;
    }

    @Override
    public void start() {
        super.start();
        this.goose.setInHitAndRunMode(true);
        this.playPanicHonk();
    }

    @Override
    public void tick() {
        super.tick();

        if (--this.honkTimer <= 0) {
            this.playPanicHonk();
            this.honkTimer = 15 + this.goose.getRandom().nextInt(15);
        }
    }

    private void playPanicHonk() {
        float pitch = this.goose.isBaby() ? 1.5F : 1.0F;
        this.goose.playSound(SillyGooseSounds.GOOSE_HONK, 1.0F, pitch + (this.goose.getRandom().nextFloat() * 0.2F));
    }

    @Override
    public void stop() {
        super.stop();
        this.goose.setInHitAndRunMode(false);
    }
}