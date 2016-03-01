package com.bitdubai.sub_app.crypto_broker_community.notifications;

import android.widget.RemoteViews;

import com.bitdubai.fermat_android_api.engine.NotificationPainter;

/**
 * This class contains the basic functionality of the crypto broker community notification painter.
 *
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 29/02/2016.
 *
 * @author lnacosta
 * @version 1.0.0
 */
public class CommunityNotificationPainter implements NotificationPainter {

    private String title;
    private String textBody;
    private String image;
    private RemoteViews remoteViews;

    public CommunityNotificationPainter(final String title   ,
                                        final String textBody,
                                        final String image   ,
                                        final String viewCode){

        this.title    = title   ;
        this.textBody = textBody;
        this.image    = image   ;

    }

    @Override
    public RemoteViews getNotificationView(String code) {
        return this.remoteViews;
    }

    @Override
    public String getNotificationTitle() {
        return this.title;
    }

    @Override
    public String getNotificationImageText() {
        return this.image;
    }

    @Override
    public String getNotificationTextBody() {
        return this.textBody;
    }

    @Override
    public int getIcon() {
        return 0;
    }


}
