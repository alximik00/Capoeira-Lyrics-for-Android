package com.alximik.capoeiralyrics.network;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 7:18 PM
 */
public class NetworkConstants implements ApiConstants {

    @Override
    public String getServerUrl() {
        return "http://capoeiralyrics.info";
    }

    @Override
    public String getSecurityToken() {
        return "CC03921EB31B11E18EC38C3C6188709B";
    }

    @Override
    public String getAppName() {
        return "Capoeira Lyrics";
    }

    @Override
    public String getAppUrl() {
        return "http://capoeiralyrics.info";
    }

    @Override
    public String getFacebookAppId() {
        return "313661285392611";
    }

    @Override
    public String getTwitterConsumerKey() {
        return "vGmskndxnpwyNT8TxxUA";
    }

    @Override
    public String getTwitterSecret() {
        return "YXly6ReWjrGG7bPBx1AQYBT2bDq6rNtkmovWiSJ94";
    }

    @Override
    public int getSmaatoPublisherId(){
        return 923862963;
    }

    @Override
    public int getSmaatoAdSpace() {
        return 65765636;
    }

}
