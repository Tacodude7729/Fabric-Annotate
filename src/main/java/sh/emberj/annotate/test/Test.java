package sh.emberj.annotate.test;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import sh.emberj.annotate.Translation;
import sh.emberj.annotate.core.Annotate;
import sh.emberj.annotate.core.AnnotateScan;
import sh.emberj.annotate.core.LoadStage;
import sh.emberj.annotate.entrypoint.Entrypoint;
import sh.emberj.annotate.mixin.MixinMethodHead;
import sh.emberj.annotate.mixin.MixinMethodTail;

@AnnotateScan
public class Test {

    @Entrypoint
    public static void onInitialize() {
        AnimalRegistry.INSTANCE.get(new Identifier("annotate:pig")).makeNoise();
        AnimalRegistry.INSTANCE.get(new Identifier("annotate:sheep")).makeNoise();
        AnimalRegistry.INSTANCE.get(new Identifier("annotate:piglet")).makeNoise();

        // AnnotateNetServer.registerNativeServerboundPacket(new Identifier("annotate:test_a")).setHandler((data, ctx) -> {
        //     Annotate.LOG.info("Got message from the client " + data.readString());
        // });

        // AnnotateNetClient.registerNativeClientboundPacket(new Identifier("annotate:test_b")).setHandler((data, ctx) -> {
        //     System.out.println(data.readString());
        //     PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        //     buf.writeString("Hello from the client <3");
        //     AnnotateNetClient.sendNativeServerbound(new Identifier("annotate:test_a"), data);
        // });
    }

    @MixinMethodHead(type = IntegratedServer.class)
    public static void tick(IntegratedServer _this, BooleanSupplier shouldKeepTicking) {
        
        if (_this.getPlayerManager().getPlayerList().size() != 0) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeString("Hello, World!");
            // AnnotateNetServer.sendNativeClientbound(_this.getPlayerManager().getPlayerList().get(0), new Identifier("annotate:test_b"), buf);            
        }
    }

    @Translation(key = "singleplayer", value = "Yay!", type = "menu.", namespace = Translation.NO_NAMESPACE)
    @Entrypoint(stage = LoadStage.PRELAUNCH)
    public static void onInit0() {
        Annotate.LOG.info("On init 0!");
    }

    @Entrypoint(stage = LoadStage.PREINIT)
    public static void onInit1() {
        Annotate.LOG.info("On init 1!");
    }

    @Entrypoint(priority = -1)
    public static void onInit2() {
        Annotate.LOG.info("On init 2!");
    }

    @Entrypoint(stage = LoadStage.POSTINIT)
    public static void onInit3() {
        Annotate.LOG.info("On init 3!");
    }

    @MixinMethodHead(type = MixinTarget.class)
    public static void staticOne() {
        Annotate.LOG.info("Static One Mixin!");
    }

    @MixinMethodHead(type = MixinTarget.class)
    public static String staticTwo(String idk, int fbfb, CallbackInfo info) {
        Annotate.LOG.info("Static Two Mixin! Got idk = " + idk + " and fbfb = " + fbfb);
        return "cancelled!";
    }

    @MixinMethodTail(type = MixinTarget.class)
    public static double memberOne(MixinTarget _this, double abcde, CallbackInfo cbi, double returnVal) {
        Annotate.LOG.info("Member One Mixin! " + abcde);
        Annotate.LOG.info("State = " + _this.state);
        Annotate.LOG.info("State = " + cbi.getId());
        Annotate.LOG.info("" + returnVal);
        return 1111;
    }

    @MixinMethodHead(type = TitleScreen.class)
    public static void init() {
        System.out.println("==== TITLE SCREEN MIXIN ====");
        // System.exit(0);
    }
}
