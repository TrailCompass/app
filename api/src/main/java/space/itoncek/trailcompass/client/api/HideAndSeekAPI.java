package space.itoncek.trailcompass.client.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class HideAndSeekAPI {
    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();

    public abstract HideAndSeekConfig getConfig();
    public abstract void saveConfig(HideAndSeekConfig cfg);

    public ServerValidity checkValidity() throws IOException {
        Response response = get(getConfig().base_url + "/");
        boolean successful = response.isSuccessful();
        if(!successful) return ServerValidity.NOT_FOUND;
        assert response.body() != null;
        String version = response.body().string();
        response.close();
        if(version.equals("vDEVELOPMENT")) return ServerValidity.DEVELOPMENT_VERSION;
        else return SERVER_VERSIONS.SERVER_VERSION.equals(version)?ServerValidity.OK:ServerValidity.INCOMPATIBLE_VERSION;
    }

    public long getPing() throws IOException {
        Request request = new Request.Builder()
                .url(getConfig().base_url+"/time")
                .build();

        long start = System.currentTimeMillis();
        Response execute = client.newCall(request).execute();
        long end = System.currentTimeMillis();

        assert execute.body() != null;
        long mid = Long.parseLong(execute.body().string());
        execute.close();
        return ((mid-start) + (end-mid))/2L;
    }

    public LoginResponse login() throws IOException {
        HideAndSeekConfig cfg = getConfig();

        ServerValidity serverValidity = checkValidity();
        if(serverValidity == ServerValidity.NOT_FOUND) return LoginResponse.UNABLE_TO_CONNECT;
        if(serverValidity.equals(ServerValidity.INCOMPATIBLE_VERSION)) return LoginResponse.INCOMPATIBLE_BACKEND;

        String req = new JSONObject()
                .put("username", cfg.username)
                .put("passwordhash", cfg.passwordHash)
                .toString(4);

        Response res = post(cfg.base_url + "/uac/login", req);

        if(!res.isSuccessful()) return LoginResponse.UNABLE_TO_AUTH;

        try {
            assert res.body() != null;
            cfg.jwt_token = new JSONObject(res.body().string()).getString("token");
            saveConfig(cfg);
        } catch (IOException | JSONException e) {
            return LoginResponse.UNABLE_TO_AUTH;
        }
        res.close();
        return LoginResponse.OK;
    }

    public Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return client.newCall(request).execute();
    }

    public Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return client.newCall(request).execute();
    }

    public Response postAuthd(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + getConfig().jwt_token)
                .post(body)
                .build();

        return client.newCall(request).execute();
    }
}