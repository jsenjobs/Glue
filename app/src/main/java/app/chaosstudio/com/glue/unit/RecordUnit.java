package app.chaosstudio.com.glue.unit;

import app.chaosstudio.com.glue.greendb.model.Record;

/**
 * Created by jsen on 2018/1/23.
 */

public class RecordUnit {
    private static Record holder;
    public static Record getHolder() {
        return holder;
    }
    public synchronized static void setHolder(Record record) {
        holder = record;
    }


}
