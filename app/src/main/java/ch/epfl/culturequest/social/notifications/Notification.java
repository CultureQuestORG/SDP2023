package ch.epfl.culturequest.social.notifications;

import android.content.Context;

/**
 * Interface that represents notifications
 */
interface Notification {

    /**
     * Returns the notification ready to be sent
     *
     * @param context the context of the notification
     * @return the notification
     */
    android.app.Notification get(Context context);
}
