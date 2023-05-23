package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.options.KeyBindingOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.PrintWriter;


@Mixin(GameOptions.class)
public abstract class MixinGameOptions {
    @Shadow
    @Final
    public KeyBinding keyForward;

    @Shadow
    @Final
    public KeyBinding keyBack;

    @Shadow
    @Final
    public KeyBinding keyLeft;

    @Shadow
    @Final
    public KeyBinding keyRight;

    @Shadow
    @Final
    public KeyBinding keyJump;

    @Shadow
    @Final
    public KeyBinding keySneak;

    @Shadow
    @Final
    public KeyBinding keySprint;

    @Shadow
    @Final
    public KeyBinding keyAttack;

    @Shadow
    @Final
    public KeyBinding keyChat;

    @Shadow
    @Final
    public KeyBinding keyPlayerList;

    @Shadow
    @Final
    public KeyBinding keyCommand;

    @Shadow
    @Final
    public KeyBinding keyTogglePerspective;

    @Shadow
    @Final
    public KeyBinding keySmoothCamera;

    @Shadow
    @Final
    public KeyBinding[] keysAll;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject$init(MinecraftClient client, File optionsFile, CallbackInfo ci) {
        KeyBindingOptions.read(this.keysAll);
        setKeybindProperties();
    }

    private void setKeybindProperties() {
        final KeyBinding[] keyBindings = {keyForward, keyBack, keyLeft, keyRight, keyJump, keySneak,
                keySprint, keyAttack, keyChat, keyPlayerList, keyCommand, keyTogglePerspective, keySmoothCamera};
        for (final KeyBinding binding : keyBindings)
            ((IKeyBinding) binding).setKeyConflictContext(KeyConflictContext.IN_GAME);
    }

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;println(Ljava/lang/String;)V", ordinal = 71))
    private void redirect$write$trap(PrintWriter instance, String x) {
        //Trap
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setBoundKey(Lnet/minecraft/client/util/InputUtil$Key;)V"))
    private void redirect$load$trap(KeyBinding instance, InputUtil.Key boundKey) {
        //Trap
    }

    @Inject(method = "write", at = @At("HEAD"))
    private void inject$write(CallbackInfo ci) {
        try {
            KeyBindingOptions.write(this.keysAll);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
