package com.heroku.api.command;

import com.heroku.api.HerokuRequestKey;
import com.heroku.api.HerokuResource;
import com.heroku.api.exception.RequestFailedException;
import com.heroku.api.http.Accept;
import com.heroku.api.http.HttpStatus;
import com.heroku.api.http.HttpUtil;
import com.heroku.api.http.Method;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Javadoc
 *
 * @author Naaman Newbold
 */
public class AppAddonCommand implements Command<JsonArrayResponse> {

    private final CommandConfig config;

    public AppAddonCommand(String appName) {
        config = new CommandConfig().app(appName);
    }
    
    @Override
    public Method getHttpMethod() {
        return Method.GET;
    }

    @Override
    public String getEndpoint() {
        return String.format(HerokuResource.AppAddons.value, config.get(HerokuRequestKey.appName));
    }
    
    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public String getBody() {
        throw HttpUtil.noBody();
    }

    @Override
    public Accept getResponseType() {
        return Accept.JSON;
    }

    @Override
    public Map<String, String> getHeaders() {
        return new HashMap<String, String>();
    }

    @Override
    public JsonArrayResponse getResponse(InputStream inputStream, int status) {
        if (status == HttpStatus.OK.statusCode) {
            return new JsonArrayResponse(inputStream);
        }
        throw new RequestFailedException(
                "Unable to get addons for " + config.get(HerokuRequestKey.appName), status, inputStream);
    }
}
