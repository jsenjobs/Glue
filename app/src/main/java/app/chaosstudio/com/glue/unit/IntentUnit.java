package app.chaosstudio.com.glue.unit;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;

/**
 * Created by jsen on 2018/1/22.
 */

public class IntentUnit {


    private static final String INTENT_TYPE_MESSAGE_RFC822 = "message/rfc822";
    public static Intent getEmailIntent(MailTo mailTo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mailTo.getTo() });
        intent.putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
        intent.putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject());
        intent.putExtra(Intent.EXTRA_CC, mailTo.getCc());
        intent.setType(INTENT_TYPE_MESSAGE_RFC822);

        return intent;
    }

    public static void share(Context context, String title, String url) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + url);
        context.startActivity(Intent.createChooser(sharingIntent, ("Share using:")));
    }
}
