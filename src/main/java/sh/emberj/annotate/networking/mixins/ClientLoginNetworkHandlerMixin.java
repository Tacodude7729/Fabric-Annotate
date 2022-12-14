package sh.emberj.annotate.networking.mixins;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.text.Text;
import sh.emberj.annotate.networking.ServerValidatorRegistry;

@Mixin(ClientLoginNetworkHandler.class)
public abstract class ClientLoginNetworkHandlerMixin {

    @Shadow
    @Final
    public ClientConnection connection;

    @Shadow
    public abstract void onDisconnected(Text reason);

    @Unique
    private AtomicBoolean _hasReceivedInfo;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ClientConnection connection, MinecraftClient client, Screen parentGui,
            Consumer<Text> statusConsumer, CallbackInfo info) {
        _hasReceivedInfo = new AtomicBoolean(false);
    }

    @Inject(method = "onQueryRequest", at = @At("HEAD"), cancellable = true)
    private void onQueryRequest(LoginQueryRequestS2CPacket packet, CallbackInfo info) {
        if (packet.getChannel().equals(ServerValidatorRegistry.ANNOTATE_CHANNEL)) {
            info.cancel();

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(true);

            this.connection.send(new LoginQueryResponseC2SPacket(packet.getQueryId(), buf));
        }
    }

    @Inject(method = "onSuccess", at = @At("HEAD"))
    public void onSuccess(LoginSuccessS2CPacket packet, CallbackInfo info) {
        if (!_hasReceivedInfo.get()) {
            onDisconnected(Text.of("Cannot connect to server! Server not running the Annotate mod."));
            info.cancel();
        }
    }
}
