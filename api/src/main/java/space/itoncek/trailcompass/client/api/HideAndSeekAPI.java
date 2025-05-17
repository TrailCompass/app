package space.itoncek.trailcompass.client.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import space.itoncek.trailcompass.api.ExchangeHandler;
import space.itoncek.trailcompass.commons.objects.GameState;
import space.itoncek.trailcompass.commons.objects.Token;
import space.itoncek.trailcompass.commons.requests.auth.LoginRequest;
import space.itoncek.trailcompass.commons.requests.auth.ProfileRequest;
import space.itoncek.trailcompass.commons.requests.gamemgr.CurrentHiderRequest;
import space.itoncek.trailcompass.commons.requests.gamemgr.CycleCurrentHiderRequest;
import space.itoncek.trailcompass.commons.requests.gamemgr.FinishSetupRequest;
import space.itoncek.trailcompass.commons.requests.gamemgr.GameStateRequest;
import space.itoncek.trailcompass.commons.requests.gamemgr.StartingTimeRequest;
import space.itoncek.trailcompass.commons.requests.map.MapHashRequest;
import space.itoncek.trailcompass.commons.requests.system.ServerTimeRequest;
import space.itoncek.trailcompass.commons.requests.system.ServerVersionRequest;
import space.itoncek.trailcompass.commons.responses.generic.OkResponse;
import space.itoncek.trailcompass.commons.responses.system.ServerTimeResponse;
import space.itoncek.trailcompass.commons.responses.system.ServerVersionResponse;
import space.itoncek.trailcompass.commons.utils.BackendException;

@SuppressWarnings("unused")
public abstract class HideAndSeekAPI {
    public static final MediaType JSON = MediaType.get("application/json");

    final OkHttpClient client = new OkHttpClient();
    private final ExchangeHandler ex;
    private WebSocket ws;

    public abstract void sendLogMessage(String message);

    public abstract void sendExceptionMessage(String message, Throwable t);

    public abstract HideAndSeekConfig getConfig();

    public abstract void saveConfig(HideAndSeekConfig cfg);

    public HideAndSeekAPI() {
        ex = new ExchangeHandler(getConfig().base_url + "/");
    }

    public ServerValidity checkValidity() throws BackendException {
        ServerVersionResponse v = ex.system().version(new ServerVersionRequest());

        if (v.ver().equals("vDEVELOPMENT")) return ServerValidity.DEVELOPMENT_VERSION;
        else
            return SERVER_VERSIONS.SERVER_VERSION.equals(v.ver()) ? ServerValidity.OK : ServerValidity.INCOMPATIBLE_VERSION;
    }

    public long getPing() throws IOException, BackendException {

        long start = System.currentTimeMillis();
        ServerTimeResponse res = ex.system().time(new ServerTimeRequest(start));
        long end = System.currentTimeMillis();

        return ((res.mid() - res.start()) + (end - res.mid())) / 2L;
    }

    public LoginResponse login() {
        try {
            HideAndSeekConfig cfg = getConfig();

            ServerValidity serverValidity = checkValidity();
            if (serverValidity == ServerValidity.NOT_FOUND) return LoginResponse.UNABLE_TO_CONNECT;
            if (serverValidity.equals(ServerValidity.INCOMPATIBLE_VERSION))
                return LoginResponse.INCOMPATIBLE_BACKEND;

            space.itoncek.trailcompass.commons.responses.auth.LoginResponse res = ex.auth().login(new LoginRequest(cfg.username, cfg.password_hash.getBytes(StandardCharsets.UTF_8)));

            cfg.jwt_token = res.token();
            saveConfig(cfg);
        } catch (BackendException e) {
            sendExceptionMessage("Unable to auth", e);
            return LoginResponse.UNABLE_TO_AUTH;
        }
        return LoginResponse.OK;
    }

    public String getMapHash() throws BackendException {
        HideAndSeekConfig cfg = getConfig();
        return ex.map().getMapHash(new MapHashRequest(getToken(cfg))).sha256();
    }

    private Token getToken(HideAndSeekConfig cfg) {
        return new Token(cfg.jwt_token);
    }

    public boolean amIAdmin() throws BackendException {
        HideAndSeekConfig cfg = getConfig();
        return ex.auth().getProfile(new ProfileRequest(getToken(cfg))).p().admin();
    }

    public @Nullable String myName() throws BackendException {
        HideAndSeekConfig cfg = getConfig();
        return ex.auth().getProfile(new ProfileRequest(getToken(cfg))).p().nickname();
    }

    public @Nullable UUID getCurrentHider() throws BackendException {
        HideAndSeekConfig cfg = getConfig();

        return ex.gameMgr().getCurrentHider(new CurrentHiderRequest(getToken(cfg))).id();
    }

    public GameState getGameState() throws BackendException {
        HideAndSeekConfig cfg = getConfig();
        return ex.gameMgr().getGameState(new GameStateRequest(getToken(cfg))).state();
    }

    public boolean startGame() throws BackendException {
        HideAndSeekConfig cfg = getConfig();
        return ex.gameMgr().finishSetup(new FinishSetupRequest(getToken(cfg))).equals(new OkResponse());
    }

    public ZonedDateTime getGameStartTime() throws BackendException {
        HideAndSeekConfig cfg = getConfig();
        return ex.gameMgr().getStartingTime(new StartingTimeRequest(getToken(cfg))).dateTime();
    }

    public boolean cycleHider() throws BackendException {
        HideAndSeekConfig cfg = getConfig();
        return ex.gameMgr().cycleCurrentHider(new CycleCurrentHiderRequest(getToken(cfg))).equals(new OkResponse());
    }

    public void startWebsocketLoop(Runnable runnable) {
        Request request = new Request.Builder().url(getConfig().base_url.replace("http://", "ws://") + "/gamemanager/await").build();
        AwaitWebsocketClient listener = new AwaitWebsocketClient() {
            @Override
            public void update() {
                runnable.run();
            }
        };
        ws = client.newWebSocket(request, listener);
        ws.send("logon");
    }

    public void stopWebsocketLoop() {
        ws.close(1000, "Shutdown");
    }


    public static abstract class AwaitWebsocketClient extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        public abstract void update();

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            System.out.println("open");
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            System.out.println("update");
            update();
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            System.out.println("update");
            update();
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            System.out.println("Closing : " + code + " / " + reason);
        }

        @SuppressWarnings("CallToPrintStackTrace")
        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
            t.printStackTrace();
        }
    }
}