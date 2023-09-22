package com.nickcoblentz.montoya.authorization;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.sessions.ActionResult;
import burp.api.montoya.http.sessions.SessionHandlingAction;
import burp.api.montoya.http.sessions.SessionHandlingActionData;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction;
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction;
import com.nickcoblentz.montoya.utilities.LogHelper;
import org.json.JSONObject;

public class BurpMontoyaAccessTokenHelper  implements BurpExtension, ProxyResponseHandler, SessionHandlingAction {

    private MontoyaApi _api;
    private String _accessToken="";
    private LogHelper _loghelper;

    private String _urlStartsWith="https://stage.login.example.com/oauth2/";
    private String _urlEndsWith="/v1/token";

    private String _targetURL="https://dashboard.example.com/portal/";

    public void initialize(MontoyaApi api) {
        _api = api;
        _loghelper = LogHelper.GetInstance(api);
        _loghelper.SetLevel(LogHelper.LogLevel.DEBUG);
        _loghelper.Info("Plugin Loading...");
        api.extension().setName("Access Token Helper");
        api.proxy().registerResponseHandler(this);
        api.http().registerSessionHandlingAction(this);
        _loghelper.Info("Plugin Loaded");
    }

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        return ProxyResponseReceivedAction.continueWith(interceptedResponse);
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        if(interceptedResponse.initiatingRequest().url().startsWith(_urlStartsWith) && interceptedResponse.initiatingRequest().url().endsWith(_urlEndsWith))
        {
            if(interceptedResponse.bodyToString().contains("\"access_token\":\""))
            {
                JSONObject bodyJson = new JSONObject(interceptedResponse.bodyToString());
                _loghelper.Debug(bodyJson.toString());
                _accessToken = bodyJson.getString("access_token");
                _loghelper.Debug("Set new access token: "+_accessToken);
            }
        }
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }

    @Override
    public String name() {
        return "Access Token Helper";
    }

    @Override
    public ActionResult performAction(SessionHandlingActionData actionData) {
        HttpRequest request = actionData.request();
        _loghelper.Debug("Session Handling");
        if(!_accessToken.isEmpty())
        {
            _loghelper.Debug("Not Empty");
            if(actionData.request().url().startsWith(_targetURL))
            {
                _loghelper.Debug("Matches URL");
                request = actionData.request().withUpdatedHeader("Authorization","Bearer "+_accessToken);
            }
        }
        return ActionResult.actionResult(request,actionData.annotations());
    }
}