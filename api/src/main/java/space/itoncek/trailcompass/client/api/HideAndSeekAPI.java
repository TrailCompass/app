package space.itoncek.trailcompass.client.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

@SuppressWarnings("unused")
public abstract class HideAndSeekAPI {
    public static final MediaType JSON = MediaType.get("application/json");

    final OkHttpClient client = new OkHttpClient();
    private WebSocket ws;

    public abstract void sendLogMessage(String message);

    public abstract void sendExceptionMessage(String message, Throwable t);

    public abstract HideAndSeekConfig getConfig();

    public abstract void saveConfig(HideAndSeekConfig cfg);

    public ServerValidity checkValidity() throws IOException {
        Response response = get(getConfig().base_url + "/");
        if (response == null || !response.isSuccessful()) return ServerValidity.NOT_FOUND;

        assert response.body() != null;
        String version = response.body().string();
        response.close();
        if (version.equals("vDEVELOPMENT")) return ServerValidity.DEVELOPMENT_VERSION;
        else
            return SERVER_VERSIONS.SERVER_VERSION.equals(version) ? ServerValidity.OK : ServerValidity.INCOMPATIBLE_VERSION;
    }

    public long getPing() throws IOException {
        Request request = new Request.Builder()
                .url(getConfig().base_url + "/time")
                .build();

        long start = System.currentTimeMillis();
        Response execute = client.newCall(request).execute();
        long end = System.currentTimeMillis();

        assert execute.body() != null;
        long mid = Long.parseLong(execute.body().string());
        execute.close();
        return ((mid - start) + (end - mid)) / 2L;
    }

    public LoginResponse login() throws IOException, JSONException {
        HideAndSeekConfig cfg = getConfig();

        ServerValidity serverValidity = checkValidity();
        if (serverValidity == ServerValidity.NOT_FOUND) return LoginResponse.UNABLE_TO_CONNECT;
        if (serverValidity.equals(ServerValidity.INCOMPATIBLE_VERSION))
            return LoginResponse.INCOMPATIBLE_BACKEND;

        String req = new JSONObject()
                .put("username", cfg.username)
                .put("passwordhash", cfg.password_hash)
                .toString(4);

        Response res = post(cfg.base_url + "/uac/login", req);

        try (res) {
            if (!res.isSuccessful()) return LoginResponse.UNABLE_TO_AUTH;
            assert res.body() != null;
            cfg.jwt_token = new JSONObject(res.body().string()).getString("token");
            saveConfig(cfg);
        } catch (IOException | JSONException e) {
            sendExceptionMessage("Unable to auth", e);
            return LoginResponse.UNABLE_TO_AUTH;
        }
        return LoginResponse.OK;
    }

    public String getMapHash() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response authd = getAuthd(cfg.base_url + "/mapserver/getServerMapHash")) {
            assert (authd != null ? authd.body() : null) != null && authd.isSuccessful();
            return authd.body().string();
        }
    }

    public String getMapTheme() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response authd = getAuthd(cfg.base_url + "/mapserver/getServerMapTheme")) {
            assert (authd != null ? authd.body() : null) != null && authd.isSuccessful();
            return authd.body().string();
        }
    }

    public boolean amIAdmin() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response authd = getAuthd(cfg.base_url + "/uac/amIAdmin")) {
            assert (authd != null ? authd.body() : null) != null && authd.isSuccessful();
            return Boolean.parseBoolean(authd.body().string());
        }
    }

    public boolean amIHider() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response authd = getAuthd(cfg.base_url + "/uac/amIHider")) {
            assert (authd != null ? authd.body() : null) != null && authd.isSuccessful();
            return Boolean.parseBoolean(authd.body().string());
        }
    }

    public @Nullable String myName() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response authd = getAuthd(cfg.base_url + "/uac/myName")) {
            assert (authd != null ? authd.body() : null) != null && authd.isSuccessful();
            return authd.body().string();
        }
    }


    public @Nullable JSONObject getCurrentHider() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response res = getAuthd(cfg.base_url + "/gamemanager/currentHider")) {
            if ((res != null ? res.body() : null) == null || !res.isSuccessful()) return null;
            return new JSONObject(res.body().string());
        }
    }

    public GameState getGameState() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response res = getAuthd(cfg.base_url + "/gamemanager/gameState")) {
            assert (res != null ? res.body() : null) != null && res.isSuccessful();
            return GameState.valueOf(res.body().string());
        }
    }

    public boolean startGame() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response res = postAuthd(cfg.base_url + "/gamemanager/finishSetup", "{}")) {
            assert (res != null ? res.body() : null) != null;
            return res.isSuccessful();
        }
    }

    public ZonedDateTime getGameStartTime() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response res = getAuthd(cfg.base_url + "/gamemanager/startTime")) {
            assert (res != null ? res.body() : null) != null && res.isSuccessful();
            return ZonedDateTime.parse(res.body().string());
        }
    }

    public boolean cycleHider() throws IOException {
        HideAndSeekConfig cfg = getConfig();
        try (Response res = postAuthd(cfg.base_url + "/gamemanager/cycleHider", "{}")) {
            assert (res != null ? res.body() : null) != null;
            return res.isSuccessful();
        }
    }

    public @Nullable Response get(String url) throws IOException {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            return client.newCall(request).execute();
        } catch (IllegalArgumentException e) {
            sendLogMessage("Unable to parse the URL!");
            return null;
        }
    }

    public @Nullable Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return client.newCall(request).execute();
    }

    public @Nullable Response postAuthd(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + getConfig().jwt_token)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.code() == 418) {
            try {
                if (Objects.requireNonNull(login()) == LoginResponse.OK) {
                    return getAuthd(url);
                }
                return null;
            } catch (JSONException e) {
                return null;
            }
        } else {
            return response;
        }
    }

    public @Nullable Response getAuthd(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + getConfig().jwt_token)
                .build();

        Response response = client.newCall(request).execute();

        if (response.code() == 418) {
            System.out.println("Refreshing tokens!");
            try {
                if (Objects.requireNonNull(login()) == LoginResponse.OK) {
                    return getAuthd(url);
                }
                return null;
            } catch (JSONException e) {
                return null;
            }
        } else {
            return response;
        }
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