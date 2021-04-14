package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CalculateRootsService extends IntentService
{


    public CalculateRootsService() {
        super("CalculateRootsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        long timeStartMs = System.currentTimeMillis();
        long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
        if (numberToCalculateRootsFor <= 0) {
            Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
            return;
        }

        Intent broadcast = new Intent();
        long r1 = numberToCalculateRootsFor, r2 = 1, timePassed = 0;
        for (long i = 2; i < (long) Math.sqrt(numberToCalculateRootsFor); i++) {
            timePassed = System.currentTimeMillis() - timeStartMs;
            if (timePassed >= 20000L) {
                broadcast.setAction("stopped_calculations");
                broadcast.putExtra("original_number", numberToCalculateRootsFor);
                broadcast.putExtra("time_until_give_up_seconds", timePassed / 1000L); // send time passed as seconds
                sendBroadcast(broadcast);
                return;
            }
            else if (numberToCalculateRootsFor % i == 0) // found a root factor
            {
                r1 = i;
                r2 = numberToCalculateRootsFor / i;
                break;
            }
        }
        // either prime number or found roots before 20s passed
        broadcast.setAction("found_roots");
        broadcast.putExtra("original_number", numberToCalculateRootsFor);
        broadcast.putExtra("root1", r1);
        broadcast.putExtra("root2", r2);
        broadcast.putExtra("calculation_time", timePassed / 1000L); // send time passed as seconds
        sendBroadcast(broadcast);

    /*
     calculate the roots.
     check the time (using `System.currentTimeMillis()`) and stop calculations if can't find an answer after 20 seconds
     upon success (found a root, or found that the input number is prime):
      send broadcast with action "found_roots" and with extras:
       - "original_number"(long)
       - "root1"(long)
       - "root2"(long)
     upon failure (giving up after 20 seconds without an answer):
      send broadcast with action "stopped_calculations" and with extras:
       - "original_number"(long)
       - "time_until_give_up_seconds"(long) the time we tried calculating

      examples:
       for input "33", roots are (3, 11)
       for input "30", roots can be (3, 10) or (2, 15) or other options
       for input "17", roots are (17, 1)
       for input "829851628752296034247307144300617649465159", after 20 seconds give up

     */
    }
}